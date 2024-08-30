plugins {
    val kotlinVersion = "2.0.0"
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

group = "com.github.eemmiirr.osshowcase"
version = "1.0.0-SNAPSHOT"

val springDataOpenSearchVersion = "1.5.1"
val opensearchVersion = "2.12.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.opensearch.client:spring-data-opensearch-starter:$springDataOpenSearchVersion") {
        exclude(group = "org.opensearch.client", module = "opensearch-rest-high-level-client")
    }
    implementation("org.opensearch.client:opensearch-java:$opensearchVersion")
    implementation("org.opensearch.client:opensearch-rest-client:$opensearchVersion")
    implementation("commons-io:commons-io:2.16.1")
    implementation("com.google.guava:guava:33.3.0-jre")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("org.apache.commons:commons-lang3:3.16.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.opensearch:opensearch-testcontainers:2.1.0")
    testImplementation("org.testcontainers:testcontainers:1.20.1")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict -Xemit-jvm-type-annotations")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
