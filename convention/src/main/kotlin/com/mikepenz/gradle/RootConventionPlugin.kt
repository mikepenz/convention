package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import kotlinx.validation.ApiValidationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class RootConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val localProperties = readLocalProperties()
        val binaryCompatibilityValidatorEnabled = project.readPropertyOrElse("com.mikepenz.binary-compatibility-validator.enabled", "false", localProperties).toBoolean()

        if (binaryCompatibilityValidatorEnabled) {
            pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")

            apiValidation {
                @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
                klib {
                    enabled = true
                }

                ignoredProjects.addAll(allprojects.filter { it.name.contains("app") }.map { it.name })
            }
        }
    }
}


internal fun Project.apiValidation(action: ApiValidationExtension.() -> Unit) = extensions.configure<ApiValidationExtension>(action)