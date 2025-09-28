# Philabid - Stamp Auction Bidding Assistant

[![CI](https://github.com/michalwy/philabid/actions/workflows/ci.yml/badge.svg)](https://github.com/michalwy/philabid/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-green.svg)](https://openjfx.io/)

Philabid is an open-source, multilingual JavaFX desktop application designed to assist stamp collectors and dealers with auction bidding. It runs locally without requiring external services, providing a complete solution for managing stamp auction data, tracking bids, and analyzing market trends.

## Features

- 🌍 **Multilingual Support**: Built with ICU4J, supporting English, German, French, and Polish
- 💰 **Monetary Handling**: Uses Moneta (JSR 354) for accurate currency and monetary calculations
- 🎨 **Modern UI**: JavaFX-based interface with ControlsFX enhancements
- 🗄️ **Local Database**: SQLite database with automatic migrations via Flyway
- 📊 **Auction Tracking**: Monitor multiple auction houses and lot items
- 🎯 **Bid Management**: Track your bids and set maximum bid limits
- 📋 **Watchlist**: Keep track of items you're interested in
- 🏷️ **Stamp Catalog**: Integrated catalog for stamp identification and valuation
- 📦 **Standalone**: Runs completely offline without external dependencies

## Requirements

- Java 21 or later
- 2GB RAM minimum, 4GB recommended
- 500MB disk space for application and database

## Building from Source

### Prerequisites

- Java 21 JDK (Temurin recommended)
- Git

### Build Steps

```bash
# Clone the repository
git clone https://github.com/michalwy/philabid.git
cd philabid

# Build the application
./gradlew build

# Run the application
./gradlew runApp

# Create a distributable package
./gradlew jlink
```

## Installation

### Pre-built Packages

Download the latest release from the [releases page](https://github.com/michalwy/philabid/releases).

### Platform-specific Installation

#### Windows
1. Download `philabid-windows.zip`
2. Extract to your desired location
3. Run `bin/philabid.bat`

#### macOS
1. Download `philabid-macos.tar.gz`
2. Extract to Applications folder
3. Run `bin/philabid`

#### Linux
1. Download `philabid-linux.tar.gz`
2. Extract to your desired location
3. Run `bin/philabid`

## Usage

### First Launch

1. Start the application
2. The database will be automatically created on first run
3. Configure your preferences in the Tools menu

### Managing Auctions

1. Navigate to the **Auctions** tab
2. Add auction houses and upcoming auctions
3. Import lot data from auction catalogs

### Tracking Bids

1. Go to the **My Bids** tab
2. Add items you want to bid on
3. Set maximum bid amounts
4. Monitor bid status in real-time

### Using the Catalog

1. Use the **Catalog** tab to browse stamp information
2. Search by country, year, or description
3. View catalog values and images

## Configuration

The application stores its configuration in `philabid-config.json`. Key settings include:

- **Locale**: Application language (en, de, fr, pl)
- **Currency**: Default currency for monetary values
- **Theme**: UI theme selection
- **Auto-refresh**: Automatic data refresh intervals

## Database

Philabid uses SQLite for local data storage. The database includes tables for:

- Auction houses and auctions
- Stamp catalog entries
- Auction lots and bids
- User watchlist and preferences

Database migrations are handled automatically by Flyway.

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/philabid/
│   │       ├── PhilabidApplication.java  # Main application class
│   │       ├── database/                 # Database management
│   │       ├── i18n/                     # Internationalization
│   │       ├── model/                    # Data models
│   │       ├── service/                  # Business logic
│   │       └── ui/                       # User interface
│   └── resources/
│       ├── css/                          # Stylesheets
│       ├── db/migration/                 # Database migrations
│       ├── fxml/                         # UI layouts
│       ├── images/                       # Application images
│       └── messages*.properties          # Localization files
└── test/
    └── java/                             # Unit tests
```

### Key Technologies

- **Java 21**: Modern Java with records, pattern matching, and virtual threads
- **JavaFX 21**: Rich desktop UI framework
- **Gradle 8.5**: Build automation with Kotlin DSL
- **ControlsFX**: Enhanced UI controls and dialogs
- **ICU4J**: Advanced internationalization and Unicode support
- **Moneta**: JSR 354 implementation for monetary calculations
- **SQLite**: Embedded database
- **Flyway**: Database migration tool
- **JUnit 5**: Testing framework
- **SLF4J + Logback**: Logging

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# Run integration tests
./gradlew integrationTest
```

### Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass (`./gradlew test`)
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## Internationalization

Adding a new language:

1. Create `messages_[locale].properties` in `src/main/resources/`
2. Add the locale to `I18nManager.supportedLocales`
3. Update the UI components to use the new locale
4. Test the application with the new language

Currently supported languages:
- English (en)
- German (de)
- French (fr)
- Polish (pl)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [OpenJFX](https://openjfx.io/) for the JavaFX platform
- [ControlsFX](https://controlsfx.github.io/) for enhanced UI controls
- [ICU](https://icu.unicode.org/) for internationalization support
- [Moneta](https://github.com/JavaMoney/jsr354-ri) for monetary calculations
- [SQLite](https://www.sqlite.org/) for the embedded database
- [Flyway](https://flywaydb.org/) for database migrations

## Support

- Create an [issue](https://github.com/michalwy/philabid/issues) for bug reports or feature requests
- Check the [wiki](https://github.com/michalwy/philabid/wiki) for documentation
- Join discussions in [GitHub Discussions](https://github.com/michalwy/philabid/discussions)

---

**Philabid** - Making stamp auction bidding easier, one bid at a time! 🎯