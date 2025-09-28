# Changelog

All notable changes to the Philabid project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure with Java 21 and JavaFX 21
- Gradle build system with Kotlin DSL
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