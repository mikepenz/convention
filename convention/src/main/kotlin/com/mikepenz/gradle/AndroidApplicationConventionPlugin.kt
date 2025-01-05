package com.mikepenz.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val compose = project.properties.getOrDefault("com.mikepenz.compose.enabled", "true").toString().toBoolean()

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

                val signing = project.properties.getOrDefault("com.mikepenz.android.signing.enabled", "false").toString().toBoolean()
                if (signing) {
                    val variant = project.properties.getOrDefault("com.mikepenz.android.signing.variant", "").toString().let { ".$it" }
                    val storeFileProp = project.properties.getOrDefault("com.mikepenz.android.signing.storeFile${variant}", "").toString()
                    val storePasswordProp = project.properties.getOrDefault("com.mikepenz.android.signing.storePassword${variant}", "").toString()
                    val keyAliasProp = project.properties.getOrDefault("com.mikepenz.android.signing.keyAlias${variant}", "").toString()
                    val keyPasswordProp = project.properties.getOrDefault("com.mikepenz.android.signing.keyPassword${variant}", "").toString()

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
