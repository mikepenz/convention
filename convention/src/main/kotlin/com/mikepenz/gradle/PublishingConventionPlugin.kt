package com.mikepenz.gradle

import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.jetbrains.dokka.gradle.DokkaExtension

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

private fun Project.mavenPublishing(action: MavenPublishBaseExtension.() -> Unit) {
    extensions.configure(MavenPublishBaseExtension::class.java, action)
}

private fun Project.dokka(action: DokkaExtension.() -> Unit) =
    extensions.configure(DokkaExtension::class.java, action)