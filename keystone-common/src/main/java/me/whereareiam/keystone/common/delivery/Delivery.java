package me.whereareiam.keystone.common.delivery;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.console.ConsoleWriter;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Shows a display to recipients: the console gets it in the plugin's log, held players get it later, and everyone
 * else is shown each part through their audience.
 */
@RequiredArgsConstructor
public final class Delivery {
	private final TextRenderer renderer;
	private final Holding holding;
	private final ConsoleWriter console;
	private final Platform platform;

	/**
	 * Sends a display to recipients, holding it for those the rule holds.
	 *
	 * @param recipients the recipients
	 * @param display    the display with its values
	 */
	public void send(@NotNull List<Recipient> recipients, @NotNull Display display) {
		if (display.isEmpty()) return;

		List<Recipient> shown = new ArrayList<>(recipients.size());
		for (Recipient recipient : recipients) {
			if (recipient.isConsole()) {
				console.write(recipient, display);
				continue;
			}

			if (!holding.hold(recipient, display)) shown.add(recipient);
		}

		show(shown, display);
	}

	/**
	 * Shows a display to recipients now, without asking the rule.
	 *
	 * @param recipients the recipients, none of them the console
	 * @param display    the display with its values
	 */
	public void show(@NotNull List<Recipient> recipients, @NotNull Display display) {
		if (recipients.isEmpty()) return;

		Map<String, Object> values = display.getValues();
		for (Part part : display.getParts()) show(recipients, part, values);
	}

	private void show(List<Recipient> recipients, Part part, Map<String, Object> values) {
		switch (part) {
			case Chat chat -> each(recipients, renderer.render(chat.getText(), values, recipients), Audience::sendMessage);
			case ActionBar bar -> each(recipients, renderer.render(bar.getText(), values, recipients), Audience::sendActionBar);
			case Title title -> showTitles(recipients, title, values);
			case BossBar bar -> showBossBars(recipients, bar, values);
			case Sound sound -> {
				net.kyori.adventure.sound.Sound played = net.kyori.adventure.sound.Sound.sound(
						sound.getName(),
						sound.getSource(),
						sound.getVolume(),
						sound.getPitch()
				);
				recipients.forEach(recipient -> recipient.audience().playSound(played));
			}
		}
	}

	private void showTitles(List<Recipient> recipients, Title title, Map<String, Object> values) {
		List<Component> titles = renderer.render(title.getTitle(), values, recipients);
		List<Component> subtitles = renderer.render(title.getSubtitle(), values, recipients);
		net.kyori.adventure.title.Title.Times times = net.kyori.adventure.title.Title.Times.times(title.getFadeIn(), title.getStay(), title.getFadeOut());

		for (int index = 0; index < recipients.size(); index++)
			recipients.get(index).audience().showTitle(net.kyori.adventure.title.Title.title(titles.get(index), subtitles.get(index), times));
	}

	private void showBossBars(List<Recipient> recipients, BossBar bar, Map<String, Object> values) {
		List<Component> texts = renderer.render(bar.getText(), values, recipients);
		Map<Component, net.kyori.adventure.bossbar.BossBar> bars = new HashMap<>();
		List<Runnable> hides = new ArrayList<>(recipients.size());

		for (int index = 0; index < recipients.size(); index++) {
			Audience audience = recipients.get(index).audience();
			net.kyori.adventure.bossbar.BossBar shown = bars.computeIfAbsent(texts.get(index), text ->
					net.kyori.adventure.bossbar.BossBar.bossBar(text, bar.getProgress(), bar.getColor(), bar.getOverlay()));

			audience.showBossBar(shown);
			hides.add(() -> audience.hideBossBar(shown));
		}

		platform.later(bar.getDuration(), () -> hides.forEach(Runnable::run));
	}

	private static void each(List<Recipient> recipients, List<Component> components, BiConsumer<Audience, Component> show) {
		for (int index = 0; index < recipients.size(); index++) show.accept(recipients.get(index).audience(), components.get(index));
	}
}
