package com.mikepenz.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val variant = project.properties.getOrDefault("com.mikepenz.kotlin.variant", "android").toString()

            with(pluginManager) {
                apply("org.jetbrains.kotlin.$variant")
            }

            if (variant == "multiplatform") {
                val targetsEnabled = project.properties.getOrDefault("com.mikepenz.targets.enabled", "true").toString().toBoolean()
                if (targetsEnabled) {
                    extensions.configure<KotlinMultiplatformExtension> {
                        configureMultiplatformTargets(target)
                    }
                }
            }

            configureJava() // Configure Java to use our chosen language level. Kotlin will automatically pick this up
            configureKotlin()
        }
    }
}