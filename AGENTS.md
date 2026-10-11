# AGENTS.md

Keystone is a messaging library for Minecraft plugins, built on Adventure. It
turns message strings into components through one serializer engine: an `Actor` receives a message,
decorators and placeholders fill it in, and an adapter reads its format (MiniMessage, legacy, Gson or
plain text). Templates render the same pipeline to strings.

## Modules

- `keystone-api`: what plugins compile against. `Actor`, the serializer engine, adapter and decorator
  contracts, `SerializerContent` and `SerializerOptions` (placeholder formats), and the template
  engine with `MessageTemplate`, `StreamMessageTemplate` and `TemplateSection`. Public API: every
  change is compatibility-sensitive and needs complete Javadocs.
- `keystone-common`: `DefaultSerializerEngine` and the format adapters (MiniMessage with legacy
  codes, legacy `&` and `§`, Gson, plain text). Adventure's serializers stay `compileOnly`; the
  plugin or platform provides them.
- `keystone`: the entry point plugins depend on (`Serializers`), which creates engines and registers
  the built-in adapters and decorators (`PrefixDecorator`, `PlayerNameDecorator`).
- `build-logic`: the `keystone.java-common` convention every module applies (Java 17, Lombok,
  publishing, the version from `VERSION`).

## Build and test

- `./gradlew test build` builds everything and runs the tests.
- `VERSION=<version> ./gradlew publishToMavenLocal` lets a plugin build against a local version.

Artifacts are published as `me.whereareiam:keystone`, `keystone-api` and `keystone-common`.

## Pull requests and releases

Build each change in its own `feature/...` branch and merge it into `dev` through a pull request.
Every pull request is checked for its title and label and runs the tests. Publishing a release draft
tags `dev` (tags are the bare version, such as `0.0.2`), publishes the Maven artifacts and attaches
the jars. A manual run of `Development build` publishes the selected branch as
`<branch>-<short commit>`.

### Release notes

A merged pull request lands in the release draft as `* <title> ([#<number>](<url>))`, under the
category of its label. Readers are plugin developers.

- Titles use `Area: Description`, with a past-tense description of what changed for a plugin:
  `Introduced`, `Changed`, `Fixed`, `Removed`.
- Areas: `Actors`, `Serialization` (the engine and its options), `Formats` (MiniMessage, legacy,
  Gson and plain text), `Placeholders`, `Decorators`, `Templates`. Add an area only for something
  plugins recognise by that name, such as a new format or kind of template, and add it to this list
  in the same pull request.
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
