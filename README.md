
# Personal Finance Companion

Personal Finance Companion is an Android app built with **Kotlin + Jetpack Compose** to help users track transactions, monitor monthly savings goals, and review spending insights in one place.

## Table of Contents
- [Overview](#overview)
- [Feature Highlights](#feature-highlights)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Data Model](#data-model)
- [How It Works](#how-it-works)
- [Setup & Run](#setup--run)
- [Testing](#testing)
- [Build Variants](#build-variants)
- [Known Limitations / Notes](#known-limitations--notes)
- [Future Improvement Ideas](#future-improvement-ideas)

## Overview
The app centers around four main screens:

1. **Dashboard** – balance overview, income vs expense summary, category spending bars, and savings-goal progress.
2. **Transactions** – add/edit/delete transactions with validation, search, and transaction-type filtering.
3. **Insights** – derived analytics (top spending category, weekly comparisons, frequent activity).
4. **Goals** – monthly savings goal creation, updates, progress tracking, and milestone messaging.

It stores data locally with Room and keeps the UI reactive through Kotlin Flows exposed by a ViewModel.

## Feature Highlights

### 1) Transaction Management
- Create transactions with:
    - amount
    - category
    - date
    - notes
    - type (`INCOME` or `EXPENSE`)
- Edit existing transactions.
- Delete transactions.
- Validate inputs (required fields, numeric amount, positive value, date parsing).
- Search transactions by category or notes.
- Filter by type (`ALL`, `INCOME`, `EXPENSE`).

### 2) Financial Snapshot (Dashboard)
- Current balance (`income - expense`).
- Total income and total expense summaries.
- Savings goal progress bar for the current month.
- Category-wise expense visualization using relative progress bars.

### 3) Savings Goals
- Set/update/remove a monthly savings target.
- Track current month savings against target.
- Show progress percent and milestone guidance.
- Display completion state when the goal is reached.

### 4) Insights
- Highest spending category and amount.
- This week vs last week spending comparison.
- Most frequent category and most frequent transaction type.
- Data sufficiency checks before showing deeper insights.

### 5) Daily Reminder Notifications
- Creates a notification channel on app start.
- Schedules a daily reminder via WorkManager (default at 20:00 local time).
- Requests `POST_NOTIFICATIONS` permission on Android 13+.
- Opens the app when the notification is tapped.

## Architecture
The project follows a lightweight layered architecture:

- **UI Layer (Compose screens + navigation)**
    - Renders state and sends user actions.
- **Presentation Layer (`FinanceViewModel` + `FinanceUiState`)**
    - Combines repository streams + UI filters.
    - Exposes reactive state to UI.
    - Performs CRUD and goal operations.
- **Data Layer (Repository + Room DAO/Database)**
    - Persists and queries transactions and savings goals.
    - Maps Room entities to domain models.
- **Domain Models**
    - `Transaction`, `SavingsGoal`, `TransactionType`.

### Data Flow
1. UI triggers action (e.g., add transaction).
2. ViewModel calls repository.
3. Repository writes to Room via DAO.
4. DAO `Flow` emits new data.
5. ViewModel recomputes `FinanceUiState`.
6. Compose screens automatically recompose.

## Project Structure

```text
Personal-Finance-Companion/
├── app/
│   ├── src/main/java/com/plcoding/personalfinancecompanion/
│   │   ├── Data/
│   │   │   ├── Local/                 # Room DB, DAO, entities, type converters
│   │   │   └── Repository/            # Repository interface + Room implementation
│   │   ├── Domain/Model/              # Core domain models
│   │   ├── presentation/              # FinanceViewModel + FinanceUiState
│   │   ├── reminders/                 # WorkManager worker + scheduler + channel helper
│   │   ├── UserInterface/
│   │   │   ├── dashboard/             # DashboardScreen
│   │   │   ├── transactions/          # TransactionsScreen
│   │   │   ├── insights/              # InsightsScreen
│   │   │   ├── goals/                 # GoalsScreen
│   │   │   └── navigation/            # NavHost + bottom navigation
│   │   ├── ui/theme/                  # Compose theming
│   │   └── MainActivity.kt            # Entry point + VM wiring + reminders
│   ├── src/main/res/                  # Android resources
│   └── build.gradle.kts               # App module config
├── gradle/libs.versions.toml          # Version catalog
├── build.gradle.kts                   # Root Gradle config
├── settings.gradle.kts                # Module/repo setup
└── README.md
```

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Navigation:** `androidx.navigation:navigation-compose`
- **State & lifecycle:** ViewModel, StateFlow, Lifecycle Compose
- **Async:** Kotlin Coroutines + Flow
- **Persistence:** Room (with KSP)
- **Background work:** WorkManager
- **Build:** Gradle Kotlin DSL + Version Catalog

### SDK / Tooling Targets
- `compileSdk = 36`
- `targetSdk = 35`
- `minSdk = 24`
- Java/Kotlin JVM target: 11

## Data Model

### Transaction
- `id: String` (UUID by default)
- `amount: Double`
- `type: TransactionType`
- `category: String`
- `date: LocalDate`
- `notes: String`

### SavingsGoal
- `targetAmount: Double`
- `month: YearMonth` (defaults to current month)

### TransactionType
- `INCOME`
- `EXPENSE`

## How It Works

### App Startup
- `MainActivity`:
    - creates reminder notification channel,
    - requests runtime notification permission when needed,
    - schedules periodic daily reminder,
    - creates `RoomFinanceRepository` from `FinanceDatabase`,
    - initializes `FinanceViewModel` and `FinanceApp`.

### State Handling
- `FinanceViewModel` combines:
    - transaction stream,
    - current month goal stream,
    - local filter state.
- The combined output is emitted as a single `FinanceUiState`.
- `FinanceUiState` also computes derived values:
    - current balance,
    - monthly savings,
    - savings progress,
    - insight-specific stats.

### Navigation
Bottom navigation routes:
- `dashboard`
- `transactions`
- `insights`
- `goals`

## Setup & Run

### Prerequisites
- **Android Studio**: latest stable recommended.
- **JDK 11** installed and configured.
- Android SDK with API levels including min/target/compile versions used by the project.

### 1) Clone the repository
```bash
git clone <your-repo-url>
cd Personal-Finance-Companion
```

### 2) Open in Android Studio
- Open the project folder.
- Let Gradle sync complete.

### 3) Run on emulator/device
- Select an emulator or connected Android device.
- Run the `app` configuration.

### 4) Command-line build
```bash
./gradlew assembleDebug
```

### 5) Install debug APK (optional)
```bash
./gradlew installDebug
```

## Testing

### Unit tests
```bash
./gradlew test
```

### Instrumented tests (requires emulator/device)
```bash
./gradlew connectedAndroidTest
```

## Build Variants
- **debug** (default for local development)
- **release** (minification currently disabled)

## Known Limitations / Notes
- Data is local-only (no cloud sync/account system).
- No dependency injection framework is used yet.
- Insight quality depends on number and variety of transactions.
- Reminder time is currently fixed by default logic (20:00), not user-configurable from UI.

## Future Improvement Ideas
- Add DI (Hilt/Koin) and modularization.
- Add export/import or cloud backup.
- Add budgets and category limits.
- Add charts (monthly trend, category pie/line views).
- Add customizable reminder settings and quiet hours.
- Add localization and currency preferences.

---


