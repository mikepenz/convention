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
        id("com.vanniktech.maven.publish") version "0.31.0" apply false
        id("nl.littlerobots.version-catalog-update") version "1.0.0"
    }
}

include(":convention")
include(":settings-convention")
include(":version-catalog")
