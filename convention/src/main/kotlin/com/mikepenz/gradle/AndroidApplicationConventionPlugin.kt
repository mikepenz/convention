package com.mikepenz.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.HasUnitTestBuilder
import com.mikepenz.gradle.utils.libs
import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            val localProperties = readLocalProperties()
            val minSdk = project.readPropertyOrElse("com.mikepenz.android.minSdk", libs.findVersion("minSdk").get().requiredVersion, localProperties)?.toInt()
            val compatPatrouille = project.readPropertyOrElse("com.mikepenz.compatPatrouille.enabled", "true", localProperties).toString()
            val tapmoc = project.readPropertyOrElse("com.mikepenz.tapmoc.enabled", compatPatrouille, localProperties).toString().toBoolean()
            val compose = project.readPropertyOrElse("com.mikepenz.compose.enabled", "true", localProperties).toBoolean()

            extensions.configure(ApplicationExtension::class.java) {
                it.apply {
                    compileSdk {
                        version = release(libs.findVersion("compileSdk").get().requiredVersion.toInt())
                    }

                    defaultConfig {
                        this.minSdk = minSdk
                        this.targetSdk = libs.findVersion("targetSdk").get().requiredVersion.toInt()
                        this.versionCode = property("VERSION_CODE")?.toString()?.toInt()
                        this.versionName = property("VERSION_NAME")?.toString()
                        this.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }

                    buildFeatures {
                        this.compose = compose
                    }

                    if (!tapmoc) {
                        val javaVersion = readPropertyOrElse("com.mikepenz.java.version", "17", localProperties)!!.toInt()
                        compileOptions {
                            sourceCompatibility = JavaVersion.forClassVersion(javaVersion + 44)
                            targetCompatibility = JavaVersion.forClassVersion(javaVersion + 44)
                        }
                    }

                    val signing = project.readPropertyOrElse("com.mikepenz.android.signing.enabled", "false", localProperties).toBoolean()
                    if (signing) {
                        val variant = project.readPropertyOrElse("com.mikepenz.android.signing.variant", null, localProperties)?.let { ".$it" } ?: ""
                        val storeFileProp = project.readPropertyOrElse("com.mikepenz.android.signing.storeFile${variant}", "", localProperties) ?: ""
                        val storePasswordProp = project.readPropertyOrElse("com.mikepenz.android.signing.storePassword${variant}", "", localProperties)
                        val keyAliasProp = project.readPropertyOrElse("com.mikepenz.android.signing.keyAlias${variant}", "", localProperties)
                        val keyPasswordProp = project.readPropertyOrElse("com.mikepenz.android.signing.keyPassword${variant}", "", localProperties)

                        if (storeFileProp.isNotEmpty()) {
                            it.signingConfigs {
                                getByName("debug") { config ->
                                    config.storeFile = file(storeFileProp)
                                    config.storePassword = storePasswordProp
                                    config.keyAlias = keyAliasProp
                                    config.keyPassword = keyPasswordProp
                                }
                                create("release") { config ->
                                    config.storeFile = file(storeFileProp)
                                    config.storePassword = storePasswordProp
                                    config.keyAlias = keyAliasProp
                                    config.keyPassword = keyPasswordProp
                                }
                            }
                        }
                    }

                    buildTypes {
                        getByName("debug") { buildType ->
                            buildType.signingConfig = it.signingConfigs.findByName("debug")
                        }

                        getByName("release") { buildType ->
                            buildType.signingConfig = it.signingConfigs.findByName("release")
                            buildType.isMinifyEnabled = true
                            buildType.isShrinkResources = true
                            buildType.proguardFiles(
                                it.getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                            )
                        }
                    }

                    packaging {
                        resources.excludes.add("META-INF/licenses/**")
                        resources.excludes.add("META-INF/AL2.0")
                        resources.excludes.add("META-INF/LGPL2.1")
                    }
                }

                androidComponents {
                    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
                        (variantBuilder as? HasUnitTestBuilder)?.apply {
                            enableUnitTest = false
                        }
                    }
                }
            }
        }
    }
}
