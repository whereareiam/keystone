plugins {
    id("module-java")
}

description = "Keystone on Paper, with Paper's own Adventure and Semantica's format for Adventure 5; PlaceholderAPI and MiniPlaceholders when present"

dependencies {
    api(projects.platformBukkitCommon)
    api(libs.semantica.format.adventure.v5)
    implementation(projects.integrationMiniplaceholders)

    compileOnly(libs.paper)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.mockito)
    testImplementation(libs.paper)
}

// Paper's API is built on its own Adventure, so its tests run on that one rather than on Keystone's.
configurations.matching { it.name.startsWith("test") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "net.kyori" && requested.name.startsWith("adventure"))
            useVersion(libs.versions.adventure.paper.get())
    }
}
