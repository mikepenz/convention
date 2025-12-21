package com.mikepenz.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

internal fun Project.androidComponents(action: AndroidComponentsExtension<*, *, *>.() -> Unit) {
    extensions.configure(AndroidComponentsExtension::class.java, action)
}
