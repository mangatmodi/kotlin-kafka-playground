import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
}

group = "com.mangatmodi"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    implementation("dev.inmo:krontab:0.6.6")
    implementation("com.google.guava:guava:31.0.1-jre")

    implementation("io.ktor:ktor-server-core:1.6.8")
    implementation("io.ktor:ktor-server-netty:1.6.8")
    implementation("io.ktor:ktor-auth:1.6.8")
    implementation("io.ktor:ktor-auth-jwt:1.6.8")


}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}