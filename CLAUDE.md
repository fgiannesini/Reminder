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

# Run the application (after build)
java -jar build/libs/Reminder-1.0.jar
```

The application requires a PostgreSQL database. Set these environment variables before running:
- `DATABASE_URL` — JDBC URL
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`

Integration tests spin up PostgreSQL automatically via TestContainers — no manual DB setup needed for tests.

## Architecture

This is a language-learning flashcard app with a Spring Boot REST backend and a vanilla JS single-page frontend (`src/main/resources/static/index.html`).

**Package structure:** `com.fgiannesini`

| Layer | Classes | Role |
|-------|---------|------|
| Domain | `Word`, `Dictionary`, `Matching`, `RemainingStats` | Core learning logic; no framework dependencies |
| Storage | `StorageHandler` (interface), `DatabaseStorageHandler` | Persistence abstraction |
| Web | `ReminderController`, DTOs, `SpringMain` | REST API and Spring Boot wiring |
| Original | `OriginalDictionary`, `CsvOriginalWord` | Loads `dictionary.csv` at startup for seeding/validation |
| Console | `Main` | Utility for detecting duplicate words in the CSV |

**REST API** (`/reminder/word/`):
- `GET /next` — picks the next word to study (at most 20 candidates: unlearned or learned 1+ week ago)
- `POST /check` — evaluates a translation attempt; returns `MATCHED`, `CLOSED`, or `NOT_MATCHED`
- `GET /remaining` — learning statistics

**Learning rules** (in `Dictionary`):
- 3 correct `MATCHED` answers → word marked as learned
- 2 post-learn confirmations → word fully mastered
- Any wrong answer resets `checkedCount` to 0
- Spaced repetition: learned words re-surface after 1 week

**Data model** (`WordDao` JPA entity, PostgreSQL):
- PK: `word` (String)
- `translation`, `checked_count`, `learnt_moment`, `learnt_count`
- DDL: `spring.jpa.hibernate.ddl-auto=update`

**Tech stack:** Java 21, Spring Boot 4.x, Spring Data JPA / Hibernate, PostgreSQL, OpenCSV, JUnit 5, TestContainers.
