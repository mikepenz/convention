import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.VersionCatalog

plugins {
    `version-catalog`
    id("com.vanniktech.maven.publish")
}

catalog {
    versionCatalog { from(files("libs.versions.toml")) }
}

mavenPublishing {
    configure(VersionCatalog())

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)
    signAllPublications()
}