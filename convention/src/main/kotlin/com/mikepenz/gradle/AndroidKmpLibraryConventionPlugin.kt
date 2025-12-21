package com.mikepenz.gradle

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.mikepenz.gradle.utils.libs
import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class AndroidKmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
            }

            val localProperties = readLocalProperties()

            extensions.configure(KotlinMultiplatformExtension::class.java) { kotlin ->
                kotlin.extensions.configure(KotlinMultiplatformAndroidLibraryTarget::class.java) {
                    it.apply {
                        this.compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()
                        val minSdk = project.readPropertyOrElse("com.mikepenz.android.minSdk", libs.findVersion("minSdk").get().requiredVersion, localProperties)?.toInt()
                        this.minSdk = minSdk
                    }
                }
            }
        }
    }
}