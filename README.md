# Movie Database Desktop App

A JavaFX-based desktop application for browsing movies, managing user accounts, saving favorites and watchlists, writing reviews, and exploring movie details from the TMDB API.

## Project Overview

This application follows a layered architecture with clear separation between:
- UI layer: JavaFX FXML views and controllers
- Application layer: services and session management
- Data layer: DAO classes and database configuration
- External API layer: TMDB integration

## Architecture

The project is organized around a modular structure that connects the user interface to business logic and persistence.

```text
+------------------------+
|        User            |
|  (JavaFX Desktop App) |
+-----------+------------+
            |
            v
+------------------------+
|      Controllers       |
|  Login / Home / Movie |
|  Profile / Register   |
+-----------+------------+
            |
            v
+------------------------+
|       Services         |
| Auth / User / TMDB    |
+-----------+------------+
            |
            +--------+--------+
            |                 |
            v                 v
+------------------+   +----------------------+
| DAO Layer        |   | External API Layer  |
| User / Review   |   | TMDB API / Images   |
| Favorite / Watch|   +----------------------+
+--------+---------+
         |
         v
+------------------------+
|      Database         |
| SQLite / JDBC        |
+------------------------+
```

## Component Breakdown

| Layer | Components | Responsibility |
| --- | --- | --- |
| Presentation | FXML files, controllers | Handles UI screens and user interaction |
| Business Logic | Services | Manages authentication, user actions, and TMDB requests |
| Data Access | DAO classes | Performs CRUD operations for users, reviews, favorites, and watchlists |
| Persistence | Database config and schema | Stores application data and relationships |
| External Integration | TMDB service | Fetches movie metadata, cast, and poster information |

## Main Workflow

```text
1. User launches the app
2. Login/Register screen is shown
3. Controller receives input
4. Service layer validates and processes requests
5. DAO layer interacts with the database
6. TMDB service fetches movie details from external API
7. Results are returned to the UI for display
```

## Request Flow

```text
User Action
   -> Controller
   -> Service
   -> DAO / Database
   -> Response
   -> JavaFX UI update
```

## Technology Stack

- Java 17+
- JavaFX for desktop UI
- Maven for build and dependency management
- JDBC for database access
- SQLite / SQL schema for persistence
- TMDB API for movie data

## Key Features

- User registration and login
- Movie browsing and detail view
- Favorite and watchlist management
- Review submission and retrieval
- Session-aware navigation

## Project Structure

```text
src/main/java/com/moviedb/
├── controller/
├── service/
├── dao/
├── model/
├── util/
├── config/
└── Main.java
```

## Notes

The application is designed to be easy to extend with new features like search, recommendations, admin tools, or cloud sync.
