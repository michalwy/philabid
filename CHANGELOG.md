# Changelog

All notable changes to the Philabid project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- **Gradle upgraded to 9.0.0** - Build system updated with latest features and performance improvements
- **JavaFX upgraded to 23.0.1** - Latest JavaFX version with enhanced features and bug fixes
- **Major dependency updates**:
  - ICU4J: 73.2 → 76.1 (latest internationalization features)
  - Moneta: 1.4.2 → 1.4.4 (monetary calculation improvements)
  - SQLite JDBC: 3.43.2.2 → 3.46.1.0 (database driver updates)
  - SLF4J: 2.0.9 → 2.0.16 (logging framework updates)
  - Logback: 1.4.11 → 1.5.8 (logging implementation updates)
  - Jackson: 2.15.3 → 2.18.0 (JSON processing improvements)
  - JUnit: 5.10.0 → 5.11.2 (testing framework updates)
  - Mockito: 5.6.0 → 5.14.2 (testing mock framework updates)
- **Enhanced toolchain management** with Foojay resolver for better JDK auto-provisioning
- **CI/CD pipeline updated** to use Gradle 9.0.0

### Added
- Initial project structure with Java 21 and JavaFX 23
- Gradle build system with Kotlin DSL and Gradle 9.0 features
- Multilingual support (English, German, French, Polish) using ICU4J
- SQLite database with automatic migrations via Flyway
- Modern JavaFX UI with ControlsFX enhancements
- Monetary calculations using Moneta (JSR 354)
- Basic application framework with dependency injection
- Comprehensive test suite with JUnit 5
- GitHub Actions CI/CD pipeline
- Cross-platform packaging with jlink
- OSS documentation (README, CONTRIBUTING, CODE_OF_CONDUCT)

### Features
- Dashboard with auction and bid statistics
- Auction management and tracking
- Stamp catalog browser with search functionality
- Personal bid tracking and management
- Watchlist for items of interest
- Configuration management with JSON persistence
- Logging system with SLF4J and Logback

### Technical
- Module system support (module-info.java)
- Resource bundle-based internationalization
- CSS styling for consistent UI appearance
- Database schema with indexes for performance
- Automated testing with headless JavaFX support

## [1.0.0] - TBD

### Added
- First stable release
- Complete auction bidding assistance functionality
- Full internationalization support
- Comprehensive user documentation
- Production-ready packaging for all platforms

---

## Release Types

- **Added** for new features
- **Changed** for changes in existing functionality
- **Deprecated** for soon-to-be removed features
- **Removed** for now removed features
- **Fixed** for any bug fixes
- **Security** for vulnerability fixes