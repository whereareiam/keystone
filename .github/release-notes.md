---
version: 2.0.0
---

# Keystone 2.0.0 - Delivery

Keystone 2.0.0 is rebuilt on Semantica: it sends a plugin's translated messages and the texts admins write in configs to each recipient in their own language, the one they chose in Intercept or their client's, as chat, action bar, title, boss bar or sound, with PlaceholderAPI, PAPIProxyBridge and MiniPlaceholders filled in. It runs on Paper, Bukkit, Velocity, BungeeCord and NeoForge 1.21.1 servers and needs Java 21.

## Upgrading

* Keystone 2.0.0 replaces the API of 1.x; nothing of it remains. `Actor`, `Serializers`, the serializer engine, its adapters, decorators, templates, the prefix and scopes are gone.
* Depend on the platform's artifact, such as `keystone-platform-bukkit-paper`, and build a messenger with its entry class: `PaperKeystone.messenger(plugin).semantica(semantica).build()`. A plugin with its own player type passes a function that returns a `Recipient` for it.
* Declare texts with Semantica 0.2's `@Messages` and send them with `messenger.send(sender, messages.text(...))`, or to many with `messenger.send(players, ...)`; the plugin picks the recipients. A config's `Display` is sent with `messenger.send(sender, display.with("player", name))`.
* Display files are added by owner, `messenger.displays().add("myplugin", path)`, so modules add and remove their own.
* `{prefix}` and other shared parts are references to translated messages, such as `{@messages.prefix}`.
* Placeholders are filled in what translators and admins wrote only, never in values such as a player's chat message.
* Legacy codes are read only in player text, through `PaperKeystone.markup(policy).legacyCodes(...)`.
* Java 21 is required. Bukkit plugins that run on older servers downgrade their shaded jar.
