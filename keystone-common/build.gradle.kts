plugins {
    id("module-java")
    `java-test-fixtures`
}

description = "Keystone's delivery: rendering through Semantica per recipient, display.yml, the language chain, the gate, player markup, the console with ANSI colours and the Configura module of contents and displays"

dependencies {
    api(projects.keystoneApi)
    api(libs.semantica.common)
    api(libs.configura)

    compileOnly(libs.bundles.adventure)

    testFixturesApi(libs.bundles.adventure)
    testFixturesApi(libs.adventure.plain)
    testFixturesApi(libs.semantica.syntax.icu)
    testFixturesApi(libs.semantica.testing)
    testFixturesImplementation(libs.semantica.format.adventure.v5)
    testFixturesCompileOnly(libs.jetbrains.annotations)
    testFixturesCompileOnly(libs.lombok)
    testFixturesAnnotationProcessor(libs.lombok)

    testImplementation(libs.semantica.format.adventure.v5)
}

// Test fixtures are for Keystone's own tests; they are not published.
val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
