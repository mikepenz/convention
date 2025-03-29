package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import kotlinx.validation.ApiValidationExtension
import nl.littlerobots.vcu.plugin.versionCatalogUpdate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class RootConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val flagKey = "com.mikepenz.binary-compatibility-validator.enabled"
        val localProperties = readLocalProperties()
        val binaryCompatibilityValidatorEnabled = project.readPropertyOrElse(flagKey, "false", localProperties).toBoolean()

        if (binaryCompatibilityValidatorEnabled) {
            pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")

            apiValidation {
                @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
                klib {
                    enabled = true
                }

                ignoredProjects.addAll(allprojects.filter {
                    val projectLocalProperties = it.readLocalProperties()
                    val projBinaryCompatibilityValidatorEnabled = it.readPropertyOrElse(flagKey, "true", projectLocalProperties).toBoolean()
                    if (projBinaryCompatibilityValidatorEnabled) it.name.contains("app") else true
                }.map { it.name })
            }
        }

        val versionCatalogUpdateEnabled = project.readPropertyOrElse("com.mikepenz.version-catalog-update.enabled", "false", localProperties).toBoolean()
        if (versionCatalogUpdateEnabled) {
            pluginManager.apply("nl.littlerobots.version-catalog-update")

            versionCatalogUpdate {
                sortByKey.set(false)

                keep {
                    keepUnusedVersions.set(true)
                }
            }
        }
    }
}


internal fun Project.apiValidation(action: ApiValidationExtension.() -> Unit) = extensions.configure<ApiValidationExtension>(action)