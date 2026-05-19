# Documentation project instructions

## About this project

- This is the documentation site for **Ember**, a Minecraft server plugin for Paper and Folia.
- The site is built on [Mintlify](https://mintlify.com). Pages are MDX with YAML frontmatter; navigation lives in `docs.json`.
- Plugin source lives in the parent repo (`../`). Always treat `paper-plugin.yml`, the `Ember.kt` and `Version.kt` API files, and `gradle.properties` as the source of truth for commands, permissions, the public API surface, and versions.
- Run `mint dev` to preview locally. Run `mint broken-links` to check internal links.

## Audience pillars

The docs are split into four top-level tabs in `docs.json`:

- **Players**: people playing on a server running Ember. Cover guides for gameplay features.
- **Server owners**: people running a server with Ember installed. Cover installation, configuration, and permissions.
- **Developers**: people building plugins that integrate with Ember. Cover concepts, getting started, and the public API.
- **Commands**: a flat reference of every `/ember` subcommand, one page per command. Linked into from the other tabs.

When adding a page, pick the audience first. That determines the tab and the tone.

## Terminology

- **"server owner"**, not "admin", for the human running the server.
- **"operator"** specifically when referring to the in-game `op` permission level.
- **"Paper/Folia"**: Ember supports both. Mention both unless the topic is specific to one.
- **"plugin developer"** for the Developers audience, not "user" or "API consumer".

## Style preferences

- Active voice, second person ("you").
- One idea per sentence.
- Sentence case for headings.
- Backticks for commands, permission nodes, file names, and code references.
- Reflect the plugin's current state honestly. If a section is bare-bones because the plugin is still alpha, say so rather than padding it.

## "Since" annotations

Commands, features, and public API types include a "Since" line that names the Ember release they were introduced in. Readers can tell at a glance whether the build of Ember they have supports something.

**Per-page convention.** Directly under the frontmatter, before the first paragraph of body content, add a line of the form:

    **Since** `v0.1.0-alpha.1`

Use this on per-command pages and per-type API reference pages.

**Per-row convention.** Tables that list multiple items (for example, the permissions reference) include a `Since` column. Each row's value is the version that item was introduced in.

Use the exact release tag from the [changelog](changelog.mdx). When a member changes meaningfully in a later release, add a follow-up note rather than overwriting the original `Since`.

## Patterns to avoid

These read as AI-generated and undermine credibility. Cut them, even when it costs an extra sentence.

- **Em and en dashes** (`—`, `–`). Use a period, comma, parentheses, or colon instead. Hyphens in compound words are fine.
- **Rhetorical triplets** ("fast, reliable, and scalable"). Real lists of three items are fine; rhetorical filler is not. If you could remove one item and the sentence would carry the same information, it's filler.
- **"Not x, it's y" framing**. State what something is, directly.
- **Salesy or sensational language**: powerful, robust, seamless, elegant, leverage, supercharge, unlock, "in today's fast-paced world", "let's dive into", "it's worth noting that". Replace with a concrete claim, or delete the sentence.

## Content boundaries

- Don't replicate JavaDocs in MDX. They're published separately from a different source.
- Don't document features that don't exist yet. Stub pages should say "coming soon" rather than fabricate behaviour.
- Don't document internal/plugin-only types in the developers API reference. Only the `sh.carr.ember` package (the `api/` Gradle module) is public.
