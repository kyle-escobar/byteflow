plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = "dev.kyleescobar.byteflow"
version = "1.1.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.ow2.asm:asm:_")
    implementation("org.ow2.asm:asm-commons:_")
    implementation("org.ow2.asm:asm-util:_")
    implementation("org.ow2.asm:asm-tree:_")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}