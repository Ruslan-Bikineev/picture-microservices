val javaVersion = "21"
val springdocVersion = "2.8.8"
val dataFakerVersion = "2.4.3"
val mapstructVersion = "1.6.3"
val restAssuredVersion = "5.5.5"
val testcontainersVersion = "1.21.3"

plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "mcp.server"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(javaVersion)
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
	implementation("org.mapstruct:mapstruct:$mapstructVersion")
	implementation("net.datafaker:datafaker:$dataFakerVersion")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))


	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
	testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
