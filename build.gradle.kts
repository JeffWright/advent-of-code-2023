import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.9.21"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.ncorti.ktfmt.gradle") version "0.11.0"
}


repositories {
    mavenLocal()
    mavenCentral()
    repositories { maven { setUrl("https://jitpack.io") } }
}

dependencies {
    implementation("com.github.JeffWright:scriptutils:0.7.5")
    implementation("com.github.JeffWright:logsugar:0.5.1")

    testImplementation(kotlin("test"))

}

ktfmt {
    googleStyle() // 2-space indentation
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("dev.jtbw.adventofcode.MainKt")
}

tasks.withType<ShadowJar> {
    dependsOn("jar")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
