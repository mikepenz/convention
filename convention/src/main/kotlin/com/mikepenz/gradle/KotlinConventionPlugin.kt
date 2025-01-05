package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val localProperties = readLocalProperties()
            val variant = project.readPropertyOrElse("com.mikepenz.kotlin.variant", "android", localProperties).toString()

            with(pluginManager) {
                apply("org.jetbrains.kotlin.$variant")
            }

            if (variant == "multiplatform") {
                val targetsEnabled = project.readPropertyOrElse("com.mikepenz.targets.enabled", "true", localProperties).toBoolean()
                if (targetsEnabled) {
                    extensions.configure<KotlinMultiplatformExtension> {
                        configureMultiplatformTargets(project = target, localProperties = localProperties)
                    }
                }
            }

            configureJava() // Configure Java to use our chosen language level. Kotlin will automatically pick this up
            configureKotlin(localProperties = localProperties)
        }
    }
}