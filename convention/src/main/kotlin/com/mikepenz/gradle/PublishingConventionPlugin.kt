package com.mikepenz.gradle

import com.vanniktech.maven.publish.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke

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
        dokkaSourceSets {
            configureEach {
                enableAndroidDocumentationLink.set(true)
            }
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

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)
        signAllPublications()
    }

    publishing {
        repositories {
            maven {
                name = "installLocally"
                setUrl("${rootProject.layout.buildDirectory}/localMaven")
            }
        }
    }
}

private fun Project.publishing(action: org.gradle.api.publish.PublishingExtension.() -> Unit) = extensions.configure<org.gradle.api.publish.PublishingExtension>(action)

private fun Project.mavenPublishing(action: MavenPublishBaseExtension.() -> Unit) {
    extensions.configure(MavenPublishBaseExtension::class.java, action)
}

private fun Project.dokka(action: org.jetbrains.dokka.gradle.DokkaExtension.() -> Unit) =
    extensions.configure<org.jetbrains.dokka.gradle.DokkaExtension>(action)