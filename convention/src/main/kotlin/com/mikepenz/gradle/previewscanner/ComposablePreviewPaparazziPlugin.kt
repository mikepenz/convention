package com.mikepenz.gradle.previewscanner

import com.mikepenz.gradle.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider

class ComposablePreviewPaparazziPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project.pluginManager) {
            apply(project.libs.findPlugin("paparazzi").get().get().pluginId)
        }

        // Create and configure the extension
        val extension = project.extensions.create(
            "composablePreviewPaparazzi",
            ComposablePreviewPaparazziExtension::class.java,
            project.objects
        )

        // Set default values
        extension.enable.convention(false)
        extension.packages.convention(emptyList())
        extension.includePrivatePreviews.convention(false)
        extension.testClassName.convention("GeneratedComposablePreviewPaparazziTests")
        extension.testPackageName.convention("generated.paparazzi.tests")

        // Configure the task after project evaluation
        project.afterEvaluate {
            if (extension.enable.get()) {
                setupGenerateComposablePreviewPaparazziTestsTask(project, extension)
            }
        }

        // project.dependencies.
        project.dependencies.apply {
            implementation(project.libs.findLibrary("composablePreviewScanner").get())
            implementation(project.libs.findLibrary("junit").get())
        }
    }
}

internal fun DependencyHandler.implementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("implementation", provider.get().group + ":" + provider.get().name + ":" + provider.get().version)
}