plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "serhii.malov"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springdoc.version"] = "2.1.0"
extra["mapstruct.version"] = "1.5.5.Final"
extra["lombok.version"] = "1.18.22"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${property("springdoc.version")}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springdoc.version")}")

    implementation("org.mapstruct:mapstruct:${property("mapstruct.version")}")
    annotationProcessor ("org.mapstruct:mapstruct-processor:${property("mapstruct.version")}")
    compileOnly("org.projectlombok:lombok:${property("lombok.version")}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment("SPRING_PROFILES_ACTIVE", "test")
}
