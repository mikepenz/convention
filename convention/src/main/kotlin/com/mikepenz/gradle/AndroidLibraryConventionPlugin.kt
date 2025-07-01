package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            val localProperties = readLocalProperties()
            configureBaseAndroid(localProperties)
        }
    }
}
