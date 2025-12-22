import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0

plugins {
    id("org.jetbrains.kotlin.jvm").version(libs.versions.kotlin.get())
    id("java-gradle-plugin")
    id("com.vanniktech.maven.publish")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        /**
         * Yay! Latest tools with compat flags 🎉
         * See also https://docs.gradle.org/current/userguide/compatibility.html
         */
        languageVersion.set(KOTLIN_2_0)
        apiVersion.set(KOTLIN_2_0)
        coreLibrariesVersion = "2.0.0"
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.mavenPublish.gradlePlugin)
    compileOnly(libs.dokka.gradlePlugin)
    compileOnly(libs.binaryCompatiblity.gradlePlugin)
    compileOnly(libs.versionCatalogUpdate.gradlePlugin)
    compileOnly(libs.tapmoc.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("root") {
            id = "com.mikepenz.convention.root"
            implementationClass = "com.mikepenz.gradle.RootConventionPlugin"
        }

        register("kotlin") {
            id = "com.mikepenz.convention.kotlin"
            implementationClass = "com.mikepenz.gradle.KotlinConventionPlugin"
        }

        register("kotlinMultiplatform") {
            id = "com.mikepenz.convention.kotlin-multiplatform"
            implementationClass = "com.mikepenz.gradle.KotlinMultiplatformConventionPlugin"
        }

        register("androidApplication") {
            id = "com.mikepenz.convention.android-application"
            implementationClass = "com.mikepenz.gradle.AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "com.mikepenz.convention.android-library"
            implementationClass = "com.mikepenz.gradle.AndroidLibraryConventionPlugin"
        }

        register("androidKmpLibrary") {
            id = "com.mikepenz.convention.android-kmp-library"
            implementationClass = "com.mikepenz.gradle.AndroidKmpLibraryConventionPlugin"
        }

        register("androidTest") {
            id = "com.mikepenz.convention.android-test"
            implementationClass = "com.mikepenz.gradle.AndroidTestConventionPlugin"
        }

        register("compose") {
            id = "com.mikepenz.convention.compose"
            implementationClass = "com.mikepenz.gradle.ComposeConventionPlugin"
        }

        register("publishing") {
            id = "com.mikepenz.convention.publishing"
            implementationClass = "com.mikepenz.gradle.PublishingConventionPlugin"
        }

        create("composablePreviewPaparazziPlugin") {
            id = "com.mikepenz.convention.composable-preview-scanner.paparazzi-plugin"
            implementationClass = "com.mikepenz.gradle.previewscanner.ComposablePreviewPaparazziPlugin"
            displayName = "Composable Preview Paparazzi Generator"
            description = "A Gradle plugin that generates and executes Paparazzi test files for screenshot testing Composable Previews. https://github.com/sergio-sastre/ComposablePreviewScanner/"
        }
    }
}

mavenPublishing {
    configure(
        GradlePlugin(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true,
        )
    )

    publishToMavenCentral(hasProperty("automaticRelease"), validateDeployment = hasProperty("validateDeployment"))
    signAllPublications()
}