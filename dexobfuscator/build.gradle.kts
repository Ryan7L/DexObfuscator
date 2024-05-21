
plugins {
    id("java-library")
//    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("groovy")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")

}

dependencies {
    implementation("com.android.tools.build:transform-api:1.5.0")
    implementation("commons-io:commons-io:2.13.0")
    //gradle sdk
    implementation(gradleApi())
    //groovy sdk
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:7.2.2")
    implementation("com.github.Ryan7L.BlackObfuscator:dex-tools:v3.1")
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


//publishing {
//    repositories {
//        maven {
//            name = "MavenLocalRepo"
//            url = uri("file://\$buildDir/repo")
//            url = uri("file://${buildDir}/repo")
//        }
//    }
//    publications {
//        create<MavenPublication>("plugin") {
//            groupId = "com.ryan7.dexObfuscator"
//            artifactId = "plugin"
//            version = "1.0.0"
//            from(components["java"])
//        }
//    }
//}
afterEvaluate {
    publishing{

        publications {
            create<MavenPublication>("plugin") {
                groupId = "com.ryan7.dexObfuscator"
                artifactId = "plugin"
                version = "1.0.3"
                from(components["java"])
            }
        }
    }
}