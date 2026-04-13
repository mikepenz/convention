package com.mikepenz.gradle

import nl.littlerobots.vcu.plugin.versionCatalogUpdate
import org.gradle.api.Project


fun Project.applyVersionCatalogUpdate() {
    pluginManager.apply("nl.littlerobots.version-catalog-update")

    versionCatalogUpdate {
        sortByKey.set(false)

        keep {
            it.keepUnusedVersions.set(true)
        }
    }
}
