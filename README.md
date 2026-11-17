# Keystone

Keystone delivers the texts of a Minecraft plugin. A plugin declares its texts with
[Semantica](https://github.com/whereareiam/semantica); Keystone sends each one to a player, the console or a
group, in that recipient's language, with the placeholders of that recipient, as the chat line, action bar, title,
boss bar or sound the server's admins chose.

It runs on Paper, Bukkit and Spigot, Velocity and BungeeCord, and NeoForge 1.21.1 servers, with Java 21.

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
    // implementation("me.whereareiam:keystone-platform-neoforge:<version>")
}
```

Shade Keystone into the plugin and relocate it. On Bukkit and BungeeCord, shade and relocate Adventure as well:
those platforms reach players through adventure-platform, and a relocated Adventure never meets one another plugin
or the server ships.

On NeoForge, the mod nests adventure-platform-neoforge (a mod of its own) and Keystone's NeoForge platform, Adventure
and Semantica as jars in jar, unrelocated: adventure-platform-neoforge 6.0.1 nests Adventure 4.17, Semantica's format
needs 4.25 or later, and NeoForge loads the highest version any mod nests, so it runs on the Adventure Keystone brings.
Keystone's NeoForge jar is a game library (`FMLModType: GAMELIBRARY`), loaded where it sees Minecraft. Minecraft 1.21.1
ships an older ICU4J than Semantica's, so the mod nests Semantica's ICU4J too.

## Send a text

Declare the texts with Semantica's `@Messages`, build Semantica, then a messenger:

```java
import me.whereareiam.keystone.Messenger;
import me.whereareiam.keystone.platform.paper.PaperKeystone;
import me.whereareiam.semantica.Message;
import me.whereareiam.semantica.declaration.Default;
import me.whereareiam.semantica.declaration.Messages;
import org.bukkit.command.CommandSender;

@Messages("economy")
public interface EconomyMessages {
	@Default("<white>Your balance is <gold>{amount, number} coins</gold>.")
	Message balance(double amount);
}

Messenger<CommandSender> messenger = PaperKeystone.messenger(plugin)
		.semantica(semantica)
		.build();

EconomyMessages messages = new GeneratedEconomyMessages();
messenger.send(sender, messages.balance(250));
```

The player sees the text in their language: the language their client reports, then the fallback, which is
Semantica's default locale unless `languages(l -> l.fallback(...))` sets another. A text missing in one language is
taken from the next. Values, such as a player's name, are never read as markup.

The plugin chooses who receives a text; the messenger shows it to each of them:

```java
messenger.send(Bukkit.getOnlinePlayers(), messages.restart(30));
messenger.send(Bukkit.getConsoleSender(), messages.reloaded(version, millis));
```

A text sent to many players is rendered once for each distinct result: players who share a language and whose
placeholders fill in alike share one rendering.

Each platform has its own entry class:

| Platform | Messenger | Sender |
| --- | --- | --- |
| Paper | `PaperKeystone.messenger(plugin)` | `CommandSender` |
| Bukkit, Spigot | `BukkitKeystone.messenger(plugin, audiences)` with the plugin's `BukkitAudiences` | `CommandSender` |
| Velocity | `VelocityKeystone.messenger(server, plugin, logger)` | `CommandSource` |
| BungeeCord | `BungeeKeystone.messenger(plugin, audiences)` with the plugin's `BungeeAudiences` | `CommandSender` |
| NeoForge | `NeoForgeKeystone.messenger(server, logger)` | `CommandSourceStack` |

On Bukkit and BungeeCord the plugin creates its audiences when it enables and closes them when it disables.

On NeoForge a messenger belongs to one server: the mod builds it when the server starts and drops it when the server
stops, since a client starts a new server for each world it opens. A player is reached through their command source:

```java
@SubscribeEvent
void starting(ServerStartingEvent event) {
	messenger = NeoForgeKeystone.messenger(event.getServer(), LOGGER)
			.semantica(semantica)
			.build();
}

messenger.send(context.getSource(), messages.reloaded());
messenger.send(player.createCommandSourceStack(), messages.welcome(player.getScoreboardName()));
```

A library that works with the plugin's senders, such as a command framework that keeps cooldowns per player, asks the
messenger how it sees one: `messenger.recipient(sender)` returns the sender's name, unique id, client language and
whether it is the console.

### A plugin's own player type

A plugin that keeps its own player type, the same on every platform, builds the messenger over that type and says
once how Keystone sees one of its players:

```java
Messenger<GamePlayer> messenger = PaperKeystone.messenger(plugin, player -> new Recipient() {
			public String name() { return player.getName(); }
			public Optional<UUID> uniqueId() { return Optional.of(player.getUniqueId()); }
			public Optional<Locale> clientLanguage() { return Optional.ofNullable(player.getLocale()); }
			public Audience audience() { return player.getAudience(); }
		})
		.semantica(semantica)
		.build();

messenger.send(game.getPlayers(), messages.roundStarted(round));
```

## Let admins choose how a text is shown

Every message goes to the chat. A display file, such as `display.yml`, lists the messages shown as more than that,
by key; `true` stands for the message's own text, and chat stays on unless it says `false`:

```yaml
economy.balance:
  chat: false
  actionbar: true

