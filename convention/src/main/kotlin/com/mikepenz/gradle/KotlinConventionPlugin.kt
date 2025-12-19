package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val localProperties = readLocalProperties()
            val variant = readPropertyOrElse("com.mikepenz.kotlin.variant", "android", localProperties).toString()

            with(pluginManager) {
                apply("org.jetbrains.kotlin.$variant")
            }

            if (variant == "multiplatform") {
                val targetsEnabled = readPropertyOrElse("com.mikepenz.targets.enabled", "true", localProperties).toBoolean()
                if (targetsEnabled) {
                    extensions.configure(KotlinMultiplatformExtension::class.java) {
                        it.configureMultiplatformTargets(project = target, localProperties = localProperties)
                    }
                }
            }

            val compatPatrouille = project.readPropertyOrElse("com.mikepenz.compatPatrouille.enabled", "true", localProperties).toString()
            val tapmoc = project.readPropertyOrElse("com.mikepenz.tapmoc.enabled", compatPatrouille, localProperties).toString().toBoolean()
            if (!tapmoc) configureJava(localProperties = localProperties) // Configure Java to use our chosen language level. Kotlin will automatically pick this up
            configureKotlin(localProperties = localProperties)
        }
    }
}
