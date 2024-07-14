plugins {
    id("java")
    id("org.panteleyev.jpackageplugin") version "1.6.0"
}

group = "com.fgiannesini"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.opencsv:opencsv:5.9")
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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

task("copyDependencies", Copy::class) {
    from(configurations.runtimeClasspath).into("$buildDir/jars")
}

task("copyJar", Copy::class) {
    from(tasks.jar).into("$buildDir/jars")
}

tasks.jpackage {
    dependsOn("build", "copyDependencies", "copyJar")

    input = "$buildDir/jars"
    destination = "$buildDir/dist"

    appName = "Reminder"
    vendor = "fgiannesini"

    mainJar = tasks.jar.get().archiveFileName.get()
    mainClass = "com.fgiannesini.Main"

    javaOptions = listOf("-Dfile.encoding=UTF-8")

    windows {
        winConsole = true
    }
}
