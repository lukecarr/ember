# Ember docs

The documentation site for [Ember](https://github.com/lukecarr/ember) — a Minecraft plugin for Paper and Folia.

Built with [Mintlify](https://mintlify.com).

## Local development

Install the Mintlify CLI:

```bash
npm i -g mint
```

From the `docs/` directory, run:

```bash
mint dev
```

The site will be served at `http://localhost:3000`.

To check for broken internal links:

```bash
mint broken-links
```

## Structure

- `docs.json` — site config and navigation.
- `index.mdx` — landing page.
- `players/` — pages for people playing on a server running Ember.
- `server-owners/` — pages for people running a server with Ember installed.
- `developers/` — pages for plugin developers integrating with the Ember API.
- `logo/`, `favicon.svg` — branding assets.
- `AGENTS.md` — guidance for AI agents editing the docs.

## License

MIT — see [LICENSE](LICENSE).
