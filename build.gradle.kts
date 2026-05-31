import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.fgiannesini"
version = "1.0"

springBoot {
    mainClass.set("com.fgiannesini.web.SpringMain")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val mockitoAgent: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.restclient)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.opencsv)

    testImplementation(libs.spring.boot.test.data.jpa)
    testImplementation(libs.spring.boot.test.restclient)
    testImplementation(libs.spring.boot.test.containers)
    testImplementation(libs.spring.boot.test.webmvc)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    runtimeOnly(libs.postgresql)

    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}

tasks {
    test {
        useJUnitPlatform()
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
