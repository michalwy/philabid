# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Development Commands

### Building and Running
```bash
# Build the application
./gradlew build

# Run the application (preferred method with proper JavaFX module path)
./gradlew runApp

# Run tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run integration tests (if available)
./gradlew integrationTest

# Run code quality checks
./gradlew check

# Clean build artifacts
./gradlew clean
```

### Database Operations
```bash
# Run Flyway database migrations manually
./gradlew flywayMigrate

# Get migration info
./gradlew flywayInfo

# Validate migrations
./gradlew flywayValidate
```

### Platform-specific Scripts
```bash
# Windows
scripts/run-dev.bat

# Unix/Linux/macOS
./scripts/run-dev.sh
```

### Testing Individual Components
When running single tests, use the JavaFX headless properties:
```bash
./gradlew test --tests "com.philabid.service.ConfigurationServiceTest" \
  -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw
```

## Project Architecture

### Technology Stack
- **Java 21** with modern features (records, pattern matching, modules)
- **JavaFX 21** for desktop UI with FXML
- **Gradle 9.1.0** with Kotlin DSL for build automation
- **SQLite** with **Flyway** for database migrations
- **ICU4J** for advanced internationalization (en, de, fr, pl)
- **Moneta (JSR 354)** for monetary calculations
- **ControlsFX** for enhanced UI components

### Core Architecture Pattern
The application follows a **layered architecture** with clear separation of concerns:

1. **Application Layer** (`PhilabidApplication.java`) - JavaFX application bootstrap and lifecycle management
2. **UI Layer** (`ui/`) - JavaFX controllers, FXML files, and UI-specific logic
3. **Service Layer** (`service/`) - Business logic and application services
4. **Database Layer** (`database/`) - Database management and migrations
5. **Model Layer** (`model/`) - Domain entities and data structures
6. **Infrastructure** (`i18n/`) - Cross-cutting concerns like internationalization

### Key Components

#### Service Initialization Pattern
Services are initialized in a specific order in `PhilabidApplication.init()`:
1. `ConfigurationService` - Loads JSON configuration
2. `I18nManager` - Sets up internationalization
3. `DatabaseManager` - Initializes SQLite with Flyway migrations

The `MainController` receives these services via dependency injection through `setServices()`.

#### Configuration Management
- Uses Jackson for JSON configuration in `philabid-config.json`
- Hierarchical configuration with dot-notation paths (e.g., `"application.locale"`)
- Automatic fallback to sensible defaults
- Runtime configuration changes are possible

#### Database Schema Management
- **Flyway migrations** in `src/main/resources/db/migration/`
- Migration naming: `V{version}__{description}.sql`
- Automatic migration on application startup
- SQLite database file: `philabid.db` (created automatically)

#### Internationalization (i18n)
- **ICU4J-based** with ResourceBundle integration
- Property files: `messages.properties`, `messages_de.properties`, etc.
- Access via `I18nManager.getString(key)` with parameter substitution
- Dynamic locale switching supported

#### Monetary Handling
- Uses **Moneta (JSR 354)** for precise currency calculations
- Multi-currency auction support
- Currency configuration per auction house

### Module System
The project uses **Java Platform Module System (JPMS)**:
- Module name: `philabid`
- All major dependencies are properly required
- Exports main packages for potential future modularity

### UI Structure
- **Main window** controlled by `MainController`
- **Tab-based interface**: Dashboard, Auctions, Catalog, Bids
- **FXML-based** UI definitions in `src/main/resources/fxml/`
- **CSS styling** in `src/main/resources/css/`
- **StatusBar** with real-time updates using Timeline animations

### Development Guidelines

#### Adding New Features
1. Create model classes in `com.philabid.model`
2. Add database migrations in `src/main/resources/db/migration/`
3. Implement services in `com.philabid.service`
4. Create UI controllers in `com.philabid.ui`
5. Add FXML files in `src/main/resources/fxml/`

#### Testing Strategy
- **JUnit 5** for unit tests
- **TestFX** for JavaFX UI testing
- **Mockito** for mocking dependencies
- Tests run in headless mode in CI/CD

#### Internationalization Workflow
1. Add keys to `src/main/resources/messages.properties`
2. Add translations to `messages_de.properties`, `messages_fr.properties`, `messages_pl.properties`
3. Use `I18nManager.getString()` in Java code
4. Use `%key` syntax in FXML files with ResourceBundle

#### Database Changes
1. Create new migration: `V{next_version}__{description}.sql`
2. Update corresponding model classes
3. Test migration on clean database
4. Update any affected services or DAOs

### Build and Packaging
- **jlink** capable for custom JRE distribution
- **Cross-platform** packaging (Windows, macOS, Linux)
- **GitHub Actions CI/CD** with artifact generation
- Build artifacts in `build/distributions/`

### Configuration Files
- `philabid-config.json` - Runtime application configuration
- `philabid.db` - SQLite database (auto-created)
- `gradle.properties` - Gradle build configuration
- `module-info.java` - Java module configuration

### Logging
- **SLF4J + Logback** for structured logging
- Log levels configurable via Logback configuration
- UI log display in main dashboard tab
