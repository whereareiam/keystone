package me.whereareiam.keystone.common.display;

import me.whereareiam.keystone.exception.DisplayFileException;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DisplayLayersTest {
	@TempDir
	Path folder;

	private final DisplayLayers layers = new DisplayLayers();

	@Test
	void aMessageWithoutEntryGoesToTheChatWithItsValues() {
		layers.add("plugin", folder.resolve("missing.yml"));

		assertEquals(Display.chat(Content.of("{@commands.balance}")).with("amount", 5), layers.of(Message.of("commands.balance").with("amount", 5)));
	}

	@Test
	void trueIsTheMessagesOwnTextAndChatStaysUnlessFalse() throws IOException {
		add("plugin", """
				commands.balance:
				  chat: false
				  actionbar: true
				commands.reloaded:
				  sound: block.note_block.pling
				""");

		assertEquals(Display.of(ActionBar.of(Content.of("{@commands.balance}"))), of("commands.balance"));
		assertEquals(Display.of(
				Chat.of(Content.of("{@commands.reloaded}")),
				Sound.of(Key.key("block.note_block.pling"))
		), of("commands.reloaded"));
	}

	@Test
	void aTitleOrBossBarWithoutTextShowsTheMessage() throws IOException {
		add("plugin", """
				chirper.restart:
				  chat: false
				  title:
				    subtitle: "<gray>in {seconds} seconds"
				    times: { in: 10, stay: 60, out: 10 }
				  bossbar: true
				""");

		assertEquals(Display.of(
				Title.builder()
						.title(Content.of("{@chirper.restart}"))
						.subtitle(Content.of("<gray>in {seconds} seconds"))
						.fadeIn(Duration.ofMillis(500))
						.stay(Duration.ofSeconds(3))
						.fadeOut(Duration.ofMillis(500))
						.build(),
				BossBar.of(Content.of("{@chirper.restart}"))
		), of("chirper.restart"));
	}

	@Test
	void theFileAddedLastWinsAndRemovingAnOwnerTakesItsEntriesAway() throws IOException {
		add("plugin", "a:\n  actionbar: true\nb:\n  actionbar: true\n");
		add("module", "a:\n  chat: false\n  title: true\n");

		assertEquals(Display.of(Title.of(Content.of("{@a}"))), of("a"));
		assertEquals(Display.of(Chat.of(Content.of("{@b}")), ActionBar.of(Content.of("{@b}"))), of("b"));

		layers.remove("module");
		assertEquals(Display.of(Chat.of(Content.of("{@a}")), ActionBar.of(Content.of("{@a}"))), of("a"));
	}

	@Test
	void reloadReadsEveryFileAgain() throws IOException {
		Path file = add("plugin", "a:\n  actionbar: true\n");
		Files.writeString(file, "a:\n  chat: false\n  title: true\n");

		layers.reload();

		assertEquals(Display.of(Title.of(Content.of("{@a}"))), of("a"));
	}

	@Test
	void anEmptyFileHasNoEntries() throws IOException {
		add("plugin", "");

		assertEquals(Display.chat(Content.of("{@a}")), of("a"));
	}

	@Test
	void anInvalidEntryNamesItsFileAndKeyAndChangesNothing() throws IOException {
		Path file = folder.resolve("display.yml");
		Files.writeString(file, """
				commands.balance:
				  hologram: true
				""");

		DisplayFileException exception = assertThrows(DisplayFileException.class, () -> layers.add("plugin", file));
		assertEquals("commands.balance", exception.getKey());
		assertEquals(file, exception.getPath());
		assertEquals(Display.chat(Content.of("{@commands.balance}")), of("commands.balance"));
	}

	@Test
	void anEntryIsAMap() throws IOException {
		Path file = folder.resolve("display.yml");
		Files.writeString(file, "commands.balance: true\n");

		assertThrows(DisplayFileException.class, () -> layers.add("plugin", file));
	}

	@Test
	void aFailedReloadKeepsTheEntriesReadBefore() throws IOException {
		Path file = add("plugin", "a:\n  actionbar: true\n");
		Files.writeString(file, "a: [");

		assertThrows(DisplayFileException.class, layers::reload);
		assertEquals(Display.of(Chat.of(Content.of("{@a}")), ActionBar.of(Content.of("{@a}"))), of("a"));
	}

	private Path add(String owner, String text) throws IOException {
		Path file = folder.resolve(owner + ".yml");
		Files.writeString(file, text);
		layers.add(owner, file);

		return file;
	}

	private Display of(String key) {
		return layers.of(Message.of(key));
	}
}
