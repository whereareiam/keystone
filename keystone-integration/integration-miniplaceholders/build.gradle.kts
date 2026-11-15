plugins {
    id("module-java")
}

description = "Keystone's MiniPlaceholders integration on Paper and Velocity: MiniPlaceholders' global tags in what translators and admins wrote"

dependencies {
    api(projects.keystoneCommon)

    compileOnly(libs.bundles.adventure)
    compileOnly(libs.miniplaceholders)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.bundles.adventure)
    testImplementation(libs.miniplaceholders)
    testImplementation(libs.mockito)
}
