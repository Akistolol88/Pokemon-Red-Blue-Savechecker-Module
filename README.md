# Java (Gradle) CLI Template

Welcome to the **1dv610** Java Command-Line Interface (CLI) template. This repository serves as a
clean, pre-configured boilerplate for building robust Java console applications with modern tools
and best practices.

## 🚀 Features

- **Gradle Build:** Wrapper scripts included — no local Gradle installation required.
- **Application Plugin:** `./gradlew run` builds and runs the CLI in one step.
- **Unit Testing:** Pre-configured with [JUnit 5](https://junit.org/junit5/).
- **Code Quality:** [Checkstyle](https://checkstyle.org) (Google Java Style) and
  [PMD](https://pmd.github.io) static analysis, with a custom summary reporter and a build-failing
  quality gate.
- **Build Logic:** Composite build with reusable convention plugins
  (`build-logic/`), keeping `app/build.gradle` minimal.
- **IDE Support:** Pre-configured for Visual Studio Code, including a debug configuration.

---

## 🛠️ Getting Started

### Prerequisites

Ensure you have a **JDK 25** (or compatible) and **Git** installed on your machine. The Gradle
wrapper is included, so no separate Gradle installation is needed.

### Installation & Project Setup

Pick the flow that matches your situation.

#### A. Starting from scratch (no repository yet) — recommended

Use GitHub's built-in template flow — no git commands needed to get a clean, single-commit history:

1. On GitHub, open this template repository and click **Use this template → Create a new repository**.
2. Clone your new repository and move into it:

   ```bash
   git clone <your-newly-created-repository-url>
   cd <your-repository-name>
   ```

3. Build the project (also verifies your JDK setup):

   ```bash
   ./gradlew build
   ```

GitHub gives your new repository its own single commit copied from this template — no shared
history, nothing to merge or squash.

> **Note:** This requires the template repository to have **Template repository** enabled under
> its GitHub Settings → General. If the "Use this template" button isn't available, use flow B
> instead.

#### B. Importing into an existing repository (empty or not)

Use this flow if you already have a repository — e.g. one provisioned by GitHub Classroom — that
you can't or don't want to recreate from a template.

1. Clone your existing repository and move into it:

   ```bash
   git clone <your-existing-repository-url>
   cd <your-repository-name>
   ```

2. If the repository has no commits yet, create an empty initial commit:

   ```bash
   git commit --allow-empty -m "Initial commit"
   ```

   _Note: This step is required for a genuinely empty repository. A branch with zero commits has
   nothing for `--squash` to diff against, so `git pull --squash` silently falls back to a plain
   fast-forward — it imports this template's entire internal commit history unmodified instead of
   collapsing it into one clean commit. An empty commit gives `--squash` a (empty) tree to compare
   against, so it behaves as intended. Skip this step if the repository already has commits (e.g.
   an auto-generated README)._

3. **Pull and squash the boilerplate code** from this template repository into your branch:

   ```bash
   git pull git@github.com:1dv610/java-gradle-cli-template.git main --squash --allow-unrelated-histories
   ```

   _Note: Using `--squash` ensures that the boilerplate's internal development history is collapsed
   into a single, clean starting point in your repository. If your repository already had files
   (e.g. GitHub auto-created a README or `.gitignore`), this will report a conflict on those files —
   resolve it by taking the template's version: `git checkout --theirs <file> && git add <file>`._

4. **Commit the imported files** to finalize the import of the boilerplate:

   ```bash
   git commit -m "Initial commit from boilerplate"
   ```

5. **Build the project** to verify your JDK setup:

   ```bash
   ./gradlew build
   ```

6. **Push the clean boilerplate setup** up to your own GitHub repository:
  
   ```bash
   git push origin main
   ```

---

## 💻 Available Gradle Tasks

### Running the Application

Builds and runs the main console application entry point (`App.java`), optionally passing a name
as the first argument:

```bash
./gradlew run -q
./gradlew run -q --args="Ada Lovelace"
```

_Note: Rename the `se.lnu.cli` package (and `group`/`mainClass` in `app/build.gradle`) to match
your own project when adapting this template._

### Building

Compiles and packages the application:

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

A human-readable test summary is printed to the console after each run and saved to
`app/build/reports/test-summary.log`.

### Code Quality

Run Checkstyle and PMD with the aggregated summary:

```bash
./gradlew checkstyleMain pmdMain printCodeQualitySummary
```

Or run everything — tests and code quality — in one go:

```bash
./gradlew check
```

The build fails if any blocker violations (Checkstyle/PMD priority 1–3) or test failures are
found. HTML reports are generated at `app/build/reports/checkstyle/main.html` and
`app/build/reports/pmd/main.html`.

### Cleaning

```bash
./gradlew clean
```

---

## Using with Visual Studio Code

### Debug Configuration

This template includes one debug configuration: **Gradle: Debug Application**. Set a breakpoint
and press F5, or open the Run and Debug panel (Ctrl+Shift+D) and click start.

### Available Tasks

Quick access to Gradle tasks through the VS Code Tasks panel (Ctrl+Shift+P → "Tasks: Run Task"):

| Task | Description |
| ---- | ----------- |
| `Gradle: run` | Run application |
| `Gradle: run (debug)` | Run with debug agent on port 5005 |
| `Gradle: build` | Build project |
| `Gradle: clean` | Clean build directory |
| `Gradle: check` | Tests + code quality |
| `Gradle: test` | Run all tests |
| `Gradle: code quality summary` | Checkstyle + PMD + summary |

---

## 📁 Project Structure

```text
├── app/                                 # Main application module
│   ├── src/
│   │   ├── main/java/se/lnu/cli/        # Production code
│   │   │   └── App.java
│   │   └── test/java/se/lnu/cli/        # Unit tests, colocated with the code they cover
│   │       └── AppTest.java
│   └── build.gradle                     # App-specific build configuration
├── build-logic/                         # Custom Gradle convention plugins
├── config/                              # Checkstyle and PMD rule sets
├── gradle/                              # Gradle wrapper and version catalog
├── test/                                # Integration and system tests (higher-level / E2E flows)
├── settings.gradle                      # Multi-project configuration
├── gradle.properties                    # Gradle performance settings
└── LICENSE                              # Unlicense (Public Domain dedication)
```

---

## ⚖️ License

This project is released into the public domain under the **Unlicense**. You are free to copy,
modify, publish, and distribute this boilerplate code in any way you see fit without any
restrictions.
