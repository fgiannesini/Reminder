plugins {
    id("java")
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.fgiannesini"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.testcontainers:testcontainers-bom:1.21.1"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.opencsv:opencsv:5.11.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    runtimeOnly("org.postgresql:postgresql")
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}
