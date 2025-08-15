package com.mikepenz.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasUnitTestBuilder
import com.android.build.gradle.BaseExtension
import com.mikepenz.gradle.utils.libs
import com.mikepenz.gradle.utils.readPropertyOrElse
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.util.Properties

fun Project.configureBaseAndroid(localProperties: Properties?) {
    android {
        compileSdkVersion(libs.findVersion("compileSdk").get().requiredVersion.toInt())

        defaultConfig {
            it.minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
            it.targetSdk = libs.findVersion("targetSdk").get().requiredVersion.toInt()
        }

        val compatPatrouille = project.readPropertyOrElse("com.mikepenz.compatPatrouille.enabled", "true", localProperties).toString().toBoolean()
        if (!compatPatrouille) {
            val javaVersion = readPropertyOrElse("com.mikepenz.java.version", "17", localProperties)!!.toInt()
            compileOptions {
                it.sourceCompatibility = JavaVersion.forClassVersion(javaVersion + 44)
                it.targetCompatibility = JavaVersion.forClassVersion(javaVersion + 44)
            }
        }

        buildTypes {
            it.getByName("release") { buildType ->
                buildType.isMinifyEnabled = false
                buildType.proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                )
            }
        }
    }

    androidComponents {
        beforeVariants(selector().withBuildType("release")) { variantBuilder ->
            (variantBuilder as? HasUnitTestBuilder)?.apply {
                enableUnitTest = false
            }
        }
    }
}

internal fun Project.android(action: BaseExtension.() -> Unit) = extensions.configure(BaseExtension::class.java, action)

private fun Project.androidComponents(action: AndroidComponentsExtension<*, *, *>.() -> Unit) {
    extensions.configure(AndroidComponentsExtension::class.java, action)
}
