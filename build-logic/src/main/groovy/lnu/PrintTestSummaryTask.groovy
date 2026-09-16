package lnu

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject
import groovy.xml.XmlSlurper

/**
 * Test Reporting Task.
 * Parses JUnit XML test results, prints real-time console feedback, and writes a clean,
 * cross-platform log artifact. Purely informational: the {@code test} task itself is the
 * single source of truth for whether the build fails.
 */
abstract class PrintTestSummaryTask extends DefaultTask {

    // Marked as internal to avoid configuration cache issues; resolved manually during execution.
    @Internal
    abstract DirectoryProperty getTestResultsDir()

    @OutputFile
    abstract RegularFileProperty getLogFile()

    // Injects the ProjectLayout service to securely access filesystem scopes during execution.
    @Inject
    abstract ProjectLayout getLayout()

    @TaskAction
    void printSummary() {
        def OK   = "✅"
        def FAIL = "❌"
        def WARN = "⚠️"
        def FILE = "📄"

        def GREEN  = "[32m"
        def YELLOW = "[33m"
        def RED    = "[31m"
        def RESET  = "[0m"

        File dir = testResultsDir.get().asFile
        if (!dir.exists()) {
            println "  ${RED}${FAIL} Test results directory not found: $dir${RESET}"
            return
        }

        Collection<File> xmlFiles = []
        dir.eachFileRecurse { file ->
            if (file.name.startsWith("TEST-") && file.name.endsWith(".xml")) {
                xmlFiles << file
            }
        }

        if (xmlFiles.isEmpty()) {
            println "  ${YELLOW}${WARN}  No test results found in $dir${RESET}"
            return
        }

        boolean isPipeline = System.getenv("CI") != null

        int passedCount = 0
        int failedCount = 0

        File log = logFile.get().asFile
        log.parentFile.mkdirs()

        log.withWriter("UTF-8") { writer ->
            xmlFiles.each { File xmlFile ->
                def xml = new XmlSlurper().parse(xmlFile)

                // Group test cases by class name, since some XML files cover multiple classes.
                def testCasesByClass = xml.testcase.groupBy { tc ->
                    def cName = tc.@classname.text() ?: xml.@name.text() ?: ""
                    // Collapse nested JUnit 5 suites (strip everything after '$').
                    return cName.contains('$') ? cName.substring(0, cName.indexOf('$')) : cName
                }

                testCasesByClass.each { baseClassToken, testCases ->
                    if (!baseClassToken) return // Skip if the class name is missing.

                    def packagePath = baseClassToken.replace('.', '/')

                    // Resolve the source file safely via the Gradle layout.
                    File javaFile = layout.projectDirectory.file("src/test/java/${packagePath}.java").asFile

                    // Fall back to a bare file name if the file isn't found on disk.
                    def displayFileName = javaFile.exists() ? javaFile.name : "${baseClassToken.tokenize('.').last()}.java"

                    // Print the file name once per class group.
                    if (javaFile.exists() && !isPipeline) {
                        def fileUri = javaFile.toURI().toString().replace("file:/", "file:///")
                        println "${FILE} ]8;;${fileUri}\\${displayFileName}]8;;\\"
                    } else {
                        println "${FILE} ${displayFileName}"
                    }
                    writer.println("📄 ${displayFileName}")

                    // Iterate over the tests belonging to this specific class.
                    testCases.each { tc ->
                        def testName = tc.@name.text()
                        def failures = tc.failure
                        def errors = tc.error

                        boolean hasFailed = (failures && failures.size() > 0) || (errors && errors.size() > 0)
                        def msgShort = null
                        if (hasFailed) {
                            def failureNode = failures?.size() > 0 ? failures : errors
                            msgShort = failureNode.text()?.readLines()?.getAt(0)?.trim() ?: "Test failed"
                        }

                        if (hasFailed) {
                            failedCount++
                            println "    ${RED}${FAIL} $testName${RESET}"
                            println "        ${msgShort}"
                            writer.println("    ❌ $testName")
                            if (msgShort) writer.println("        ${msgShort}")
                        } else {
                            passedCount++
                            println "    ${GREEN}${OK} $testName${RESET}"
                            writer.println("    ✅ $testName")
                        }
                    }
                }
            }

            int total = passedCount + failedCount
            int pct = total > 0 ? (int) ((passedCount / (double) total) * 100) : 0
            writer.println("\nTest summary: ✅ $passedCount / $total ($pct%) passed, ❌ $failedCount failed")
        }

        println "\nTest summary: ${GREEN}${OK} $passedCount${RESET}, ${RED}${FAIL} $failedCount${RESET}"
        println "Full summary written to: ${log.absolutePath}"
    }
}
