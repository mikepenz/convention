rootProject.name = "convention-root"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("libs") {
            from(files("version-catalog/libs.versions.toml"))
        }
    }
}

pluginManagement {
    plugins {
        id("com.vanniktech.maven.publish") version "0.30.0" apply false
    }
}

include(":convention")
include(":version-catalog")
