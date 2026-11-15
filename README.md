# Keystone

Keystone delivers the texts of a Minecraft plugin. A plugin declares its texts with
[Semantica](https://github.com/whereareiam/semantica); Keystone sends each one to a player, the console or a
group, in that recipient's language, with the placeholders of that recipient, as the chat line, action bar, title,
boss bar or sound the server's admins chose.

It runs on Paper, Bukkit and Spigot, Velocity and BungeeCord, with Java 21.

## Installation

```kotlin
repositories {
    maven("https://registry.whereareiam.me/maven/packages")
}

dependencies {
    // one platform:
    implementation("me.whereareiam:keystone-platform-bukkit-paper:<version>")
    // implementation("me.whereareiam:keystone-platform-bukkit-bukkit:<version>")
    // implementation("me.whereareiam:keystone-platform-proxy-velocity:<version>")
    // implementation("me.whereareiam:keystone-platform-proxy-bungeecord:<version>")
}
```

Shade Keystone into the plugin and relocate it. On Bukkit and BungeeCord, shade and relocate Adventure as well:
those platforms reach players through adventure-platform, and a relocated Adventure never meets one another plugin
or the server ships.

## Send a text

Declare the texts with Semantica's `@Messages`, build Semantica, then Keystone:

```java
import me.whereareiam.keystone.Keystone;
import me.whereareiam.keystone.platform.paper.PaperKeystone;
import me.whereareiam.semantica.Message;
import me.whereareiam.semantica.declaration.Default;
import me.whereareiam.semantica.declaration.Messages;
import org.bukkit.command.CommandSender;

@Messages("commands")
public interface CommandMessages {
	@Default("<white>You must wait <gray>{time, number} seconds</gray> before using this command again.")
	Message cooldown(double time);
}

Keystone<CommandSender> keystone = PaperKeystone.builder(plugin)
		.semantica(semantica)
		.display(plugin.getDataFolder().toPath().resolve("display.yml"))
		.build();

CommandMessages messages = new GeneratedCommandMessages();
keystone.recipients().of(sender).send(messages.cooldown(3.5));
```

The player sees the text in their language: the language their client reports, then the fallback, which is
Semantica's default locale unless `languages(l -> l.fallback(...))` sets another. A text missing in one language is
taken from the next. Values, such as a player's name, are never read as markup.

Other recipients:

```java
keystone.recipients().players().send(messages.restart(30));
keystone.recipients().permission("myplugin.staff").send(messages.staffJoined(name));
keystone.recipients().player(uniqueId).ifPresent(player -> player.send(messages.welcome()));
keystone.console().send(messages.reloaded(version, millis));
```

The builders of the other platforms take what that platform needs:

| Platform | Builder | Command sender |
| --- | --- | --- |
| Paper | `PaperKeystone.builder(plugin)` | `CommandSender` |
| Bukkit, Spigot | `BukkitKeystone.builder(plugin, audiences)` with the plugin's `BukkitAudiences` | `CommandSender` |
| Velocity | `VelocityKeystone.builder(server, plugin, logger)` | `CommandSource` |
| BungeeCord | `BungeeKeystone.builder(plugin, audiences)` with the plugin's `BungeeAudiences` | `CommandSender` |

On Bukkit and BungeeCord the plugin creates its audiences when it enables and closes them when it disables.

## Let admins choose how a text is shown

Every message goes to the chat. `display.yml` lists the messages shown as more than that, by key; `true` stands for
the message's own text, and chat stays on unless it says `false`:

```yaml
commands.cooldown:
  chat: false
  actionbar: true

chirper.restart:
  title:
    subtitle: "<gray>in {seconds} seconds"   # the title is the message itself
    times: { in: 10, stay: 60, out: 10 }      # ticks
  bossbar: { color: red, seconds: 10 }
  sound: { name: block.note_block.pling, volume: 1, pitch: 1.2 }
```

A missing file has no entries. `keystone.reload()` reads it again; when it cannot be read, a
`DisplayFileException` names the file and the entry, and the displays read before stay in use.

## Text written in a config

A config field of type `Content` holds one string: the admin's own text, a translated message pulled in with
`{@key}`, or both. A field of type `Display` holds the parts, as in `display.yml`. Register Keystone's Jackson
module with Configura to read and write both:

```java
import me.whereareiam.keystone.common.configura.KeystoneModule;

Configura configura = Configura.builder()
		.format(Format.YAML)
		.module(new KeystoneModule())
		.build();

public class Announcement {
	public Display display = Display.chat(Content.of("{@chirper.vote}"));
}
```

```yaml
display:
  chat: "<gold>{@chirper.vote} <gray>(ends Sunday)"
  bossbar: { text: "<green>/vote", color: green, seconds: 10 }
```

```java
recipient.send(announcement.display, Values.of("player", name));
```

Check each content after loading with `semantica.check(content)` and skip an entry with issues, naming its file and
key, so a mistyped reference doesn't reach players.

## Placeholders

Keystone fills the placeholders of other plugins when they are present: PlaceholderAPI on Paper and Bukkit,
PAPIProxyBridge on Velocity and BungeeCord, and MiniPlaceholders' global placeholders on Paper and Velocity.
Placeholders apply to what translators and admins wrote, never to values: a player who types `%player_name%` into a
chat message sees it as typed.

A plugin adds its own, as text or as MiniMessage tags:

```java
PaperKeystone.builder(plugin)
		.semantica(semantica)
		.placeholders(placeholders -> placeholders
				.add((Placeholders.Text) (recipient, text) -> text.replace("{server}", serverName))
				.disable(MiniPlaceholdersIntegration.class))
		.build();
```

An integration is installed when its plugin is enabled before Keystone is built, so list it under `softdepend`.
PAPIProxyBridge answers over the network; a text waits for it at most 100 ms and is otherwise shown with its
placeholders as written.

## Languages

A recipient's languages are, in order: those of the language resolvers, the client's language, and the fallback.
A plugin that stores a player's choice adds a resolver:

```java
.languages(languages -> languages
		.fallback(Locale.ENGLISH)
		.resolver(recipient -> recipient.uniqueId().flatMap(storedLanguages::get)))
```

## Hold texts until a player is ready

The gate holds texts sent to a player, such as before they log in, and shows them in order when the plugin releases
the player:

```java
keystone.gate(recipient -> loggedIn(recipient) ? Gate.SEND : Gate.HOLD);

keystone.release(recipient);   // after login
keystone.discard(recipient);   // when they leave without logging in
```

The console is never held. At most 256 texts are held per player; past that the oldest is dropped.

## What players write

`Markup` reads a chat message with only the tags a policy allows, and the legacy codes a filter accepts:

```java
Markup markup = keystone.markup(TagPolicy.of(TagPolicy.Group.COLOR, TagPolicy.Group.DECORATION))
		.legacyCodes(code -> !(code instanceof LegacyCode.Reset));

Component text = markup.read(typed);
```

A legacy code counts at the start, after whitespace, after another code or after a tag, so `R&D` stays as typed.
A colour ends the colours and decorations before it, as in the vanilla client.

## The console and the start banner

The console writes each text to the plugin's log with ANSI colours, in the fallback language. A banner prints its
lines between empty lines:

```java
keystone.console().print(Banner.of(
				Content.of("<gold>  █▀ █▀▀   <white>MyPlugin v<gray>{version}"),
				Content.of("<gold>  ▄█ █▄▄   <white>Platform: <gray>{platform}"))
		.values(Values.of("version", version).and("platform", "Paper")));
```

## Building

```sh
./gradlew test build
```

`VERSION=<version> ./gradlew publishToMavenLocal` lets a plugin build against a local version.
