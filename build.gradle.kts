import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
}

group = "com.mangatmodi"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies{
    implementation ("dev.inmo:krontab:0.6.6")
    implementation("com.google.guava:guava:31.0.1-jre")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}