chirper.restart:
  title:
    subtitle: "<gray>in {seconds} seconds"   # the title is the message itself
    times: { in: 10, stay: 60, out: 10 }      # ticks
  bossbar: { color: red, seconds: 10 }
  sound: { name: block.note_block.pling, volume: 1, pitch: 1.2 }
```

The plugin, and each of its modules, adds its own file under an owner's name:

```java
messenger.displays().add("myplugin", dataFolder.resolve("display.yml"));
messenger.displays().add("mymodule", moduleFolder.resolve("display.yml"));   // when the module enables
messenger.displays().remove("mymodule");                                     // when it disables
messenger.displays().reload();
```

A missing file has no entries, and the file added last wins a key that several files list. When a file cannot be
read, a `DisplayFileException` names the file and the entry; `add` then changes nothing, and after a failed
`reload` the displays read before stay in use.

## Text written in a config

A config field of type `Content` holds one string: the admin's own text, a translated message pulled in with
`{@key}`, or both. A field of type `Display` holds the parts, as in a display file, or one string for the chat alone.
Register Keystone's Jackson module with Configura to read and write both:

```java
import me.whereareiam.keystone.common.configura.KeystoneModule;

Configura configura = Configura.builder()
		.format(Format.YAML)
		.module(new KeystoneModule())
		.build();

public class Announcements {
	public Display welcome = Display.chat(Content.of("<gold>Welcome, {player}!"));
	public Display vote = Display.chat(Content.of("{@chirper.vote}"));
}
```

```yaml
welcome: "<gold>Welcome, {player}!"
vote:
  chat: "<gold>{@chirper.vote} <gray>(ends Sunday)"
  bossbar: { text: "<green>/vote", color: green, seconds: 10 }
```

```java
messenger.send(player, announcements.welcome.with("player", name));
```

Check each content after loading with `semantica.check(content)` and skip an entry with issues, naming its file and
key, so a mistyped reference doesn't reach players.

## Placeholders

Keystone fills the placeholders of other plugins when they are present: PlaceholderAPI on Paper and Bukkit,
PAPIProxyBridge on Velocity and BungeeCord, and MiniPlaceholders' global placeholders on Paper and Velocity.
Placeholders apply to what translators and admins wrote, never to values: a player who types `%player_name%` into a
chat message sees it as typed.

A plugin adds its own, as text filled in for each recipient or as MiniMessage tags that are the same for everyone:

```java
PaperKeystone.messenger(plugin)
		.semantica(semantica)
		.placeholders(placeholders -> placeholders
				.add((Placeholders.Text) (recipient, text) -> text.replace("{server}", serverName))
				.disable(MiniPlaceholdersIntegration.class))
		.build();
```

An integration is installed when its plugin is enabled before the messenger is built, so list it under `softdepend`.
PAPIProxyBridge answers over the network; a text waits for it at most 100 ms and is otherwise shown with its
placeholders as written.

## Languages

A recipient's languages are, in order: the language the player chose in Intercept, when Intercept runs on Paper or
Velocity, those of the plugin's own resolvers, the client's language, and the fallback. On Paper, list Intercept
under `softdepend`, and never relocate `me.whereareiam.intercept`: its API is loaded from Intercept. A plugin that
stores a player's choice itself adds a resolver:

```java
.languages(languages -> languages
		.fallback(Locale.ENGLISH)
		.resolver(recipient -> recipient.uniqueId().flatMap(storedLanguages::get)))
```

## Hold texts until a player is ready

A rule given to the builder holds the texts sent to a player, such as before they log in; the plugin releases them
in order when the player is ready:

```java
PaperKeystone.messenger(plugin)
		.semantica(semantica)
		.hold(recipient -> !loggedIn(recipient))
		.build();

messenger.release(player);   // after login
messenger.discard(player);   // when they leave without logging in
```

The console is never held. At most 256 texts are held per player; past that the oldest is dropped.

## What players write

`Markup` reads a chat message with only the tags a policy allows, and the legacy codes a filter accepts:

```java
Markup markup = PaperKeystone.markup(TagPolicy.of(TagPolicy.Group.COLOR, TagPolicy.Group.DECORATION))
		.legacyCodes(code -> !(code instanceof LegacyCode.Reset));

Component text = markup.read(typed);
```

A legacy code counts at the start, after whitespace, after another code or after a tag, so `R&D` stays as typed.
A colour ends the colours and decorations before it, as in the vanilla client.

## The console and the start banner

Texts sent to the console are written to the plugin's log with ANSI colours, in the fallback language. A banner
prints its lines between empty lines:

```java
messenger.print(Banner.of(
				Content.of("<gold>  █▀ █▀▀   <white>MyPlugin v<gray>{version}"),
				Content.of("<gold>  ▄█ █▄▄   <white>Platform: <gray>{platform}"))
		.with("version", version)
		.with("platform", "Paper"));
```

## Building

```sh
./gradlew test build
```

`VERSION=<version> ./gradlew publishToMavenLocal` lets a plugin build against a local version.
