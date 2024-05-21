import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("groovy")
    id("maven-publish")

}

dependencies {
    implementation(libs.gradle)
    implementation(libs.transform.api)
    implementation(libs.commons.io)
    //gradle sdk
    implementation(gradleApi())
    //groovy sdk
    implementation(localGroovy())
    implementation(libs.dex.tools)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}


publishing {
    repositories {
        maven {
            name = "MavenLocalRepo"
            url = uri("file://\$buildDir/repo")
            url = uri("file://${buildDir}/repo")
        }
    }
    publications {
        create<MavenPublication>("plugin") {
            groupId = "com.ryan7.dexObfuscator"
            artifactId = "plugin"
            version = "1.0.0"
            from(components["java"])
        }
    }
}