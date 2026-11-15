plugins {
    id("module-java")
}

description = "Keystone's Intercept integration on Paper and Velocity: the language a player chose in Intercept comes first in their languages"

dependencies {
    api(projects.keystoneCommon)

    // Intercept's own API, which the server loads from Intercept; never shaded or relocated.
    compileOnly(libs.intercept.api)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.bundles.adventure)
    testImplementation(libs.intercept.api)
    testImplementation(libs.mockito)
}
