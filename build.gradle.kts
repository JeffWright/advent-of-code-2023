import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.9.21"
    //application
    id("com.ncorti.ktfmt.gradle") version "0.11.0"
}


repositories {
    mavenLocal()
    mavenCentral()
    repositories { maven { setUrl("https://jitpack.io") } }
}

dependencies {
    implementation("com.github.JeffWright:scriptutils:0.7.4")
    implementation("com.github.JeffWright:logsugar:0.5.0")

    testImplementation(kotlin("test"))

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
