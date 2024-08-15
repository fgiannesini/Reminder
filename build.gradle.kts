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
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
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
    from(configurations.runtimeClasspath).into(layout.buildDirectory.dir("jars"))
}

task("copyJar", Copy::class) {
    from(tasks.jar).into(layout.buildDirectory.dir("jars"))
}

tasks.jpackage {
    dependsOn("build", "copyDependencies", "copyJar")

    input = layout.buildDirectory.dir("jars").map { it.asFile.path }.get()
    destination = layout.buildDirectory.dir("dist").map { it.asFile.path }.get()

    appName = "Reminder"
    vendor = "fgiannesini"

    mainJar = tasks.jar.get().archiveFileName.get()
    mainClass = "com.fgiannesini.Main"

    javaOptions = listOf("-Dfile.encoding=UTF-8")

    windows {
        winConsole = true
    }
}
