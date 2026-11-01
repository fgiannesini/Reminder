# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.fgiannesini.DictionaryTest"

# Run the application on the JVM (after build)
java -jar build/libs/Reminder-1.0.jar

# Build the GraalVM native image (needs Docker, ~13 min, 8 GB RAM advised)
docker build -t reminder-native:local .
```

The application requires a PostgreSQL database. Set these environment variables before running:

- `POSTGRES_url` — JDBC URL
- `POSTGRES_username`
- `POSTGRES_password`

Integration tests spin up PostgreSQL automatically via TestContainers — no manual DB setup needed for tests.

## Architecture

This is a language-learning flashcard app with a Spring Boot REST backend and a vanilla JS single-page frontend (
`src/main/resources/static/index.html`).

**Package structure:** `com.fgiannesini`

| Layer    | Classes                                                                                 | Role                                                     |
|----------|-----------------------------------------------------------------------------------------|----------------------------------------------------------|
| Domain   | `Word`, `Dictionary`, `Matching`, `RemainingStats`, `SmRepetition`, `RecentWordsWindow` | Core learning logic; no framework dependencies           |
| Storage  | `StorageHandler` (interface), `DatabaseStorageHandler`                                  | Persistence abstraction                                  |
| Web      | `ReminderController`, DTOs, `SpringMain`                                                | REST API and Spring Boot wiring                          |
| Original | `OriginalDictionary`, `CsvOriginalWord`                                                 | Loads `dictionary.csv` at startup for seeding/validation |
| Console  | `Main`                                                                                  | Utility for detecting duplicate words in the CSV         |

**REST API** (`/reminder/word/`):

- `GET /next` — picks next word to study (top 5 candidates: non-mastered, due or unlearned, null `next_review` first)
- `POST /check` — evaluates a translation attempt; returns `MATCHED`, `CLOSED`, or `NOT_MATCHED`
- `GET /remaining` — learning statistics

**Word lifecycle — 3 phases:**

1. **Learning** (`checkedCount < 3`): `MATCHED`/`CLOSED` increments `checkedCount`; `NOT_MATCHED` resets it to 0.
   Reaching 3 triggers graduation: SM-2 starts with the first `apply()` call.
2. **Confirmation / SM-2** (`smRepetitions` 1–7): each answer runs the SM-2 algorithm in `SmRepetition.apply()`.
   Intervals are adaptive (1 day → 6 days → `interval * easeFactor`). Wrong answers (`quality < 3`) reset
   `smRepetitions` to 0 and penalise `easeFactor`.
3. **Mastered** (`smRepetitions >= 8`): word excluded from selection permanently.

**Duplicate words:** on CSV load, each entry is stored twice — `word→translation` and `translation→word` — so both
directions are practiced.

**`RecentWordsWindow`:** sliding window of 10 recently-seen `wordToLearn` values. `next()` filters out candidates whose
`translation` matches any recent `wordToLearn`, preventing showing the inverse of a word seen in the last 10 turns.

**Data model** (`WordDao` JPA entity, PostgreSQL):

- PK: `word` (String)
- `translation`, `checked_count`, `next_review`, `sm_repetitions`, `ease_factor`, `interval_days`
- DDL: `spring.jpa.hibernate.ddl-auto=update`

**Tech stack:** Java 25, Spring Boot 4.x, Spring Data JPA / Hibernate, PostgreSQL, OpenCSV, JUnit 5, TestContainers.

**Build:** Gradle 9.8.2 with Kotlin DSL. Versions centralized in `gradle/libs.versions.toml`. Build cache, parallel
execution, and configuration cache enabled via `gradle.properties`.

**Deployment:** GitHub Actions (`.github/workflows/native.yml`) runs the tests on each push to `master`, then builds the
GraalVM native image (`Dockerfile`) and pushes it to `ghcr.io/<owner>/reminder:latest`. Koyeb runs this Docker image
(port 8000, `POSTGRES_*` variables set in Koyeb). Native hints live in `web/NativeHints.java`: any new reflection,
resource or serialized DTO may need a hint. Startup is ~0.5 s native versus ~16 s on the JVM.
