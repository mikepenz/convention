package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.DeploymentValidation
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.vanniktech.maven.publish")
        pluginManager.apply("org.jetbrains.dokka")

        configureDocumentation()
        configurePublishing()
    }
}

fun Project.configureDocumentation() {
    dokka {
        dokkaSourceSets.configureEach {
            it.enableAndroidDocumentationLink.set(true)
        }
    }
}


fun Project.configurePublishing() {
    val localProperties = readLocalProperties()
    mavenPublishing {
        if (pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml"), sourcesJar = SourcesJar.Sources(), androidVariantsToPublish = listOf("release")))
        } else if (pluginManager.hasPlugin("com.android.kotlin.multiplatform.library")) {
            configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml"), sourcesJar = SourcesJar.Sources()))
        } else if (pluginManager.hasPlugin("org.jetbrains.kotlin.android") || pluginManager.hasPlugin("com.android.library")) {
            configure(AndroidSingleVariantLibrary())
        } else {
            throw IllegalStateException("Currently only supported for multiplatform or kotlin android projects")
        }

        val targetsEnabled = readPropertyOrElse("com.mikepenz.publishing.autorelease", "true", localProperties).toBoolean()
        val validateDeployment = readPropertyOrElse("com.mikepenz.publishing.validate", "false", localProperties).toBoolean()
        publishToMavenCentral(automaticRelease = targetsEnabled, validateDeployment = if (validateDeployment) DeploymentValidation.VALIDATED else DeploymentValidation.NONE)
        signAllPublications()
    }

    publishing {
        repositories {
            it.maven { maven ->
                maven.name = "installLocally"
                maven.setUrl("${rootProject.layout.buildDirectory}/localMaven")
            }
        }
    }
}

private fun Project.publishing(action: PublishingExtension.() -> Unit) = extensions.configure(PublishingExtension::class.java, action)

private fun Project.mavenPublishing(action: com.vanniktech.maven.publish.MavenPublishBaseExtension.() -> Unit) {
    extensions.configure(com.vanniktech.maven.publish.MavenPublishBaseExtension::class.java, action)
}

private fun Project.dokka(action: org.jetbrains.dokka.gradle.DokkaExtension.() -> Unit) =
    extensions.configure(org.jetbrains.dokka.gradle.DokkaExtension::class.java, action)