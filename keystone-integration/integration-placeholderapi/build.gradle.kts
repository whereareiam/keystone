plugins {
    id("module-java")
}

description = "Keystone's PlaceholderAPI integration on Bukkit and Paper: %placeholders% in what translators and admins wrote"

dependencies {
    api(projects.keystoneCommon)

    compileOnly(libs.spigot)
    compileOnly(libs.placeholderapi)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.bundles.adventure)
    testImplementation(libs.mockito)
    testImplementation(libs.placeholderapi)
    testImplementation(libs.spigot)
}
