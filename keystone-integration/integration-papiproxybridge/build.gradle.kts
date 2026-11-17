plugins {
    id("module-java")
}

description = "Keystone's PAPIProxyBridge integration on Velocity and BungeeCord: PlaceholderAPI's %placeholders% of the player's backend server"

dependencies {
    api(projects.keystoneCommon)

    compileOnly(libs.papiproxybridge)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.bundles.adventure)
    testImplementation(libs.mockito)
}
