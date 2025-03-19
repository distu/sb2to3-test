import org.owasp.dependencycheck.reporting.ReportGenerator.Format

plugins {
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.owasp.dependencycheck") version "12.1.0"
    id("com.google.cloud.tools.jib") version "3.4.4"
    id("java")
    jacoco
}

group = "br.rnp"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://gitlab.rnp.br/api/v4/groups/638/-/packages/maven")
        credentials(HttpHeaderCredentials::class.java) {
            val jobToken = System.getenv("CI_JOB_TOKEN")
            if (jobToken != null) {
                // GitLab CI
                name = "Job-Token"
                value = System.getenv("CI_JOB_TOKEN")
            } else {
                name = "Private-Token"
                value = System.getenv("GITLAB_TOKEN")
            }
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("br.rnp:rnp-bom:2.0.0")
        mavenBom("org.testcontainers:testcontainers-bom:1.20.5")
    }
}

dependencies {
    implementation("br.rnp:rnp-core:3.0.0")
    implementation("br.rnp:rnp-core-onpremise:2.0.0")
    implementation("br.rnp:rnp-core-storage-minio:2.0.0")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.liquibase:liquibase-core")
    implementation("org.mapstruct:mapstruct:1.6.3")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:testcontainers:1.20.5")
    testImplementation("org.testcontainers:postgresql:1.20.5")
    testImplementation("com.github.dasniko:testcontainers-keycloak:2.5.0")
    testImplementation("org.testcontainers:junit-jupiter:1.20.5")
    testImplementation("com.tngtech.archunit:archunit-junit5:0.23.1")

    testCompileOnly("org.projectlombok:lombok:1.18.36")

    annotationProcessor("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    if (System.getProperty("os.name").equals("Mac OS X") && System.getProperty("os.arch").equals("aarch64")) {
        testImplementation(group = "io.netty", name = "netty-resolver-dns-native-macos", version = "4.1.119.Final", classifier = "osx-aarch_64")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+UseCompressedClassPointers -XX:+UseZGC -XX:+ZUncommit -XX:+UseDynamicNumberOfGCThreads -XX:-OmitStackTraceInFastThrow -XX:+OptimizeStringConcat -server -ea -Xms8m -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError".split(" "))
    configure<JacocoTaskExtension> {
        excludes = listOf("br.rnp.pgf.tests.*", "br.rnp.pgf.mocks.*", "br.rnp.pgf.framework.initializr.*", "br.rnp.pgf.config.aws.*", "**/generated/**")
    }
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("reports/jacoco/html").get().asFile
    }
}

dependencyCheck {
    failOnError = false
    format = Format.ALL.name
    failBuildOnCVSS = 7F
    suppressionFile = "dependency-check-known-issues.xml"
}

jib {
    from {
        image = "gcr.io/distroless/java17:nonroot"
    }
    containerizingMode = "packaged"
    container {
        environment = mapOf(
                Pair("JAVA_OPTS", "-server -Dfile.encode=UTF-8 -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:+UseZGC -XX:+UseDynamicNumberOfGCThreads -XX:+UseContainerSupport -Xms64m -XX:MaxRAMPercentage=90 -XX:+OptimizeStringConcat -XX:+UseStringDeduplication"),
                Pair("TZ", "America/Sao_Paulo"),
                Pair("LANG", "pt_BR.UTF-8"),
                Pair("LANGUAGE", "pt_BR.UTF-8"),
                Pair("LC_ALL", "pt_BR.UTF-8"),
        )
        ports = listOf("8080", "7188")
    }
    to {
        credHelper {
            helper = "ecr-login"
        }
    }
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}