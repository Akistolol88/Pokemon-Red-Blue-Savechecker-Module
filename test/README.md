# Integration Tests

This directory holds integration and system-level tests — flows that exercise the packaged
application end-to-end (e.g. running the assembled application via `./gradlew run` or a built
distribution and asserting on its output), as opposed to the unit tests colocated with the
production code in `app/src/test/java` (e.g. `AppTest.java`).

There is no dedicated Gradle source set for this yet. Add one (e.g. an `integrationTest` source
set in `app/build.gradle`) when real tests are added here, and delete this file at that point.
