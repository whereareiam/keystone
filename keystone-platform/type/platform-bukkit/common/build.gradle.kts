plugins {
    id("module-java")
}

description = "What Keystone's Bukkit and Paper share: players, the plugin's log, scheduling and plugin presence through the Bukkit API"

dependencies {
    api(projects.platformCommon)
    implementation(projects.integrationIntercept)
    implementation(projects.integrationPlaceholderapi)

    compileOnly(libs.spigot)
    compileOnly(libs.bundles.adventure)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.bundles.adventure)
    testImplementation(libs.mockito)
    testImplementation(libs.spigot)
}
