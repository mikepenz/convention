package com.mikepenz.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        configureCompose()
    }
}

internal fun Project.configureCompose() {
    composeCompiler {
        reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
        metricsDestination.set(layout.buildDirectory.dir("compose_compiler"))
    }
}

private fun Project.composeCompiler(action: ComposeCompilerGradlePluginExtension.() -> Unit) =
    extensions.configure(ComposeCompilerGradlePluginExtension::class.java, action)
