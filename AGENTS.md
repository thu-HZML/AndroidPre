# Repository Guidelines

## Project Structure & Module Organization
This repository is a single-module Android app using Kotlin + Jetpack Compose.
- `app/src/main/java/com/example/firstcompose/`: app source code (`MainActivity.kt`, UI logic, theme code).
- `app/src/main/res/`: Android resources (`values/`, `drawable/`, `mipmap/`, `xml/`).
- `app/src/test/`: local JVM unit tests.
- `app/src/androidTest/`: instrumented tests for device/emulator.
- Root Gradle files: `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`.
- Build output appears in `app/build/`; do not commit generated files.

## Build, Test, and Development Commands
Run from repository root:
- `./gradlew assembleDebug` (or `gradlew.bat assembleDebug` on Windows): build debug APK.
- `./gradlew testDebugUnitTest`: run local unit tests in `app/src/test`.
- `./gradlew connectedDebugAndroidTest`: run instrumented tests on a connected device/emulator.
- `./gradlew lint`: run Android lint checks.
- `./gradlew clean`: remove build artifacts.

## Coding Style & Naming Conventions
- Use Kotlin with 4-space indentation and standard Android Studio formatting.
- Class/Composable names: `UpperCamelCase` (e.g., `TaskItem`, `TodoApp`).
- Functions/variables: `lowerCamelCase` (e.g., `onDelete`, `isCompleted`).
- Keep package names lowercase (`com.example.firstcompose...`).
- Prefer small, focused composables; move reusable UI into separate files under `ui/` as the app grows.

## Testing Guidelines
- Frameworks: JUnit4 for unit tests, AndroidX Test + Espresso/Compose test APIs for instrumented tests.
- Test file naming: `<Subject>Test.kt`.
- Test method naming: descriptive snake/camel style such as `addition_isCorrect` or `showsTaskList_whenTasksExist`.
- Add unit tests for non-UI logic and instrumented tests for critical UI flows.

## Commit & Pull Request Guidelines
No `.git` metadata is available in this workspace snapshot, so historical commit conventions cannot be inferred.
Use this default:
- Commit format: `type(scope): summary` (e.g., `feat(todo): add swipe-to-delete`).
- Keep commits focused and buildable.
- PRs should include: purpose, key changes, test evidence (`testDebugUnitTest`/screenshots for UI), and linked issue (if any).

## Security & Configuration Tips
- Keep `local.properties` machine-specific; never include secrets or API keys in source.
- Manage dependency versions via `gradle/libs.versions.toml`.
