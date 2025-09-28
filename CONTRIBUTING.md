# Contributing to Philabid

Thank you for your interest in contributing to Philabid! This document provides guidelines and information for contributors.

## Code of Conduct

We are committed to providing a welcoming and inclusive environment for all contributors. Please be respectful and professional in all interactions.

## Getting Started

### Development Environment Setup

1. **Java 21**: Install Java 21 JDK (Temurin recommended, LTS version)
2. **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
3. **Git**: For version control

### Building the Project

```bash
git clone https://github.com/michalwy/philabid.git
cd philabid
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Running the Application

```bash
./gradlew runApp
```

## How to Contribute

### Reporting Bugs

1. Check existing [issues](https://github.com/michalwy/philabid/issues) first
2. Create a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details (OS, Java version)
   - Screenshots if applicable

### Requesting Features

1. Check if the feature already exists or is planned
2. Create a new issue with:
   - Clear description of the feature
   - Use cases and benefits
   - Possible implementation approach

### Submitting Changes

1. **Fork** the repository
2. **Create a branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes** following our coding standards
4. **Add tests** for new functionality
5. **Run tests** to ensure nothing breaks:
   ```bash
   ./gradlew test
   ```
6. **Commit** your changes with clear messages:
   ```bash
   git commit -m "Add feature: brief description"
   ```
7. **Push** to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```
8. **Create a Pull Request** with:
   - Clear title and description
   - Reference to related issues
   - Screenshots for UI changes

## Coding Standards

### Java Code Style

- Follow standard Java naming conventions
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Add JavaDoc for public methods and classes
- Use meaningful variable and method names

### File Organization

- Keep classes focused and cohesive
- Organize imports (remove unused ones)
- Place constants at the top of the class
- Group related methods together

### Testing

- Write unit tests for new functionality
- Aim for good test coverage
- Use descriptive test method names
- Test both happy path and edge cases

### Documentation

- Update README.md for significant changes
- Add inline comments for complex logic
- Update JavaDoc for API changes
- Include examples in documentation

## Internationalization (i18n)

When adding new UI text:

1. Add keys to `messages.properties`
2. Add translations to all language files:
   - `messages_de.properties` (German)
   - `messages_fr.properties` (French)
   - `messages_pl.properties` (Polish)
3. Use `I18nManager.getString()` in code
4. Test with different locales

## Database Changes

For database schema changes:

1. Create a new Flyway migration file in `src/main/resources/db/migration/`
2. Use format: `V{version}__{description}.sql`
3. Test migrations on clean database
4. Update corresponding model classes

## UI Guidelines

- Follow JavaFX best practices
- Ensure accessibility (keyboard navigation, screen readers)
- Test on different screen sizes
- Use consistent styling with existing UI
- Add proper tooltips and help text

## Pull Request Guidelines

### Before Submitting

- [ ] Code compiles without warnings
- [ ] All tests pass
- [ ] New functionality is tested
- [ ] Documentation is updated
- [ ] Code follows style guidelines
- [ ] Commit messages are clear

### PR Description

Include:
- What changes were made
- Why the changes were necessary
- How to test the changes
- Screenshots for UI changes
- Breaking changes (if any)

## Release Process

Releases follow semantic versioning (MAJOR.MINOR.PATCH):

- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

## Development Workflow

1. **Issues**: All changes should be linked to an issue
2. **Branches**: Use feature branches for development
3. **Reviews**: All PRs require review before merging
4. **CI**: All tests must pass before merging
5. **Documentation**: Keep docs up to date

## Getting Help

- Ask questions in [GitHub Discussions](https://github.com/michalwy/philabid/discussions)
- Check the [Wiki](https://github.com/michalwy/philabid/wiki) for detailed documentation
- Look at existing code for examples
- Reach out to maintainers if needed

## Recognition

Contributors will be recognized in:
- CONTRIBUTORS.md file
- Release notes
- GitHub contributors page

Thank you for contributing to Philabid! 🚀