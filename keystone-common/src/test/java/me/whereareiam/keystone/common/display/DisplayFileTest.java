package me.whereareiam.keystone.common.display;

import me.whereareiam.keystone.exception.DisplayFileException;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DisplayFileTest {
	@TempDir
	Path folder;

	@Test
	void aMessageWithoutEntryGoesToTheChat() {
		DisplayFile file = new DisplayFile(folder.resolve("display.yml"));
		file.load();

		assertEquals(Display.chat(Content.of("{@commands.cooldown}")), file.of("commands.cooldown"));
		assertEquals(Display.chat(Content.of("{@commands.cooldown}")), new DisplayFile(null).of("commands.cooldown"));
	}

	@Test
	void trueIsTheMessagesOwnTextAndChatStaysUnlessFalse() throws IOException {
		DisplayFile file = file("""
				commands.cooldown:
				  chat: false
				  actionbar: true
				commands.reloaded:
				  sound: block.note_block.pling
				""");

		assertEquals(Display.of(ActionBar.of(Content.of("{@commands.cooldown}"))), file.of("commands.cooldown"));
		assertEquals(Display.of(
				Chat.of(Content.of("{@commands.reloaded}")),
				Sound.of(Key.key("block.note_block.pling"))
		), file.of("commands.reloaded"));
	}

	@Test
	void aTitleOrBossBarWithoutTextShowsTheMessage() throws IOException {
		DisplayFile file = file("""
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
		), file.of("chirper.restart"));
	}

	@Test
	void anEmptyFileHasNoEntries() throws IOException {
		assertEquals(Display.chat(Content.of("{@a}")), file("").of("a"));
	}

	@Test
	void anInvalidEntryNamesItsKey() throws IOException {
		Files.writeString(folder.resolve("display.yml"), """
				commands.cooldown:
				  hologram: true
				""");
		DisplayFile file = new DisplayFile(folder.resolve("display.yml"));

		DisplayFileException exception = assertThrows(DisplayFileException.class, file::load);
		assertEquals("commands.cooldown", exception.getKey());
		assertEquals(folder.resolve("display.yml"), exception.getPath());
	}

	@Test
	void anEntryIsAMap() throws IOException {
		Files.writeString(folder.resolve("display.yml"), "commands.cooldown: true\n");

		assertThrows(DisplayFileException.class, () -> new DisplayFile(folder.resolve("display.yml")).load());
	}

	@Test
	void aFailedReloadKeepsTheEntriesReadBefore() throws IOException {
		DisplayFile file = file("a:\n  actionbar: true\n");
		Files.writeString(folder.resolve("display.yml"), "a: [");

		assertThrows(DisplayFileException.class, file::load);
		assertEquals(Display.of(Chat.of(Content.of("{@a}")), ActionBar.of(Content.of("{@a}"))), file.of("a"));
	}

	private DisplayFile file(String text) throws IOException {
		Path path = folder.resolve("display.yml");
		Files.writeString(path, text);

		DisplayFile file = new DisplayFile(path);
		file.load();

		return file;
	}
}
