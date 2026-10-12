# Connect Supabase & Fix Compilation Errors

## Problem Statement
The project currently has compilation errors preventing build and execution:
1. **`MainActivity.kt`**: Contains legacy/scratch test code (`TodoList`, duplicate `MainActivity`, unimported supabase client) conflicting with the clean architecture app entry point (`KimsiaApp`).
2. **Missing Raw Audio Resources**: `SoundEffectPlayer.kt` references `R.raw.sfx_correct` and `R.raw.sfx_wrong`, but the `res/raw` directory and audio files do not exist.
3. **Supabase Configuration**: Supabase is configured via `local.properties` (`supabase.url` and `supabase.anonKey`). We need to document or provide fallback / instructions so Supabase connects properly when credentials are provided in `local.properties`.

## Proposed Changes

### App & UI Components

#### [MODIFY] [MainActivity.kt](file:///C:/Users/RYZEN%207/Documents/Code%20-%20Mac/Android%20Studio/DemoTecno/app/src/main/java/com/example/kimsia/MainActivity.kt)
- Remove the leftover `supabase` global definition, `TodoList` composable, and duplicate `MainActivity` class.
- Keep only the correct implementation launching `KimsiaApp` inside `KimsiaTheme` with edge-to-edge support.

#### [MODIFY] [SoundEffectPlayer.kt](file:///C:/Users/RYZEN%207/Documents/Code%20-%20Mac/Android%20Studio/DemoTecno/app/src/main/java/com/example/kimsia/audio/SoundEffectPlayer.kt)
- Either add dummy/placeholder sound effect raw files (`sfx_correct.mp3`, `sfx_wrong.mp3`) in `res/raw/` or make `SoundEffectPlayer` handle missing raw resources gracefully (e.g. catch resource loading exceptions or conditional check) so the app compiles and runs even if sound files are not present.

### Supabase Integration Check
- Verify `SupabaseProvider.kt`, `AppContainer.kt`, and repositories (`AuthRepositoryImpl`, `LessonRepositoryImpl`, `AudioRepositoryImpl`) correctly handle Supabase client creation and fallback to offline mode (`SampleData` / Room) when `local.properties` lacks Supabase credentials.

## Verification Plan

### Automated Tests
- Run Gradle build: `./gradlew app:assembleDebug` to verify all compilation errors are resolved.
- Run unit tests: `./gradlew app:testDebugUnitTest`.

### Manual Verification
- Verify app builds successfully and starts up correctly.
- Review Supabase connection guide in README or local.properties setup instructions for the user.
