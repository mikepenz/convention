# convention

A convention plugin used for various other open source projects.
Aim is to consolidate the main logic and simplify upgrades and maintenance.

## Usage

Define the plugin in the projects `libs.versions.toml` file:

```toml
[versions]
conventionPlugin = "{latest-version}"

[plugins]
conventionPlugin = { id = "com.mikepenz.convention.root", version.ref = "conventionPlugin" }
```

Add the following to the root `build.gradle.kts` file:

```groovy
alias(libs.plugins.conventionPlugin)
```

Apply sub-plugins to the specific module:

```kts
plugins {
    id("com.mikepenz.convention.android-library")
    id("com.mikepenz.convention.kotlin-multiplatform")
    id("com.mikepenz.convention.compose")
    id("com.mikepenz.convention.publishing")
    // ...
}
```

> [!IMPORTANT]  
> The `version-catalog` plugin is required for the opinionated `android-*` plugins to work correctly.

## Plugins

### `com.mikepenz.convention.root`

This plugin is currently no-op.

### `com.mikepenz.convention.kotlin-multiplatform`

Applies the Kotlin Multiplatform plugin onto the respective module, and configures Java and Kotlin.

#### Configuration options

This plugin supports configurations per module

```properties
# applies the multiplatfrom plugin - enabled by default
com.mikepenz.multiplatform.enabled=true
# configures multiplatform targets - enabled by default
com.mikepenz.targets.enabled=true
# configure android target - enabled by default
com.mikepenz.android.enabled=true
# configure jvm target - enabled by default
com.mikepenz.jvm.enabled=true
# configure wasm target - enabled by default
com.mikepenz.wasm.enabled=true
# configure js target - enabled by default
com.mikepenz.js.enabled=true
# configure composeNative target - enabled by default
# note this only enables native targets which support compose
com.mikepenz.composeNative.enabled=true
# configure native target - disabled by default
# this enables native targets which do NOT support compose
com.mikepenz.native.enabled=true
```

## `com.mikepenz.convention.android-application`

Applies the Android application plugin to the respecitve module.
This also has various opinionated configurations:

- Compose will be enabled
- VersionCode/Name are resolved via `VERSION_CODE`/`VERSION_NAME` from the `gradle.properties`
- Source & Target compile compatibility is set to JVM 17
- Unit tests are disabled for release builds

## `com.mikepenz.convention.android-library`

Similar to `android-application`, without app specific configuration.

## `com.mikepenz.convention.android-test`

Similar to `android-application`, without app specific configuration.

## `com.mikepenz.convention.compose`

Configures the compose plugin for the respective module.
This also configures reports and metrics destinations of the compose plugin.

## `com.mikepenz.convention.publishing`

Configures publishing support for the respective module via the `com.vanniktech.maven.publish` plugin.
It also configures and enables `Dokka` for documentation.

# version-catalog

Common Version-Catalog with opinionated versioning and naming for various other open source projects.
Aim is to consolidate the key dependencies and plugins and their versions across projects.

## Usage

In the root `settings.gradle.kts` of the project. Specify the version catalog:

```kts

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        // ...
    }

    versionCatalogs {
        create("baseLibs") {
            from("com.mikepenz:version-catalog:{latest-version}")
        }
    }
}
```

> [!NOTE]  
> The libs provided are required to be called `baseLibs` if you also intend to use the `convention` plugin.
> The `convention` plugin uses the `baseLibs` catalog to resolve the versions of various plugins.

## License

    Copyright 2025 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
