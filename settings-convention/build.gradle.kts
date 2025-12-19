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
}

gradlePlugin {
    plugins {
        register("settings") {
            id = "com.mikepenz.convention.settings"
            implementationClass = "com.mikepenz.gradle.SettingsConventionPlugin"
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