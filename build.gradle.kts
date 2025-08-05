plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.regdevs.notesfx"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("com.regdevs.notesfx.MainApp")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    implementation("org.fxmisc.richtext:richtextfx:0.11.0")
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("org.slf4j:slf4j-simple:2.0.12")

    testImplementation(platform(
        "org.junit:junit-bom:5.10.0"
    ))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}