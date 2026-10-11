---
version: 1.0.0
---

# Keystone 1.0.0 - Stable messages

Keystone 1.0.0 is the first stable release: actors, the serializer engine, decorators and placeholders keep their API from here on. It adds string templates, configurable placeholder formats and scoped decorators; plugins written against 0.0.1 need the upgrade steps below.

## Highlights

* Templates: `TemplateEngine` renders a message through decorators and placeholders to a string without turning it into a component; `MessageTemplate` and `StreamMessageTemplate` render one template or one line per stream item, with placeholders and `TemplateSection`s.
* Placeholders: the placeholder format is configurable through `SerializerOptions.placeholderFormat`, with `{name}`, `%name%`, `$name` or a custom format.
* Decorators: decorators can be registered for a scope with `Serializers.registerScopedDecorator`; a message may name its scope.
* MiniMessage: with legacy input enabled, legacy `&` and `§` codes are applied inside MiniMessage text instead of the whole message being parsed as legacy text.

## Upgrading

* `Player` was removed; implement `Actor` instead. The `playerName` placeholder is filled for every receiver, not only a `Player`.
* Placeholder keys are names without their format: `addPlaceholder("coins", ...)` instead of `"{coins}"`.
* `PlayerNameDecorator` and `PrefixDecorator` moved to `me.whereareiam.keystone.decorator` in `keystone`.
