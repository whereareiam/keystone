plugins {
    id("module-java")
}

description = "Keystone on BungeeCord, through adventure-platform-bungeecord and Semantica's format for Adventure 4; PAPIProxyBridge when present"

dependencies {
    api(projects.platformCommon)
    api(libs.semantica.format.adventure.v4)
    api(libs.adventure.platform.bungeecord)
    implementation(projects.integrationPapiproxybridge)

    compileOnly(libs.bungeecord)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.bungeecord)
    testImplementation(libs.mockito)
}
