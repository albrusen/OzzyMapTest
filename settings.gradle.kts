pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OzzyMapTest"
include(":app")
include(":core")
include(":featureMaps")
include(":featureMaps:domain")
include(":featureMaps:data")
include(":featureMaps:presentation")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":featureHome")
include(":featureHome:presentation")
