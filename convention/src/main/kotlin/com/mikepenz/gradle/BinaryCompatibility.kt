package com.mikepenz.gradle

import com.mikepenz.gradle.utils.readLocalProperties
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.Project


fun Project.applyBinaryCompatibilityValidator(flagKey: String) {
    extensions.configure(kotlinx.validation.ApiValidationExtension::class.java) { apiValidationExt ->
        @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
        apiValidationExt.klib {
            it.enabled = true
        }

        apiValidationExt.ignoredProjects.addAll(allprojects.filter {
            val projectLocalProperties = it.readLocalProperties()
            val projBinaryCompatibilityValidatorEnabled = it.readPropertyOrElse(flagKey, "true", projectLocalProperties).toBoolean()
            if (projBinaryCompatibilityValidatorEnabled) it.name.contains("app") else true
        }.map { it.name })
    }
}