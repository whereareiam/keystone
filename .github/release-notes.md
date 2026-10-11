---
version: 0.0.2
---

# Keystone 0.0.2 - [Short subtitle]

[1-2 sentence summary of the release.]

## Highlights

* Templates: `TemplateEngine` renders a message through decorators and placeholders to a string without turning it into a component; `MessageTemplate` and `StreamMessageTemplate` render one template or one line per stream item, with placeholders and `TemplateSection`s.
* Placeholders: the placeholder format is configurable through `SerializerOptions.placeholderFormat`, with `{name}`, `%name%`, `$name` or a custom format.
* Decorators: decorators can be registered for a scope with `Serializers.registerScopedDecorator`; a message may name its scope.
* MiniMessage: with legacy input enabled, legacy `&` and `§` codes are applied inside MiniMessage text instead of the whole message being parsed as legacy text.

## Upgrading

* `Player` was removed; implement `Actor` instead. The `playerName` placeholder is filled for every receiver, not only a `Player`.
* Placeholder keys are names without their format: `addPlaceholder("coins", ...)` instead of `"{coins}"`.
* `PlayerNameDecorator` and `PrefixDecorator` moved to `me.whereareiam.keystone.decorator` in `keystone`.
