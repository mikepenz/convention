package com.mikepenz.gradle.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

internal val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("baseLibs")

/** `compileSdk` as (major, minor); the catalog value may be "37" or "37.2". */
internal val Project.compileSdkVersion: Pair<Int, Int>
    get() = libs.findVersion("compileSdk").get().requiredVersion.let {
        it.substringBefore('.').toInt() to it.substringAfter('.', "0").toInt()
    }
