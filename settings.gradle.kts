import org.gradle.api.initialization.resolve.RepositoriesMode

rootProject.name = "Keystone"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://registry.whereareiam.me/maven/packages")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.extendedclip.com/releases/")
        maven("https://repo.william278.net/releases/")
    }
}

include(":keystone-api")
project(":keystone-api").projectDir = file("keystone-api")

include(":keystone-common")
project(":keystone-common").projectDir = file("keystone-common")

include(":platform-common")
project(":platform-common").projectDir = file("keystone-platform/common")

include(":platform-bukkit-common")
project(":platform-bukkit-common").projectDir = file("keystone-platform/type/platform-bukkit/common")

include(":platform-bukkit-bukkit")
project(":platform-bukkit-bukkit").projectDir = file("keystone-platform/type/platform-bukkit/bukkit")

include(":platform-bukkit-paper")
project(":platform-bukkit-paper").projectDir = file("keystone-platform/type/platform-bukkit/paper")

include(":platform-proxy-velocity")
project(":platform-proxy-velocity").projectDir = file("keystone-platform/type/platform-proxy/velocity")

include(":platform-proxy-bungeecord")
project(":platform-proxy-bungeecord").projectDir = file("keystone-platform/type/platform-proxy/bungeecord")

include(":integration-placeholderapi")
project(":integration-placeholderapi").projectDir = file("keystone-integration/integration-placeholderapi")

include(":integration-papiproxybridge")
project(":integration-papiproxybridge").projectDir = file("keystone-integration/integration-papiproxybridge")

include(":integration-miniplaceholders")
project(":integration-miniplaceholders").projectDir = file("keystone-integration/integration-miniplaceholders")

include(":integration-intercept")
project(":integration-intercept").projectDir = file("keystone-integration/integration-intercept")
