package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.databind.JsonMappingException;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeystoneModuleTest {
	private final Configura configura = Configura.builder().format(Format.YAML).module(new KeystoneModule()).build();

	@Test
	void readsAndWritesAContentAsItsSource() throws Exception {
		Announcement read = read("text: \"<gold>{@chirper.vote} {player}\"\n", Announcement.class);

		assertEquals(Content.of("<gold>{@chirper.vote} {player}"), read.text);
		assertTrue(write(read).contains("<gold>{@chirper.vote} {player}"));
	}

	@Test
	void readsEveryPart() throws Exception {
		Announcement read = read("""
				display:
				  chat: "{@chirper.vote}"
				  actionbar: "<green>/vote"
				  title: { title: "<gold>Vote", subtitle: "<gray>now", times: { in: 0, stay: 40, out: 20 } }
				  bossbar: { text: "<green>/vote", color: green, overlay: notched_10, progress: 0.25, seconds: 2.5 }
				  sound: { name: "minecraft:block.note_block.pling", source: player, volume: 0.5, pitch: 1.2 }
				""", Announcement.class);

		assertEquals(Display.of(
				Chat.of(Content.of("{@chirper.vote}")),
				ActionBar.of(Content.of("<green>/vote")),
				Title.builder().title(Content.of("<gold>Vote")).subtitle(Content.of("<gray>now"))
						.fadeIn(Duration.ZERO).stay(Duration.ofSeconds(2)).fadeOut(Duration.ofSeconds(1)).build(),
				BossBar.builder().text(Content.of("<green>/vote")).color(net.kyori.adventure.bossbar.BossBar.Color.GREEN)
						.overlay(net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_10).progress(0.25F)
						.duration(Duration.ofMillis(2500)).build(),
				Sound.builder().name(Key.key("block.note_block.pling")).source(net.kyori.adventure.sound.Sound.Source.PLAYER)
						.volume(0.5F).pitch(1.2F).build()
		), read.display);
	}

	@Test
	void readsTheShortForms() throws Exception {
		Announcement read = read("""
				display:
				  title: "<gold>Vote"
				  bossbar: "<green>/vote"
				  sound: block.note_block.pling
				""", Announcement.class);

		assertEquals(Display.of(
				Title.of(Content.of("<gold>Vote")),
				BossBar.of(Content.of("<green>/vote")),
				Sound.of(Key.key("block.note_block.pling"))
		), read.display);
	}

	@Test
	void aStringIsTheChatAlone() throws Exception {
		assertEquals(Display.chat(Content.of("<gold>Welcome")), read("display: \"<gold>Welcome\"\n", Announcement.class).display);
	}

	@Test
	void writesWhatItReadsLeavingOutDefaults() throws Exception {
		Announcement announcement = new Announcement();
		announcement.display = Display.of(
				Chat.of(Content.of("{@chirper.vote}")),
				Title.of(Content.of("<gold>Vote")),
				BossBar.builder().text(Content.of("/vote")).color(net.kyori.adventure.bossbar.BossBar.Color.GREEN).build(),
				Sound.of(Key.key("block.note_block.pling"))
		);

		String written = write(announcement);

		assertEquals(announcement.display, read(written, Announcement.class).display);
		assertTrue(written.contains("color: \"green\"") || written.contains("color: green"), written);
		assertTrue(!written.contains("times") && !written.contains("subtitle") && !written.contains("seconds"), written);
		assertTrue(!written.contains("volume") && written.contains("block.note_block.pling"), written);
	}

	@Test
	void writesChangedTimesInTicks() throws Exception {
		Announcement announcement = new Announcement();
		announcement.display = Display.of(Title.builder().title(Content.of("T")).stay(Duration.ofSeconds(3)).build());

		String written = write(announcement);

		assertTrue(written.contains("stay: 60"), written);
		assertEquals(announcement.display, read(written, Announcement.class).display);
	}

	@Test
	void refusesWhatIsNoDisplay() {
		assertThrows(JsonMappingException.class, () -> read("display: { hologram: x }\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: { bossbar: { text: x, color: teal } }\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: { title: { subtitle: x } }\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: { title: { title: x, times: { in: -1 } } }\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: { bossbar: { text: x, progress: 2 } }\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: { sound: { name: \"Not A Key\" } }\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: [chat]\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("text: [a]\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: { actionbar: true }\n", Announcement.class));
		assertThrows(JsonMappingException.class, () -> read("display: { sound: { volume: 1 } }\n", Announcement.class));
	}

	private <T> T read(String yaml, Class<T> type) throws Exception {
		return configura.mapper().readValue(yaml, type);
	}

	private String write(Object value) throws Exception {
		return new String(configura.mapper().writeValueAsBytes(value), StandardCharsets.UTF_8);
	}

	public static final class Announcement {
		public Content text = Content.of("");
		public Display display = Display.none();
	}
}
