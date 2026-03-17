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
