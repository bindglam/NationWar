plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
}

group = "io.github.bindglam.nationwar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.bindglam:ConfigLib:1.1.2")
    compileOnly("com.github.bindglam:DatabaseLib:1.0.4")
    compileOnly("org.incendo:cloud-paper:2.0.0-beta.14")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.2.3")
    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

paperPluginYaml {
    main = "$group.NationWarPlugin"
    loader = "$group.NationWarPluginLoader"
    authors.add("bindglam")
    apiVersion = "1.20"
}

tasks {
    runServer {
        minecraftVersion("1.20.1")
    }
}