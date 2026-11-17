package me.whereareiam.keystone.type.part;

import me.whereareiam.semantica.Content;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartTest {
	@Test
	void aTitleHasTheClientsTimesAndNoSubtitleByDefault() {
		Title title = Title.of(Content.of("Big"));

		assertEquals(Content.of(""), title.getSubtitle());
		assertEquals(Duration.ofMillis(500), title.getFadeIn());
		assertEquals(Duration.ofMillis(3500), title.getStay());
		assertEquals(Duration.ofSeconds(1), title.getFadeOut());
	}

	@Test
	void aBossBarIsWhiteFullAndShownForFiveSeconds() {
		BossBar bar = BossBar.of(Content.of("Boss"));

		assertEquals(net.kyori.adventure.bossbar.BossBar.Color.WHITE, bar.getColor());
		assertEquals(net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS, bar.getOverlay());
		assertEquals(1F, bar.getProgress());
		assertEquals(Duration.ofSeconds(5), bar.getDuration());
	}

	@Test
	void aBossBarsProgressAndDurationAreChecked() {
		assertThrows(IllegalArgumentException.class, () -> BossBar.builder().text(Content.of("x")).progress(1.5F).build());
		assertThrows(IllegalArgumentException.class, () -> BossBar.builder().text(Content.of("x")).duration(Duration.ZERO).build());
		assertThrows(NullPointerException.class, () -> BossBar.builder().build());
	}

	@Test
	void aSoundPlaysAtItsOwnVolumeAndPitch() {
		Sound sound = Sound.of(Key.key("block.note_block.pling"));

		assertEquals(net.kyori.adventure.sound.Sound.Source.MASTER, sound.getSource());
		assertEquals(1F, sound.getVolume());
		assertEquals(1F, sound.getPitch());
	}

	@Test
	void textPartsNeedAText() {
		assertThrows(NullPointerException.class, () -> Chat.of(null));
		assertThrows(NullPointerException.class, () -> ActionBar.of(null));
		assertEquals(Content.of("x"), Chat.of(Content.of("x")).getText());
	}
}
