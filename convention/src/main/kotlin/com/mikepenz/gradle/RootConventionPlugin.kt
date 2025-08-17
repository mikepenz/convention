package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import nl.littlerobots.vcu.plugin.versionCatalogUpdate
import org.gradle.api.Plugin
import org.gradle.api.Project

class RootConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val flagKey = "com.mikepenz.binary-compatibility-validator.enabled"
        val localProperties = readLocalProperties()
        val binaryCompatibilityValidatorEnabled = project.readPropertyOrElse(flagKey, "false", localProperties).toBoolean()
        if (binaryCompatibilityValidatorEnabled) {
            pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")
            applyBinaryCompatibilityValidator(flagKey)
        }

        val versionCatalogUpdateEnabled = project.readPropertyOrElse("com.mikepenz.version-catalog-update.enabled", "false", localProperties).toBoolean()
        if (versionCatalogUpdateEnabled) {
            pluginManager.apply("nl.littlerobots.version-catalog-update")

            versionCatalogUpdate {
                sortByKey.set(false)

                keep {
                    it.keepUnusedVersions.set(true)
                }
            }
        }

        val javaResolverResolver = project.readPropertyOrElse("com.mikepenz.ktlint.enabled", "false", localProperties).toBoolean()
        if (javaResolverResolver) {
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            subprojects {
                this.pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            }
        }
    }
}
