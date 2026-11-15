---
version: 2.0.0
---

# Keystone 2.0.0 - Delivery

Keystone 2.0.0 is rebuilt on Semantica: it sends a plugin's translated messages and the texts admins write in configs to each recipient in their own language, as chat, action bar, title, boss bar or sound, with PlaceholderAPI, PAPIProxyBridge and MiniPlaceholders filled in. It runs on Paper, Bukkit, Velocity and BungeeCord and needs Java 21.

## Upgrading

* Keystone 2.0.0 replaces the API of 1.x; nothing of it remains. `Actor`, `Serializers`, the serializer engine, its adapters, decorators, templates, the prefix and scopes are gone.
* Declare texts with Semantica 0.2's `@Messages` and send them with `keystone.recipients().of(sender).send(messages.text(...))`; a config's text is a `Content` sent with `recipient.send(content, values)`.
* Depend on the platform's artifact, such as `keystone-platform-bukkit-paper`, and build Keystone with its builder: `PaperKeystone.builder(plugin).semantica(semantica).build()`.
* `{prefix}` and other shared parts are references to translated messages, such as `{@messages.prefix}`.
* Placeholders are filled in what translators and admins wrote only, never in values such as a player's chat message.
* Legacy codes are read only in player text, through `keystone.markup(policy).legacyCodes(...)`.
* Java 21 is required. Bukkit plugins that run on older servers downgrade their shaded jar.
