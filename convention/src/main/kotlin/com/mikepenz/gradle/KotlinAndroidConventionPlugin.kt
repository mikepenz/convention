package com.mikepenz.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
            }

            configureJava() // Configure Java to use our chosen language level. Kotlin will automatically pick this up
            configureKotlin()
        }
    }
}