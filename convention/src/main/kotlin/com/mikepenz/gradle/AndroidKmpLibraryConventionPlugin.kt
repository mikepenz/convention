package com.mikepenz.gradle

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
            androidKmpLibrary {
                compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()
                val minSdk =
                    project.readPropertyOrElse("com.mikepenz.android.minSdk", "${libs.findVersion("minSdk").get().requiredVersion.toInt()}", localProperties).toString().toInt()
                this.minSdk = minSdk
            }
        }
    }
}

internal fun Project.androidKmpLibrary(action: com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget.() -> Unit) =
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        it.extensions.configure(com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget::class.java, action)
    }
