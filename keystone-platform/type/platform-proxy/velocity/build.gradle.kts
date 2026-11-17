plugins {
    id("module-java")
}

description = "Keystone on Velocity, with Velocity's own Adventure and Semantica's format for Adventure 5; PAPIProxyBridge and MiniPlaceholders when present"

dependencies {
    api(projects.platformCommon)
    api(libs.semantica.format.adventure.v5)
    implementation(projects.integrationIntercept)
    implementation(projects.integrationPapiproxybridge)
    implementation(projects.integrationMiniplaceholders)

    compileOnly(libs.velocity)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.mockito)
    testImplementation(libs.velocity)
}
