import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    //id("application")
    id("org.jetbrains.compose") version "1.0.0"
}

group = "ru.senin.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    //val kotlin_version by System.getProperties()
    val kotlin_version = "1.5.31"

    //implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    //implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

    implementation(kotlin("stdlib", kotlin_version))
    implementation(kotlin("reflect", kotlin_version))

    //testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    //testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    //testImplementation("junit:junit:4.13.2")

    testImplementation(kotlin("test", kotlin_version))
    //testImplementation(kotlin("test-junit", kotlin_version))

    implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    //kotlinOptions.jvmTarget = '11'
}
/*
compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sport-management-system"
            packageVersion = "1.0.0"
        }
    }
}
*/
