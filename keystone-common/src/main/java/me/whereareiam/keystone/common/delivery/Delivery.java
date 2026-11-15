package me.whereareiam.keystone.common.delivery;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Shows the parts of a display to a player or another sender, unless the gate holds them.
 */
@RequiredArgsConstructor
public final class Delivery {
	private final TextRenderer renderer;
	private final Gatekeeper gatekeeper;
	private final Platform<?> platform;

	/**
	 * Shows a display now, or holds it when the gate says so.
	 *
	 * @param recipient the recipient
	 * @param display   the parts
	 * @param values    the values their texts see
	 */
	public void send(@NotNull Recipient recipient, @NotNull Display display, @NotNull Map<String, ?> values) {
		if (display.isEmpty() || gatekeeper.hold(recipient, display, values)) return;

		show(recipient, display, values);
	}

	/**
	 * Shows a display now, past the gate.
	 *
	 * @param recipient the recipient
	 * @param display   the parts
	 * @param values    the values their texts see
	 */
	public void show(@NotNull Recipient recipient, @NotNull Display display, @NotNull Map<String, ?> values) {
		Audience audience = recipient.audience();
		for (Part part : display.getParts()) show(recipient, audience, part, values);
	}

	private void show(Recipient recipient, Audience audience, Part part, Map<String, ?> values) {
		switch (part) {
			case Chat chat -> audience.sendMessage(renderer.render(recipient, chat.getText(), values));
			case ActionBar bar -> audience.sendActionBar(renderer.render(recipient, bar.getText(), values));
			case Title title -> audience.showTitle(net.kyori.adventure.title.Title.title(
					renderer.render(recipient, title.getTitle(), values),
					renderer.render(recipient, title.getSubtitle(), values),
					net.kyori.adventure.title.Title.Times.times(title.getFadeIn(), title.getStay(), title.getFadeOut())
			));
			case BossBar bar -> showBossBar(recipient, audience, bar, values);
			case Sound sound -> audience.playSound(net.kyori.adventure.sound.Sound.sound(
					sound.getName(),
					sound.getSource(),
					sound.getVolume(),
					sound.getPitch()
			));
		}
	}

	private void showBossBar(Recipient recipient, Audience audience, BossBar bar, Map<String, ?> values) {
		net.kyori.adventure.bossbar.BossBar shown = net.kyori.adventure.bossbar.BossBar.bossBar(
				renderer.render(recipient, bar.getText(), values),
				bar.getProgress(),
				bar.getColor(),
				bar.getOverlay()
		);

		audience.showBossBar(shown);
		platform.later(bar.getDuration(), () -> audience.hideBossBar(shown));
	}
}
