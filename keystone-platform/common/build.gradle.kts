plugins {
    id("module-java")
}

description = "What every platform of Keystone shares: the builder, its language and placeholder options, and Semantica's Adventure formats for Adventure 4 and 5"

dependencies {
    api(projects.keystoneCommon)

    compileOnly(libs.bundles.adventure)
    compileOnly(libs.semantica.format.adventure.v4)
    compileOnly(libs.semantica.format.adventure.v5)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.adventure.plain)
    testImplementation(libs.bundles.adventure)
    testImplementation(libs.semantica.format.adventure.v4)
    testImplementation(libs.semantica.format.adventure.v5)
    testImplementation(libs.semantica.syntax.icu)
}

dependencies {
    testImplementation(libs.semantica.testing)
}
