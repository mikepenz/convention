package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.*

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val localProperties = readLocalProperties()
        val multiplatformEnabled = project.readPropertyOrElse("com.mikepenz.multiplatform.enabled", "true", localProperties).toBoolean()
        if (multiplatformEnabled) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            val targetsEnabled = project.readPropertyOrElse("com.mikepenz.targets.enabled", "true", localProperties).toBoolean()
            if (targetsEnabled) {
                extensions.configure<KotlinMultiplatformExtension> {
                    configureMultiplatformTargets(project = target, localProperties = localProperties)
                }
            }
        }

        configureJava() // Configure Java to use our chosen language level. Kotlin will automatically pick this up
        configureKotlin(localProperties = localProperties)
    }
}

fun KotlinMultiplatformExtension.configureMultiplatformTargets(
    project: Project,
    localProperties: Properties? = project.readLocalProperties(),
) {
    // COMPOSE COMPATIBLE TARGETS START
    val androidEnabled = project.readPropertyOrElse("com.mikepenz.android.enabled", "true", localProperties).toBoolean()
    val jvmEnabled = project.readPropertyOrElse("com.mikepenz.jvm.enabled", "true", localProperties).toBoolean()
    val wasmEnabled = project.readPropertyOrElse("com.mikepenz.wasm.enabled", "true", localProperties).toBoolean()
    val jsEnabled = project.readPropertyOrElse("com.mikepenz.js.enabled", "true", localProperties).toBoolean()
    val composeNativeEnabled = project.readPropertyOrElse("com.mikepenz.composeNative.enabled", "true", localProperties).toBoolean()
    val nativeEnabled = project.readPropertyOrElse("com.mikepenz.native.enabled", "false", localProperties).toBoolean()
    // COMPOSE COMPATIBLE TARGETS FALSE

    applyDefaultHierarchyTemplate()

    // COMPOSE COMPATIBLE TARGETS START
    if (androidEnabled && project.pluginManager.hasPlugin("com.android.library")) {
        androidTarget {
            publishLibraryVariants("release")
        }
    }

    if (jvmEnabled) {
        jvm()
    }

    if (wasmEnabled) {
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            nodejs()
            browser()
        }
    }

    if (jsEnabled) {
        js(IR) {
            nodejs {}
            browser {}
            compilerOptions {
                moduleKind.set(JsModuleKind.MODULE_UMD)
                sourceMap.set(true)
                sourceMapEmbedSources.set(null)
            }
        }
    }

    if (composeNativeEnabled) {
        macosX64()
        macosArm64()

        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }
    // COMPOSE COMPATIBLE TARGETS END

    if (nativeEnabled) {
        // tier 2
        linuxX64()
        linuxArm64()
        watchosSimulatorArm64()
        watchosX64()
        watchosArm32()
        watchosArm64()
        tvosSimulatorArm64()
        tvosX64()
        tvosArm64()

        // tier 3
        // androidNativeArm32()
        // androidNativeArm64()
        // androidNativeX86()
        // androidNativeX64()
        mingwX64()
        watchosDeviceArm64()
    }
}

fun Project.configureKotlin(
    localProperties: Properties? = readLocalProperties(),
) {
    val warningsAsErrors = project.readPropertyOrElse("com.mikepenz.kotlin.warningsAsErrors.enabled", "true", localProperties).toString().toBoolean()

    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            allWarningsAsErrors.set(warningsAsErrors)

            if (this is KotlinJvmCompilerOptions) {
                jvmTarget.set(JvmTarget.JVM_17)
            }

            languageVersion.set(KotlinVersion.KOTLIN_2_0)
            apiVersion.set(KotlinVersion.KOTLIN_2_0)
        }
    }
}

fun Project.configureJava() {
    if (extensions.findByType(JavaPluginExtension::class.java) != null) {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }
}

private fun Project.java(action: JavaPluginExtension.() -> Unit) = extensions.configure<JavaPluginExtension>(action)
