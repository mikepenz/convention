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

            configureBaseAndroid()

            android {
                buildFeatures.compose = compose

                defaultConfig {
                    versionCode = property("VERSION_CODE").toString().toInt()
                    versionName = property("VERSION_NAME").toString()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                val signing = project.readPropertyOrElse("com.mikepenz.android.signing.enabled", "false", localProperties).toBoolean()
                if (signing) {
                    val variant = project.readPropertyOrElse("com.mikepenz.android.signing.variant", null, localProperties)?.let { ".$it" } ?: ""
                    val storeFileProp = project.readPropertyOrElse("com.mikepenz.android.signing.storeFile${variant}", "", localProperties) ?: ""
                    val storePasswordProp = project.readPropertyOrElse("com.mikepenz.android.signing.storePassword${variant}", "", localProperties)
                    val keyAliasProp = project.readPropertyOrElse("com.mikepenz.android.signing.keyAlias${variant}", "", localProperties)
                    val keyPasswordProp = project.readPropertyOrElse("com.mikepenz.android.signing.keyPassword${variant}", "", localProperties)

                    signingConfigs {
                        getByName("debug") {
                            storeFile = file(storeFileProp)
                            storePassword = storePasswordProp
                            keyAlias = keyAliasProp
                            keyPassword = keyPasswordProp
                        }
                        create("release") {
                            storeFile = file(storeFileProp)
                            storePassword = storePasswordProp
                            keyAlias = keyAliasProp
                            keyPassword = keyPasswordProp
                        }
                    }
                }

                buildTypes {
                    getByName("debug") {
                        signingConfig = signingConfigs.findByName("debug")
                    }

                    getByName("release") {
                        signingConfig = signingConfigs.findByName("release")
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                        )
                    }
                }

                packagingOptions {
                    resources.excludes.add("META-INF/licenses/**")
                    resources.excludes.add("META-INF/AL2.0")
                    resources.excludes.add("META-INF/LGPL2.1")
                }
            }
        }
    }
}
