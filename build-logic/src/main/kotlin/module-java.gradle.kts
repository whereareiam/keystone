import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    `java-library`
    `maven-publish`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

group = "me.whereareiam"
version = providers.environmentVariable("VERSION").orElse("dev").get()

// Compilation and tests use Java 21 whichever Java runs Gradle; Semantica needs it.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

dependencies {
    add("compileOnly", libs.findLibrary("jetbrains-annotations").get())
    add("compileOnly", libs.findLibrary("lombok").get())

    add("annotationProcessor", libs.findLibrary("lombok").get())

    add("testImplementation", libs.findLibrary("junit-jupiter").get())

    add("testCompileOnly", libs.findLibrary("jetbrains-annotations").get())
    add("testCompileOnly", libs.findLibrary("lombok").get())
    add("testAnnotationProcessor", libs.findLibrary("lombok").get())

    add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // Mockito attaches its agent to mock platform types.
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
}

// Published as keystone-<project>, such as keystone-api or keystone-platform-bukkit-paper.
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = if (project.name.startsWith("keystone-")) project.name else "keystone-${project.name}"

            pom {
                url.set("https://github.com/whereareiam/keystone")
                description.set(provider { project.description })
            }
        }
    }

    repositories {
        maven {
            val base = providers.environmentVariable("PUBLISH_MAVEN_BASE_URL")
                .orElse("https://registry.whereareiam.me/maven")
                .get()
            val repository = providers.environmentVariable("PUBLISH_MAVEN_REPOSITORY")
                .orElse("packages")
                .get()

            url = uri("$base/$repository")

            credentials {
                username = providers.environmentVariable("PUBLISH_USER").orNull
                password = providers.environmentVariable("PUBLISH_TOKEN").orNull
            }
        }
    }
}
