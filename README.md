# Ember

[![CI](https://img.shields.io/github/actions/workflow/status/lukecarr/ember/ci.yml?label=ci)](https://github.com/lukecarr/ember/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/actions/workflow/status/lukecarr/ember/release.yml?label=release)](https://github.com/lukecarr/ember/actions/workflows/release.yml)
[![Latest release](https://img.shields.io/github/v/release/lukecarr/ember?color=brightgreen)](https://github.com/lukecarr/ember/releases/latest)
[![Test coverage](https://img.shields.io/codecov/c/gh/lukecarr/ember)](https://codecov.io/gh/lukecarr/ember)
[![Open issues](https://img.shields.io/github/issues/lukecarr/ember)](https://github.com/lukecarr/ember/issues)

Ember is a (WIP) plugin for [Paper](https://papermc.io/software/paper/) and [Folia](https://papermc.io/software/folia/) servers for Minecraft 26.1.x, that transforms your server into a unique experience by implementing an abundance of custom mechanics.

## For server owners

**Requirements:** Paper or Folia, Minecraft 26.1.2 or later, JDK 25 or later. The same jar runs on both Paper and Folia with no extra configuration.

To install:

1. Download the latest `ember-<version>.jar` from [GitHub Releases](https://github.com/lukecarr/ember/releases/latest). Each release includes a `.sha256` checksum.
2. Drop the jar into your server's `plugins/` directory.
3. Restart the server.
4. Run `/ember version` to confirm Ember loaded.

See [ember.carr.sh/server-owners](https://ember.carr.sh/server-owners) for the full installation guide, flags reference, and permission nodes.

## For developers

Ember publishes `ember-api` to [GitHub Packages](https://github.com/lukecarr/ember/packages). You'll need a GitHub personal access token with the `read:packages` scope to pull it.

Add the repository to your `build.gradle.kts`:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/lukecarr/ember")
        credentials {
            username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
            password = providers.gradleProperty("gpr.token").orNull ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Add the dependency as `compileOnly` — the API ships inside the Ember plugin jar, so you don't want a second copy bundled in your own:

```kotlin
dependencies {
    compileOnly("sh.carr.ember:ember-api:0.1.0-alpha.1")
}
```

Access the service at runtime via `Ember.instance` (Kotlin) or `Ember.getInstance()` (Java).

Declare Ember as a hard dependency in your `paper-plugin.yml` so it loads first:

```yaml
dependencies:
  server:
    Ember:
      load: BEFORE
      required: true
```

See [ember.carr.sh/developers](https://ember.carr.sh/developers) for the quickstart, API concepts, and reference.

> [!NOTE]
> The API is pre-1.0, so minor-version bumps may include breaking changes.

## For players

See [ember.carr.sh/players](https://ember.carr.sh/players) for guides on what Ember adds to the server you're playing on.

## License

Ember is licensed under the [MIT License](LICENSE).
