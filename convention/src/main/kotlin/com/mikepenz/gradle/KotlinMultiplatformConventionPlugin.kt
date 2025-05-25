package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import compat.patrouille.CompatPatrouilleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.*

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val localProperties = readLocalProperties()
        val multiplatformEnabled = readPropertyOrElse("com.mikepenz.multiplatform.enabled", "true", localProperties).toBoolean()
        if (multiplatformEnabled) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            val targetsEnabled = readPropertyOrElse("com.mikepenz.targets.enabled", "true", localProperties).toBoolean()
            if (targetsEnabled) {
                extensions.configure<KotlinMultiplatformExtension> {
                    configureMultiplatformTargets(project = target, localProperties = localProperties)
                }
            }
        }

        val hotReloadEnabled = readPropertyOrElse("com.mikepenz.hotreload.enabled", "false", localProperties).toBoolean()
        if (hotReloadEnabled) {
            with(pluginManager) {
                apply("org.jetbrains.compose")
                apply("org.jetbrains.compose.hot-reload")
            }
        }

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
        }
    }

    val javaVersion = readPropertyOrElse("com.mikepenz.java.version", "17", localProperties)!!.toInt()
    val kotlinVersion = readPropertyOrElse("com.mikepenz.kotlin.version", "2.1.21", localProperties)!!
    compatPatrouille {
        java(javaVersion)
        kotlin(kotlinVersion)
    }
}

internal fun Project.compatPatrouille(action: CompatPatrouilleExtension.() -> Unit) = extensions.configure<CompatPatrouilleExtension>(action)