package com.mikepenz.gradle

import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
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
    mavenPublishing {
        if (pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml"), true, androidVariantsToPublish = listOf("release")))
        } else if (pluginManager.hasPlugin("org.jetbrains.kotlin.android")) {
            configure(AndroidSingleVariantLibrary())
        } else {
            throw IllegalStateException("Currently only supported for multiplatform or kotlin android projects")
        }

        publishToMavenCentral(true)
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