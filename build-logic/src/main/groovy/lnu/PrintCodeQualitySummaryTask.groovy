package lnu

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import groovy.xml.XmlSlurper

/**
 * Code Quality Gate Task.
 * Parses XML reports from Checkstyle and PMD, aggregates issues per source file,
 * and prints colorized summaries to the console while maintaining a strict UTF-8 log asset.
 *
 * Also guards against a tool silently failing to run (e.g. an invalid ruleset): with
 * {@code ignoreFailures = true} on the Checkstyle/PMD tasks, a broken configuration can
 * produce an empty report instead of a build failure, which would otherwise read as
 * "zero violations found" rather than "analysis never ran".
 */
abstract class PrintCodeQualitySummaryTask extends DefaultTask {

    @InputDirectory
    @Optional
    abstract DirectoryProperty getCheckstyleReportDir()

    @InputDirectory
    @Optional
    abstract DirectoryProperty getPmdReportDir()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    abstract ConfigurableFileCollection getMainSourceFiles()

    @OutputFile
    abstract RegularFileProperty getLogFile()

    @TaskAction
    void printSummary() {
        // Standardized Unicode status indicators (Globally supported via strict UTF-8 enforcement)
        def OK   = "✅"
        def FAIL = "❌"
        def WARN = "⚠️"
        def FILE = "📄"

        // Native ANSI escape sequences for colorized console reporting streams
        def GREEN  = "[32m"
        def YELLOW = "[33m"
        def RED    = "[31m"
        def RESET  = "[0m"

        int totalWarnings = 0
        int totalErrors = 0
        int analyzedFileCount = 0
        int expectedFileCount = mainSourceFiles.files.size()

        File log = logFile.get().asFile
        log.parentFile.mkdirs()

        log.withWriter("UTF-8") { writer ->
            def allFiles = new TreeSet<String>()
            def issuesPerFile = [:].withDefault { [] }

            // 1. PROCESS CHECKSTYLE ARTIFACTS
            if (checkstyleReportDir.isPresent()) {
                File csDir = checkstyleReportDir.get().asFile
                if (csDir.exists()) {
                    csDir.eachFileRecurse { file ->
                        if (file.name.endsWith(".xml")) {
                            def xml = new XmlSlurper().parse(file)
                            xml.file.each { f ->
                                def fileName = f.@name.text()
                                allFiles << fileName
                                f.error.each { e ->
                                    def line = e.@line.text()
                                    def sev  = e.@severity.text()
                                    def msg  = e.@message.text()
                                    issuesPerFile[fileName] << [sev: sev, msg: msg, line: line, type: "Checkstyle"]
                                    if (sev == "warning") totalWarnings++ else totalErrors++
                                }
                            }
                        }
                    }
                }
            }

            // 2. PROCESS PMD ARTIFACTS
            if (pmdReportDir.isPresent()) {
                File pmdDir = pmdReportDir.get().asFile
                if (pmdDir.exists()) {
                    pmdDir.eachFileRecurse { file ->
                        if (file.name.endsWith(".xml")) {
                            def xml = new XmlSlurper().parse(file)
                            xml.file.each { f ->
                                def fileName = f.@name.text()
                                allFiles << fileName
                                f.violation.each { v ->
                                    def line = v.@beginline.text()
                                    def rule = v.@rule.text()
                                    def msg  = v.text().trim()
                                    issuesPerFile[fileName] << [sev: "warning", msg: msg, line: line, type: "PMD:${rule}"]
                                    totalWarnings++
                                }
                            }
                        }
                    }
                }
            }

            analyzedFileCount = allFiles.size()

            // 3. SORT FILES (Defects bubble up to the top)
            def filesWithIssues = allFiles.findAll { issuesPerFile[it].size() > 0 }
            def okFiles         = allFiles.findAll { issuesPerFile[it].size() == 0 }
            def sortedFiles     = filesWithIssues + okFiles

            // 4. GENERATE DETAILED HUMAN & MACHINE LOGS
            sortedFiles.each { fileName ->
                def issues = issuesPerFile[fileName]

                // Static log file artifact composition (Always writes EVERYTHING to the log asset)
                if (issues) {
                    writer.println("${FILE} ${fileName}")
                    issues.each { i ->
                        def symbol = (i.sev == "error") ? FAIL : WARN
                        writer.println("    ${symbol} [${i.type}] line ${i.line}: ${i.msg}")
                    }
                } else {
                    writer.println("${OK} ${fileName}")
                }

                // Synchronized console reporting stream (Only prints Blocker Errors to the terminal)
                if (issues) {
                    // Filter out mild warnings for the console output to prevent terminal noise
                    def blockerIssues = issues.findAll { i -> i.sev == "error" }

                    if (!blockerIssues.isEmpty()) {
                        println "${FILE} ${fileName}"
                        blockerIssues.each { i ->
                            println "    ${RED}${FAIL} [${i.type}] line ${i.line}: ${i.msg}${RESET}"
                        }
                    } else {
                        // If a file ONLY has priority 4-5 warnings, we print it as green/OK in the terminal
                        println "${GREEN}${OK} ${fileName}${RESET}"
                    }
                } else {
                    println "${GREEN}${OK} ${fileName}${RESET}"
                }
            }

            // 5. EVALUATE QUALITY GATE VIOLATIONS BOUNDARY
            println "\nCode Quality summary: ${RED}${FAIL} Blocker Errors (Priority 1-3): $totalErrors${RESET}, ${YELLOW}${WARN}  Mild Warnings (Priority 4-5): $totalWarnings${RESET}"

            // Standardized text-based path tracking matching the test suite reporting pattern
            File logSummaryFile = logFile.get().asFile
            println "Full summary written to: ${logSummaryFile.absolutePath}"

            writer.println("\nCode Quality summary: ${FAIL} Blocker Errors: $totalErrors, ${WARN}  Mild Warnings: $totalWarnings")
        }

        // Coverage Guard: Checkstyle/PMD run with ignoreFailures = true so both tools always
        // complete and contribute to the combined report above. That means a broken ruleset or
        // other configuration error can make a tool silently analyze zero files instead of
        // failing the build — which would otherwise read as "no violations" rather than
        // "analysis didn't run". Treat incomplete coverage as a hard failure.
        if (expectedFileCount > 0 && analyzedFileCount < expectedFileCount) {
            throw new GradleException(
                "Quality gate could not be verified: Checkstyle/PMD reported results for only "
                    + "${analyzedFileCount} of ${expectedFileCount} source file(s). This usually "
                    + "means a tool failed to run — check the checkstyleMain/pmdMain output above "
                    + "for a configuration error — rather than that the code is clean.")
        }

        // Execution Gate Enforcement: Only fail the entire build pipeline if active priority 1-3 breaches exist
        if (totalErrors > 0) {
            throw new GradleException("Quality Gate Breach: $totalErrors fatal error(s) found in static analysis. Build terminated.")
        } else if (totalWarnings > 0) {
            println "${YELLOW}${WARN}  Static analysis completed successfully with style guidelines warnings.${RESET}"
        } else {
            println "${GREEN}${OK} Static analysis compliance verified. No anomalies detected.${RESET}"
        }
    }
}
