plugins {
    id("module-java")
    // ModDevGradle, on the build's classpath through its settings plugin (settings.gradle.kts)
    id("net.neoforged.moddev")
}

description = "Keystone on NeoForge servers, through adventure-platform-neoforge and Semantica's format for Adventure 4"

// Compiles against Minecraft and NeoForge, which the mod that uses Keystone runs. The tests run inside NeoForge, without
// starting a game, so that the game's types initialise; Keystone's classes and the tests form a mod of the tests' own
// (src/test/resources/META-INF/neoforge.mods.toml), which puts them in the game's layer as the jar's manifest does.
neoForge {
    version = libs.versions.neoforge.get()

    mods {
        create("keystone_test") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.test.get())
        }
    }

    unitTest {
        enable()
        testedMod = mods["keystone_test"]
    }
}

dependencies {
    api(projects.platformCommon)
    api(libs.semantica.format.adventure.v4)
    api(libs.adventure.platform.neoforge)
    api(libs.bundles.adventure.neoforge)

    testImplementation(testFixtures(projects.keystoneCommon))
    testImplementation(libs.mockito)
}

// Compiles and tests against the Adventure Keystone brings to NeoForge, the one it runs on, rather than Keystone's own.
configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "net.kyori" && requested.name.startsWith("adventure") && !requested.name.startsWith("adventure-platform"))
            useVersion(libs.versions.adventure.neoforge.get())
    }
}

// It refers to Minecraft's classes: NeoForge loads it in the game's layer, also when a mod nests it.
tasks.jar {
    manifest {
        attributes("FMLModType" to "GAMELIBRARY", "Automatic-Module-Name" to "me.whereareiam.keystone.platform.neoforge")
    }
}

// Minecraft 1.21.1 pins an older ICU4J than Semantica's; the tests run on Semantica's, as the mod that nests it does.
configurations.matching { it.name.startsWith("test") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.ibm.icu" && requested.name == "icu4j")
            useVersion(libs.versions.icu4j.get())
    }
}
