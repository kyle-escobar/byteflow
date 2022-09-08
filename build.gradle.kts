plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

group = "dev.kyleescobar.byteflow"
version = "1.0.0"

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
    kotlinOptions.jvmTarget = "11"
}

tasks.compileJava {
    sourceCompatibility = "11"
    targetCompatibility = "11"
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set("ByteFlow")
                description.set("A Java ByteCode modification and analysis tree library.")
                url.set("https://github.com/kyle-escobar/byteflow")
                developers {
                    developer {
                        id.set("kyle-escobar")
                        name.set("Kyle Escobar")
                    }
                }
            }
        }
    }
}