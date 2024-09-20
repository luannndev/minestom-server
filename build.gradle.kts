plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "dev.luan.server"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:6fc64e3a5d")
    testImplementation("net.minestom:minestom-snapshots:6fc64e3a5d")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}