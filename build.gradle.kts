plugins {
    kotlin("jvm") version "2.2.0"
    application
    idea
    jacoco
}

group = "math.simulation"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.17")

    implementation("ch.qos.logback:logback-classic:1.5.18")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.2")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.2")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.19.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("org.apache.commons:commons-math3:3.6.1")

    implementation("org.apache.xmlgraphics:batik-svggen:1.19")

    implementation("org.apache.xmlgraphics:batik-dom:1.19")

    testImplementation("org.jetbrains.kotlin:kotlin-test")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")

    testImplementation("org.assertj:assertj-core:3.27.3")

    testImplementation("io.mockk:mockk:1.14.5")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}