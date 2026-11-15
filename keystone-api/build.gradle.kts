plugins {
    id("module-java")
}

description = "Keystone's contracts and models: recipients, displays and their parts, placeholders, language resolvers, the gate and player markup"

dependencies {
    api(libs.semantica.api)
    api(libs.semantica.format.adventure.common)

    compileOnlyApi(libs.bundles.adventure)
}

dependencies {
    testImplementation(libs.bundles.adventure)
}
