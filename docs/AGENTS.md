# Documentation project instructions

## About this project

- This is the documentation site for **Ember**, a Minecraft server plugin for Paper and Folia.
- The site is built on [Mintlify](https://mintlify.com). Pages are MDX with YAML frontmatter; navigation lives in
  `docs.json`.
- Plugin source lives in the parent repo (`../`). Always treat `paper-plugin.yml`, the `Ember.kt` / `Version.kt` API
  files, and `gradle.properties` as the source of truth for commands, permissions, the public API surface, and versions.
- Run `mint dev` to preview locally; `mint broken-links` to check internal links.

## Audience pillars

The docs are split into three audiences, each a top-level tab in `docs.json`:

- **Players** — people playing on a server running Ember. Cover commands they can run and gameplay guides.
- **Server owners** — people running a server with Ember installed. Cover installation, configuration, and permissions.
- **Developers** — people building plugins that integrate with Ember. Cover concepts, getting started, and the public
  API.

When adding a page, pick the audience first; that determines the tab and the tone.

## Terminology

- **"server owner"**, not "admin" — for the human running the server.
- **"operator"** specifically when referring to the in-game `op` permission level.
- **"Paper/Folia"** — Ember supports both; mention both unless the topic is specific to one.
- **"plugin developer"** for the Developers audience — not "user" or "API consumer".

## Style preferences

- Active voice, second person ("you").
- One idea per sentence.
- Sentence case for headings.
- Backticks for commands, permission nodes, file names, and code references.
- Reflect the plugin's current state honestly — if a section is bare-bones because the plugin is still alpha, say so
  rather than padding it.

## Content boundaries

- Don't replicate JavaDocs in MDX — they're published separately from a different source.
- Don't document features that don't exist yet. Stub pages should say "coming soon" rather than fabricate behaviour.
- Don't document internal/plugin-only types in the developers API reference — only the `sh.carr.ember` package (the
  `api/` Gradle module) is public.
