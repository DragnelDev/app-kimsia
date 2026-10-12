# Kimsia

App Android educativa gamificada (estilo Duolingo) para aprender Quechua, Aimara y Guaraní.
Kotlin · Jetpack Compose · Room · Supabase · Media3 · MVVM + Clean Architecture.

## Configurar Supabase (opcional)
1. Ejecuta `supabase/schema.sql` en el SQL Editor de tu proyecto.
2. Sube los mp3 al bucket `audio-lessons` (ej. `gn/maitei.mp3`) y guarda esa ruta en `exercises.audio_url`.
3. En `local.properties` (no se versiona):
   ```
   supabase.url=https://TU-PROYECTO.supabase.co
   supabase.anonKey=TU_ANON_KEY
   ```
Sin credenciales la app funciona offline con los datos de ejemplo (`SampleData`).

## Estructura (`com.example.kimsia`)
- `audio/` — `AudioPlayerManager` (Media3), `SoundEffectPlayer` (SoundPool)
- `data/local` · `data/remote` · `data/mapper` · `data/repository`
- `domain/` — modelos, interfaces de repositorio, casos de uso
- `ui/home` · `ui/lesson` · `ui/components` · `ui/navigation` · `ui/theme`
- `di/` — contenedor manual

## Estado
- Fase 1: estructura, Room, DTOs, HomeScreen (ruta serpenteante)
- Fase 2: cliente Supabase, repositorios, audio, LessonScreen + LessonViewModel
