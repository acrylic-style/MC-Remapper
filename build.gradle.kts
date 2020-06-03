plugins {
    kotlin("jvm") version "1.3.72"
    application
    `maven-publish`
}

group = "io.heartpattern"
version = "2.0.0-SNAPSHOT"

repositories {
    maven("https://maven.heartpattern.io/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt", "clikt", "2.1.0")
    implementation("org.ow2.asm", "asm", "8.0.1")
    implementation("org.ow2.asm", "asm-commons", "8.0.1")
    implementation("org.ow2.asm", "asm-tree", "8.0.1")
    implementation("com.google.guava", "guava", "28.1-jre")
    implementation("me.tongfei", "progressbar", "0.8.1")
    implementation("kr.heartpattern","MCVersions","1.0.0-SNAPSHOT")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

application {
    mainClassName = "io.heartpattern.mcremapper.commandline.MCRemapperAppKt"
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + "-XXLanguage:+InlineClasses"
            freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
        }
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
}


if("maven.username" in properties && "maven.password" in properties){
    publishing{
        repositories{
            maven(
                if(version.toString().endsWith("SNAPSHOT"))
                    "https://maven.heartpattern.io/repository/maven-public-snapshots/"
                else
                    "https://maven.heartpattern.io/repository/maven-public-releases/"
            ){
                credentials{
                    username = properties["maven.username"].toString()
                    password = properties["maven.password"].toString()
                }
            }
        }

        publications{
            create<MavenPublication>("maven"){
                artifactId = "mcremapper"
                from(components["java"])
                artifact(tasks["sourcesJar"])
            }
        }
    }
}