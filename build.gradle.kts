plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.flywaydb.flyway") version "9.22.3"
}

group = "com.philabid"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // JavaFX - upgraded to latest version
    implementation("org.openjfx:javafx-controls:23.0.1")
    implementation("org.openjfx:javafx-fxml:23.0.1")
    
    // ControlsFX for enhanced UI controls
    implementation("org.controlsfx:controlsfx:11.2.1")
    
    // ICU4J for internationalization - updated to latest
    implementation("com.ibm.icu:icu4j:76.1")
    
    // Moneta for monetary handling
    implementation("org.javamoney:moneta:1.4.4")
    
    // SQLite database - updated to latest
    implementation("org.xerial:sqlite-jdbc:3.46.1.0")
    
    // Flyway for database migrations - keeping compatible version
    implementation("org.flywaydb:flyway-core:9.22.3")
    
    // Logging - updated to latest
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.8")
    
    // JSON processing for configuration - updated to latest
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    
    // Testing - updated to latest with platform engine
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
}

application {
    mainClass = "com.philabid.PhilabidApplication"
    mainModule = "philabid"
}

javafx {
    version = "23.0.1"
    modules("javafx.controls", "javafx.fxml")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("testfx.robot", "glass")
    systemProperty("testfx.headless", "true")
    systemProperty("prism.order", "sw")
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
}

// Flyway configuration
flyway {
    url = "jdbc:sqlite:philabid.db"
    locations = arrayOf("classpath:db/migration")
}

// Custom task to run with JavaFX modules
tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Run the application with proper JavaFX module path"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "com.philabid.PhilabidApplication"
    jvmArgs = listOf(
        "--module-path", configurations.runtimeClasspath.get().asPath,
        "--add-modules", "javafx.controls,javafx.fxml"
    )
}