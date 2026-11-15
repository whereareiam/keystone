plugins {
    id("module-java")
}

description = "Keystone on Bukkit and Spigot, through adventure-platform-bukkit and Semantica's format for Adventure 4; PlaceholderAPI when present"

dependencies {
    api(projects.platformBukkitCommon)
    api(libs.semantica.format.adventure.v4)
    api(libs.adventure.platform.bukkit)

    compileOnly(libs.spigot)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.mockito)
    testImplementation(libs.spigot)
}
