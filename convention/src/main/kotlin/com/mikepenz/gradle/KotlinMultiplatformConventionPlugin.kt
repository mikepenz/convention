package com.mikepenz.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val multiplatformEnabled = project.properties.getOrDefault("com.mikepenz.multiplatform.enabled", "true").toString().toBoolean()
        if (multiplatformEnabled) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            val targetsEnabled = project.properties.getOrDefault("com.mikepenz.targets.enabled", "true").toString().toBoolean()
            if (targetsEnabled) {
                extensions.configure<KotlinMultiplatformExtension> {
                    configureMultiplatformTargets(target)
                }
            }
        }

        configureJava() // Configure Java to use our chosen language level. Kotlin will automatically pick this up
        configureKotlin()
    }
}

fun KotlinMultiplatformExtension.configureMultiplatformTargets(project: Project) {
    // COMPOSE COMPATIBLE TARGETS START
    val androidEnabled = project.properties.getOrDefault("com.mikepenz.android.enabled", "true").toString().toBoolean()
    val jvmEnabled = project.properties.getOrDefault("com.mikepenz.jvm.enabled", "true").toString().toBoolean()
    val wasmEnabled = project.properties.getOrDefault("com.mikepenz.wasm.enabled", "true").toString().toBoolean()
    val jsEnabled = project.properties.getOrDefault("com.mikepenz.js.enabled", "true").toString().toBoolean()
    val composeNativeEnabled = project.properties.getOrDefault("com.mikepenz.composeNative.enabled", "true").toString().toBoolean()
    val nativeEnabled = project.properties.getOrDefault("com.mikepenz.native.enabled", "false").toString().toBoolean()
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

fun Project.configureKotlin() {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            allWarningsAsErrors.set(true)

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
