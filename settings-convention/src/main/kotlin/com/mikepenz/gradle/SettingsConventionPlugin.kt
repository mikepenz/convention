package com.mikepenz.gradle

import org.gradle.api.Plugin
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.util.GradleVersion

class SettingsConventionPlugin : Plugin<Settings> {
    override fun apply(target: Settings) = with(target) {
        val minGradleVersion = GradleVersion.version("8.10")
        require(GradleVersion.current() >= minGradleVersion) {
            "Please use at least ${minGradleVersion.version} or later. Found ${GradleVersion.current().version}"
        }

        val properties = settings.extensions.extraProperties
        val tokenErrorMsg =
            "Please supply the artifactory token in '~/.gradle/gradle.properties', or '-P\"settings.artifactory.token\"=\"<>\"', or as system ENV 'JF_ACCESS_TOKEN'."
        val token = System.getenv("JF_ACCESS_TOKEN") ?: properties.getOrThrow("settings.artifactory.token") { IllegalArgumentException(tokenErrorMsg) }

        val filters = properties.getOrNull("settings.artifactory.filters")?.split(",")?.map { it.trim() }

        val baseUrl = properties.getOrNull("settings.artifactory.baseUrl") ?: properties.getOrNull("settings.artifactory.fallbackBaseUrlPropKey")?.let { fallbackBaseUrlProp ->
            properties.getOrNull(fallbackBaseUrlProp)
        } ?: throw IllegalArgumentException("This plugin requires at the baseUrl to be set as 'settings.artifactory.baseUrl'.")

        val repositoriesPropKey = "settings.artifactory.repositories"
        val repositories = properties.getOrThrow(repositoriesPropKey) {
            IllegalArgumentException("This plugin requires at least one repository to be set as 'settings.artifactory.repositories'. (this is a comma seperated list)")
        }.split(",").map { it.trim() }


        val configure: MavenArtifactRepository.(providedUrl: String) -> Unit = { providedUrl ->
            setUrl(providedUrl)
            credentials(HttpHeaderCredentials::class.java) {
                it.name = "Authorization"
                it.value = "Bearer $token"
            }
            authentication { it.create("header", HttpHeaderAuthentication::class.java) }
            content {
                if (!filters.isNullOrEmpty()) {
                    @Suppress("UnstableApiUsage")
                    filters.forEach { filter ->
                        it.includeGroupAndSubgroups(filter)
                    }
                }
            }
        }

        pluginManagement { plugin ->
            plugin.repositories { repoHandler ->
                repositories.onEach { repo ->
                    repoHandler.maven { maven ->
                        configure(maven, "${baseUrl}/${repo}")
                    }
                }

                // common
                repoHandler.mavenCentral()
                repoHandler.google()
                repoHandler.gradlePluginPortal()

                // maven local
                repoHandler.mavenLocal()
            }
        }

        val versionCatalog = properties.getOrElse("settings.artifactory.versionCatalog.coordinate") {
            logger.info("No version catalog coordinate found (For property: 'settings.artifactory.versionCatalog.coordinate'). Skipping version catalog configuration.")
            ""
        }

        dependencyResolutionManagement {
            // if project defines. use these
            @Suppress("UnstableApiUsage")
            it.repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

            @Suppress("UnstableApiUsage")
            it.repositories { repoHandler ->
                repositories.onEach { repo ->
                    repoHandler.maven { maven ->
                        configure(maven, "${baseUrl}/${repo}")
                    }
                }

                // common
                repoHandler.mavenCentral()
                repoHandler.google()
                repoHandler.gradlePluginPortal()

                // maven local
                repoHandler.mavenLocal()
            }

            it.versionCatalogs { vc ->
                if (versionCatalog.isNotBlank() && versionCatalog != "false") {
                    val versionCatalogVersion = properties.getOrNull("settings.artifactory.versionCatalog.propKeys")
                        ?.let { keys -> keys.split(",").map { it.trim() } } ?: listOf("settings.artifactory.versionCatalogVersion")

                    val version = versionCatalogVersion.firstNotNullOfOrNull { key -> properties.getOrNull(key)?.takeIf { it.isNotBlank() } }
                        ?: throw IllegalArgumentException("This plugin requires a version catalog version to be set as 'settings.artifactory.versionCatalogVersion'. Or via via one of the keys as defined in 'settings.artifactory.versionCatalog.propKeys'.")

                    vc.create("baseLibs") { vb ->
                        vb.from("${versionCatalog}:${version}")
                    }
                }
            }
        }

        enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
        enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
    }

    private fun ExtraPropertiesExtension.getOrElse(key: String, default: () -> String): String {
        return if (has(key)) get(key)!!.toString() else default()
    }

    private fun ExtraPropertiesExtension.getOrNull(key: String): String? {
        return if (has(key)) get(key)!!.toString() else null
    }

    private fun ExtraPropertiesExtension.getOrThrow(key: String, default: () -> Throwable): String {
        return if (has(key)) get(key)!!.toString() else throw default()
    }

    companion object {
        private val logger = Logging.getLogger(SettingsConventionPlugin::class.java)
    }
}
