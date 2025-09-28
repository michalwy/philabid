plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.flywaydb.flyway") version "9.22.3"
}

group = "com.philabid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // JavaFX
    implementation("org.openjfx:javafx-controls:26-ea+7")
    implementation("org.openjfx:javafx-fxml:26-ea+7")

    // ControlsFX for enhanced UI controls
    implementation("org.controlsfx:controlsfx:11.2.2")

    // ICU4J for internationalization
    implementation("com.ibm.icu:icu4j:77.1")

    // Moneta for monetary handling
    implementation("org.javamoney:moneta:1.4.5")

    // SQLite database
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")

    // Flyway for database migrations
    implementation("org.flywaydb:flyway-core:9.22.3")

    // Logging
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // JSON processing for configuration
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0-RC3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
}

application {
    mainClass = "com.philabid.PhilabidApplication"
    mainModule = "philabid"
}

javafx {
    version = "21.0.1"
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
        "--add-modules", "javafx.controls,javafx.fxml",
        "--enable-native-access=org.xerial.sqlitejdbc",
        "--enable-native-access=javafx.graphics",
        "--add-reads=org.xerial.sqlitejdbc=ALL-UNNAMED"
    )
}