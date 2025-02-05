import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
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

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, hasProperty("automaticRelease"))
    signAllPublications()
}