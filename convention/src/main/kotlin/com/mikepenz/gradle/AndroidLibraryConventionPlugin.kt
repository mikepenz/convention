package com.mikepenz.gradle

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.HasUnitTestBuilder
import com.mikepenz.gradle.utils.libs
import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            val localProperties = readLocalProperties()
            val minSdk = project.readPropertyOrElse("com.mikepenz.android.minSdk", libs.findVersion("minSdk").get().requiredVersion, localProperties)?.toInt()
            val compatPatrouille = project.readPropertyOrElse("com.mikepenz.compatPatrouille.enabled", "true", localProperties).toString()
            val tapmoc = project.readPropertyOrElse("com.mikepenz.tapmoc.enabled", compatPatrouille, localProperties).toString().toBoolean()

            extensions.configure(LibraryExtension::class.java) {
                it.apply {
                    compileSdk {
                        version = release(libs.findVersion("compileSdk").get().requiredVersion.toInt())
                    }

                    defaultConfig {
                        this.minSdk = minSdk
                    }

                    if (!tapmoc) {
                        val javaVersion = readPropertyOrElse("com.mikepenz.java.version", "17", localProperties)!!.toInt()
                        compileOptions {
                            sourceCompatibility = JavaVersion.forClassVersion(javaVersion + 44)
                            targetCompatibility = JavaVersion.forClassVersion(javaVersion + 44)
                        }
                    }

                    buildTypes({
                        getByName("release") { buildType ->
                            buildType.isMinifyEnabled = false
                            buildType.proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                            )
                        }
                    })
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
