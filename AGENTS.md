# AGENTS.md

Keystone delivers the texts of a Minecraft plugin; Semantica resolves and renders them. A plugin sends a
Semantica `Message`, a `Content` from a config or a `Display` to a `Recipient`; Keystone resolves it in the
recipient's languages, fills the recipient's placeholders into the literal parts, renders it with the plugin's
`TagPolicy` and shows each part (chat, action bar, title, boss bar, sound). Keystone keeps no settings file; plugins
configure it through a platform's builder.

## Modules

```text
keystone-api/                 contracts and models                         :keystone-api
keystone-common/              delivery, display.yml, languages, gate,      :keystone-common
                              markup and legacy codes, console, Configura
keystone-platform/
  common/                     KeystoneBuilder, the Adventure 4 and 5       :platform-common
                              formats of Semantica
  type/
    platform-bukkit/
      common/                 shared by Bukkit and Paper                   :platform-bukkit-common
      bukkit/                 adventure-platform-bukkit, Adventure 4        :platform-bukkit-bukkit
      paper/                  Paper's own Adventure, Adventure 5 format     :platform-bukkit-paper
    platform-proxy/
      velocity/               Velocity's own Adventure, Adventure 5 format  :platform-proxy-velocity
      bungeecord/             adventure-platform-bungeecord, Adventure 4    :platform-proxy-bungeecord
keystone-integration/
  integration-placeholderapi/   text placeholders, Bukkit and Paper        :integration-placeholderapi
  integration-papiproxybridge/  text placeholders, proxies                 :integration-papiproxybridge
  integration-miniplaceholders/ tag placeholders, Paper and Velocity       :integration-miniplaceholders
  integration-intercept/        language resolver, Paper and Velocity      :integration-intercept
```

- `keystone-api` is what plugins compile against: `Keystone`, `recipient/` (`Recipient`, `RecipientGroup`,
  `Recipients`, `Console`), `model/` (`Display`, `Values`, `Banner`), `type/` (`Gate`, `LegacyCode`, and the sealed
  `Part`s in `type/part/`), `Placeholders`, `LanguageResolver`, `Markup` and `exception/`. Public API: every change is
  compatibility-sensitive and needs complete Javadocs.
- `keystone-common` holds the logic; a platform only adapts it through `platform/Platform` and `platform/Sender`, and
  picks Semantica's Adventure format through `render/AdventureFormat`. Its test fixtures (`testFixtures`) give every
  module's tests a platform, senders that record what they are shown, and a Semantica with English and German texts.
- Keystone's own classes use only Adventure API that versions 4 and 5 share, so one jar serves both; the platform
  module brings `format-adventure-v4` or `-v5`.
- An integration is an `Integration`: the plugin it needs by name and what it installs. Its class names none of the
  other plugin's types; the class it installs does. The builder installs it only when the platform reports the
  plugin. No reflection: presence comes from the platform's plugin manager.
- A new platform joins the group it shares code with; a group gets its `common/` once two members share something.
- Artifacts are published as `me.whereareiam:keystone-<project>`, such as `keystone-api` or
  `keystone-platform-bukkit-paper`.

## Build and test

- `./gradlew test build` builds everything and runs the tests; Java 21, whichever Java runs Gradle.
- `VERSION=<version> ./gradlew publishToMavenLocal` lets a plugin build against a local version.
- Semantica comes from the registry, as a release or a development build (`dev-<commit>`); `gradle/libs.versions.toml`
  names it.
- Intercept's API (`me.whereareiam.intercept:api`) comes from the registry, as a release or a development build;
  `gradle/libs.versions.toml` names it.
- Paper's tests run on the Adventure of the Paper API they compile against (`adventure-paper`), since its types are
  built on it.

## Pull requests and releases

Build each change in its own `feature/...` branch and merge it into `dev` through a pull request.
Every pull request is checked for its title and label and runs the tests. Publishing a release draft
tags `dev` (tags are the bare version, such as `2.0.0`), publishes the Maven artifacts and attaches
the jars. A manual run of `Development build` publishes the selected branch as
`<branch>-<short commit>`.

### Release notes

A merged pull request lands in the release draft as `* <title> ([#<number>](<url>))`, under the
category of its label. Readers are plugin developers.

- Titles use `Area: Description`, with a past-tense description of what changed for a plugin:
  `Introduced`, `Changed`, `Fixed`, `Removed`.
- Areas: `Recipients` (sending, groups, the console), `Displays` (parts and `display.yml`), `Languages`,
  `Placeholders`, `Markup` (player text and legacy codes), `Configuration` (the Configura module), `Platforms`.
  Add an area only for something plugins recognise by that name, such as a new platform or integration, and add it
  to this list in the same pull request.
- Never use module or class names such as `Common` or `API` as an area.

| Label | Use for |
| --- | --- |
| `feature` | something new for plugins |
| `change` | changed behavior or API |
| `bug` | a fix for a bug that shipped in a release |
| `dependencies` | a library update |
| `skip-changelog` | refactors, build, CI, tests and documentation, and fixes of bugs that never shipped |

Add `major` next to the category when plugins must change their code on upgrade.

Text written by hand, the subtitle, the summary and upgrade notes, lives in `.github/release-notes.md`;
it replaces the draft's placeholders while its `version` matches the drafted version. The release
workflow stops while a placeholder is left.
