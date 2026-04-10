import com.vanniktech.maven.publish.DeploymentValidation
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
    publishToMavenCentral(hasProperty("automaticRelease"), validateDeployment = if (hasProperty("validateDeployment")) DeploymentValidation.VALIDATED else DeploymentValidation.NONE)
    signAllPublications()
}