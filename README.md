# Kimsia

App Android educativa gamificada (estilo Duolingo) para aprender Quechua, Aimara y Guaraní.
Kotlin · Jetpack Compose · Room · Supabase · MVVM + Clean Architecture.

## Estructura (paquete `com.example.kimsia`)
- `data/local` — Room: entidades, DAO, base de datos, datos de ejemplo
- `data/remote/dto` — DTOs serializables para Supabase (Fase 2)
- `data/mapper` — DTO → Entity → Domain
- `data/repository` — implementación offline-first
- `domain` — modelos, interfaz de repositorio, casos de uso
- `ui` — tema, componentes, pantallas, navegación
- `di` — contenedor de dependencias manual
- `supabase/schema.sql` — esquema PostgreSQL + RLS

## Estado
Fase 1: estructura, entidades/DTOs, HomeScreen (ruta serpenteante). Lógica de la ruta con tests unitarios.
