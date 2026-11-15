package me.whereareiam.keystone.common.fixture;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Records what is shown to it, one line per part, such as {@code chat: Hello} or {@code title: Big | small}.
 */
public final class RecordingAudience implements Audience {
	private final List<String> shown = new ArrayList<>();
	private final List<Component> messages = new ArrayList<>();
	private final List<BossBar> bossBars = new ArrayList<>();

	public @NotNull List<String> shown() {
		return shown;
	}

	public @NotNull List<Component> messages() {
		return messages;
	}

	public @NotNull List<BossBar> bossBars() {
		return bossBars;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		messages.add(message);
		shown.add("chat: " + plain(message));
	}

	@Override
	public void sendActionBar(@NotNull Component message) {
		shown.add("actionbar: " + plain(message));
	}

	@Override
	public void showTitle(@NotNull Title title) {
		shown.add("title: " + plain(title.title()) + " | " + plain(title.subtitle()) + " " + title.times());
	}

	@Override
	public void showBossBar(@NotNull BossBar bar) {
		bossBars.add(bar);
		shown.add("bossbar: " + plain(bar.name()) + " " + bar.color() + " " + bar.progress());
	}

	@Override
	public void hideBossBar(@NotNull BossBar bar) {
		bossBars.remove(bar);
		shown.add("hide bossbar: " + plain(bar.name()));
	}

	@Override
	public void playSound(@NotNull Sound sound) {
		shown.add("sound: " + sound.name().asString() + " " + sound.source() + " " + sound.volume() + " " + sound.pitch());
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}
}
