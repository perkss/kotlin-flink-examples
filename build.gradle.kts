buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    jacoco
    kotlin("jvm") version "1.6.10"
    `maven-publish`
    id("org.unbroken-dome.test-sets") version "4.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("com.diffplug.spotless") version "6.0.5"
    id("com.github.spotbugs") version "5.0.3"
    id("com.avast.gradle.docker-compose") version "0.14.11"
    id("com.github.ben-manes.versions") version "0.39.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.flink.java)
    implementation(libs.flink.streaming.java)
    implementation(libs.flink.clients)
    implementation(libs.bundles.logging)
    implementation("org.codehaus.groovy:groovy-all:3.0.9")

    testImplementation(libs.flink.test.utils.core)
    testImplementation(libs.flink.runtime)
    testImplementation(libs.flink.test.utils.junit)
    testImplementation(libs.bundles.test)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

spotless {
    java { googleJavaFormat() }
    kotlinGradle { ktlint() }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

group = "com.perkss"
version = "1.0-SNAPSHOT"
