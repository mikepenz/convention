plugins {
    id("nl.littlerobots.version-catalog-update")
}

versionCatalogUpdate {
    sortByKey = false
    catalogFile = file("version-catalog/libs.versions.toml")

    keep {
        keepUnusedVersions = true
    }
}
