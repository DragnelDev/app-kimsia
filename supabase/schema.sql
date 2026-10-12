-- Esquema Kimsia para Supabase (SQL Editor). Alineado con las entidades Room y los DTOs.
create table languages (
  id bigint generated always as identity primary key,
  code text not null unique check (code in ('qu','ay','gn')),
  name text not null,
  flag_icon_url text,
  is_active boolean not null default true
);
create table units (
  id bigint generated always as identity primary key,
  language_id bigint not null references languages(id) on delete cascade,
  title text not null,
  description text not null default '',
  order_index int not null
);
create table lessons (
  id bigint generated always as identity primary key,
  unit_id bigint not null references units(id) on delete cascade,
  title text not null,
  order_index int not null,
  total_xp int not null default 0
);
create table exercises (
  id bigint generated always as identity primary key,
  lesson_id bigint not null references lessons(id) on delete cascade,
  type text not null check (type in ('multiple_choice','audio_listen','translate','pairs')),
  question_text text not null,
  audio_url text,
  order_index int not null
);
create table exercise_options (
  id bigint generated always as identity primary key,
  exercise_id bigint not null references exercises(id) on delete cascade,
  option_text text not null,
  image_url text,
  is_correct boolean not null default false
);
create table profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text not null,
  avatar_url text,
  streak_days int not null default 0,
  hearts int not null default 5,
  total_xp int not null default 0,
  created_at timestamptz not null default now()
);
create table user_lesson_progress (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references profiles(id) on delete cascade,
  lesson_id bigint not null references lessons(id) on delete cascade,
  completed boolean not null default false,
  score int not null default 0,
  completed_at timestamptz,
  unique (user_id, lesson_id)
);
create table user_streaks (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null unique references profiles(id) on delete cascade,
  last_active_date date,
  current_streak int not null default 0
);

-- RLS: contenido educativo legible por anon y autenticados; datos de usuario solo del propio usuario.
alter table languages enable row level security;
alter table units enable row level security;
alter table lessons enable row level security;
alter table exercises enable row level security;
alter table exercise_options enable row level security;
alter table profiles enable row level security;
alter table user_lesson_progress enable row level security;
alter table user_streaks enable row level security;

create policy "read content" on languages for select to anon, authenticated using (true);
create policy "read content" on units for select to anon, authenticated using (true);
create policy "read content" on lessons for select to anon, authenticated using (true);
create policy "read content" on exercises for select to anon, authenticated using (true);
create policy "read content" on exercise_options for select to anon, authenticated using (true);
create policy "own profile" on profiles for all to authenticated using (id = auth.uid()) with check (id = auth.uid());
create policy "own progress" on user_lesson_progress for all to authenticated using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy "own streak" on user_streaks for all to authenticated using (user_id = auth.uid()) with check (user_id = auth.uid());

-- Fase 2: bucket público para las pronunciaciones (mp3). Sube archivos como `gn/maitei.mp3`.
insert into storage.buckets (id, name, public) values ('audio-lessons', 'audio-lessons', true)
on conflict (id) do nothing;
create policy "public read audio" on storage.objects for select to anon, authenticated
  using (bucket_id = 'audio-lessons');

-- Fase 2: crea el perfil automáticamente al registrarse un usuario (Auth).
create or replace function public.handle_new_user() returns trigger
language plpgsql security definer set search_path = public as $$
begin
  insert into public.profiles (id, username)
  values (new.id, coalesce(new.raw_user_meta_data->>'username', split_part(new.email, '@', 1)));
  return new;
end;
$$;
create trigger on_auth_user_created after insert on auth.users
  for each row execute function public.handle_new_user();
