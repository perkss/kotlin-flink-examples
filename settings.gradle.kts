pluginManagement {
    plugins {
        java
        id("org.jetbrains.kotlin.jvm") version "1.6.10"
    }
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }
}

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.6.10")
            version("flink", "1.14.1")
            version("spring-boot", "2.6.1")
            version("slf4j", "1.7.32")
            version("logback", "1.2.6")
            version("guava", "30.0-jre")
            version("jupiter", "5.7.1")

            alias("flink-java").to("org.apache.flink", "flink-java").versionRef("flink")
            alias("flink-clients").to("org.apache.flink", "flink-clients_2.12").versionRef("flink")
            alias("flink-streaming-java").to("org.apache.flink", "flink-streaming-java_2.12").versionRef("flink")
            alias("kotlin-stdlib-jdk8").to("org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef("kotlin")
            alias("kotlin-std-lib").to("org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef("kotlin")
            alias("spring-boot-starter").to("org.springframework.boot", "spring-boot-starter").versionRef("spring-boot")
            alias("spring-boot-starter-actuator").to("org.springframework.boot", "spring-boot-starter-actuator").versionRef("spring-boot")
            alias("spring-boot-starter-webflux").to("org.springframework.boot", "spring-boot-starter-webflux").versionRef("spring-boot")
            alias("slf4j").to("org.slf4j", "slf4j-api").versionRef("slf4j")
            alias("logback").to("ch.qos.logback", "logback-classic").versionRef("logback")
            alias("junit-jupiter").to("org.junit.jupiter", "junit-jupiter-api").versionRef("jupiter")
            alias("junit-engine").to("org.junit.jupiter", "junit-jupiter-engine").withoutVersion()

            bundle("logging", listOf("slf4j", "logback"))
        }
    }
}

rootProject.name = "kotlin-flink-examples"
