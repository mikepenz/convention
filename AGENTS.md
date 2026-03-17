# Agent Guidelines

## Commit Messages

All commits **must** follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).

### Format

```
<type>(<scope>): <short description>

[optional body]

[optional footer(s)]
```

### Types

| Type       | When to use                                             |
|------------|---------------------------------------------------------|
| `feat`     | A new feature                                           |
| `fix`      | A bug fix                                               |
| `chore`    | Maintenance, dependency updates, tooling changes        |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `docs`     | Documentation changes only                              |
| `test`     | Adding or updating tests                                |
| `ci`       | CI/CD configuration changes                             |
| `perf`     | Performance improvements                                |
| `build`    | Changes to the build system or external dependencies    |

### Dependency Updates

When updating dependencies, **every updated dependency must be listed explicitly** in the commit
body, including the old and new version:

```
chore(deps): update dependencies

- com.example:library 1.2.3 -> 1.4.0
- org.jetbrains.kotlin:kotlin-gradle-plugin 1.9.0 -> 2.0.0
- androidx.compose:compose-bom 2024.01.00 -> 2024.06.00
```

Do **not** use vague messages like `chore: bump dependencies` without listing the specific changes.

## Version Bumps

When bumping versions, update the following files:

1. `convention/gradle.properties` — `VERSION_NAME` and `VERSION_CODE`
2. `version-catalog/gradle.properties` — `VERSION_NAME` and `VERSION_CODE`
3. `version-catalog/libs.versions.toml` — `conventionPlugin` version entry

For a **minor** bump (e.g. `0.9.5 → 0.10.0`), reset the patch to `0` and increment the minor segment. `VERSION_CODE` should reflect the new version numerically (e.g. `0.10.0` → `100`).

Commit message format:
```
chore: bump minor versions

- [release][version] <new-catalog-version>
- [release][convention] <new-convention-version>
```

### Local Publication

After a version bump, publish both artifacts to Maven Local to verify the build:

```bash
./gradlew version-catalog:publishToMavenLocal && ./gradlew convention:publishToMavenLocal
```

### Release Publication

To publish a release to Maven Central:

```bash
./gradlew version-catalog:publishAllPublicationsToMavenCentralRepository -PautomaticRelease=true && ./gradlew convention:publishAllPublicationsToMavenCentralRepository -PautomaticRelease=true
```
