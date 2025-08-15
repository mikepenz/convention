package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val localProperties = readLocalProperties()
            val compose = project.readPropertyOrElse("com.mikepenz.compose.enabled", "true", localProperties).toBoolean()

            with(pluginManager) {
                apply("com.android.application")
            }

            configureBaseAndroid(localProperties)

            android {
                buildFeatures.compose = compose

                defaultConfig {
                    it.versionCode = property("VERSION_CODE")?.toString()?.toInt()
                    it.versionName = property("VERSION_NAME")?.toString()
                    it.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                val signing = project.readPropertyOrElse("com.mikepenz.android.signing.enabled", "false", localProperties).toBoolean()
                if (signing) {
                    val variant = project.readPropertyOrElse("com.mikepenz.android.signing.variant", null, localProperties)?.let { ".$it" } ?: ""
                    val storeFileProp = project.readPropertyOrElse("com.mikepenz.android.signing.storeFile${variant}", "", localProperties) ?: ""
                    val storePasswordProp = project.readPropertyOrElse("com.mikepenz.android.signing.storePassword${variant}", "", localProperties)
                    val keyAliasProp = project.readPropertyOrElse("com.mikepenz.android.signing.keyAlias${variant}", "", localProperties)
                    val keyPasswordProp = project.readPropertyOrElse("com.mikepenz.android.signing.keyPassword${variant}", "", localProperties)

                    signingConfigs {
                        it.getByName("debug") { config ->
                            config.storeFile = file(storeFileProp)
                            config.storePassword = storePasswordProp
                            config.keyAlias = keyAliasProp
                            config.keyPassword = keyPasswordProp
                        }
                        it.create("release") { config ->
                            config.storeFile = file(storeFileProp)
                            config.storePassword = storePasswordProp
                            config.keyAlias = keyAliasProp
                            config.keyPassword = keyPasswordProp
                        }
                    }
                }

                buildTypes {
                    it.getByName("debug") { buildType ->
                        buildType.signingConfig = signingConfigs.findByName("debug")
                    }

                    it.getByName("release") { buildType ->
                        buildType.signingConfig = signingConfigs.findByName("release")
                        buildType.isMinifyEnabled = true
                        buildType.isShrinkResources = true
                        buildType.proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                        )
                    }
                }

                packagingOptions {
                    it.resources.excludes.add("META-INF/licenses/**")
                    it.resources.excludes.add("META-INF/AL2.0")
                    it.resources.excludes.add("META-INF/LGPL2.1")
                }
            }
        }
    }
}
