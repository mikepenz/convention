package com.mikepenz.gradle.utils

import org.gradle.api.Project
import java.util.*

internal fun Project.readLocalProperties(): Properties? {
    return Properties().apply {
        val file = project.rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { load(it) }
        } else {
            return null
        }
    }
}

/**
 * reads from local.properties, otherwise falls back to project based properties
 */
fun Project.readPropertyOrElse(
    key: String,
    default: String? = null,
    localProperties: Properties? = readLocalProperties(),
): String? {
    val property = localProperties?.getProperty(key, null)
    return property ?: if (project.hasProperty(key)) project.property(key)?.toString() else default
}