import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    java
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("org.openapi.generator") version "7.0.0-beta"
}

group = "net.prasenjit.poc"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val jaxWs: Configuration = configurations.create("jaxWs")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    implementation("org.springframework.boot:spring-boot-starter-web-services")

    jaxWs("com.sun.xml.ws:jaxws-tools:3.0.0")
    jaxWs("jakarta.xml.ws:jakarta.xml.ws-api:3.0.0")
    jaxWs("jakarta.xml.bind:jakarta.xml.bind-api:3.0.0")
    jaxWs("jakarta.activation:jakarta.activation-api:2.0.0")
    jaxWs("com.sun.xml.ws:jaxws-rt:3.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val specDir = "$projectDir/src/main/resources/spec"
val generatedDir = "$buildDir/generated"

val oasImport = tasks.register<GenerateTask>("oasImport") {
    generatorName.set("spring")
    cleanupOutput.set(true)
    inputSpec.set("$specDir/api.yaml")
    apiPackage.set("net.prasenjit.poc.bootmongo.spec.rest")
    modelPackage.set("net.prasenjit.poc.bootmongo.spec.rest.model")
    outputDir.set("$generatedDir/rest")
    configOptions.set(mapOf(
            "dateLibrary" to "java8",
            "openApiNullable" to "false",
            "interfaceOnly" to "true",
            "implicitHeaders" to "true",
            "useSpringController" to "true",
            "oas3" to "true",
            "useOptional" to "true",
            "useSpringBoot3" to "true"
    ))
}

val wsdlPath = "$specDir/backend.wsdl"
val outputPath = "$generatedDir/soap"
val wsdlPackageName = "net.prasenjit.poc.bootmongo.spec.soap"

val wsImport: Task = tasks.create("wsImport") {
    description = "Import WSDL file"
    doLast {
        project.mkdir(outputPath)
        ant.withGroovyBuilder {
            "taskdef"("name" to "wsImport", "classname" to "com.sun.tools.ws.ant.WsImport", "classpath" to jaxWs.asPath)
            "wsImport"("keep" to true, "destDir" to outputPath, "extension" to true, "xnocompile" to true, "wsdl" to wsdlPath, "verbose" to true, "package" to wsdlPackageName) {
                "xjcarg"("value" to "-XautoNameResolution")
            }
        }
    }
}

sourceSets.main {
    java.srcDir(listOf("$generatedDir/rest/src/main/java", outputPath))
}

val codeGen = tasks.create("codeGen"){
    dependsOn(wsImport, oasImport)
}

tasks.named("compileJava").get().dependsOn(codeGen)