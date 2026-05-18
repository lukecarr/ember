# Contributing to Ember

## Modules

- `api` — public API, published as `sh.carr.ember:ember-api`.
- `plugin` — Paper plugin implementation (Mojang-mapped via paperweight-userdev).

## Build

```sh
./gradlew build
```

The plugin jar lands at `plugin/build/libs/ember-<version>.jar`.

## Run a local Paper 26.1.2 server

```sh
./gradlew :plugin:runServer
```

## Publish the API locally

```sh
./gradlew :api:publishToMavenLocal
```
