package me.whereareiam.keystone.common.console;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.display.DisplayFile;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.platform.Sender;
import me.whereareiam.keystone.common.recipient.LanguageChain;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Banner;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.model.Values;
import me.whereareiam.keystone.recipient.Console;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The console: renders each text in the fallback language and writes it to the plugin's log, one line per line of
 * the text, with ANSI colours. It is never held by the gate.
 */
@RequiredArgsConstructor
public final class DefaultConsole implements Console {
	private final Sender sender;
	private final Platform<?> platform;
	private final TextRenderer renderer;
	private final LanguageChain languages;
	private final DisplayFile displays;
	private final AnsiSerializer ansi = new AnsiSerializer();

	@Override
	public @NotNull String name() {
		return sender.name();
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return Optional.empty();
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		return Optional.empty();
	}

	@Override
	public @NotNull List<Locale> languages() {
		return languages.resolve(this);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return true;
	}

	@Override
	public @NotNull Audience audience() {
		return sender.audience();
	}

	@Override
	public void send(@NotNull Message message) {
		write(displays.of(message.getKey()), message.getValues());
	}

	@Override
	public void send(@NotNull Content content, @NotNull Values values) {
		write(Display.chat(content), values.asMap());
	}

	@Override
	public void send(@NotNull Display display, @NotNull Values values) {
		write(display, values.asMap());
	}

	@Override
	public void print(@NotNull Banner banner) {
		platform.log("");
		for (Content line : banner.getLines()) write(line, banner.getValues().asMap());
		platform.log("");
	}

	private void write(Display display, Map<String, ?> values) {
		for (Part part : display.getParts()) {
			switch (part) {
				case Chat chat -> write(chat.getText(), values);
				case ActionBar bar -> write(bar.getText(), values);
				case Title title -> {
					write(title.getTitle(), values);
					if (!title.getSubtitle().getSource().isEmpty()) write(title.getSubtitle(), values);
				}
				case BossBar bar -> write(bar.getText(), values);
				case Sound ignored -> {
				}
			}
		}
	}

	private void write(Content content, Map<String, ?> values) {
		String text = ansi.serialize(renderer.render(this, content, values));
		for (String line : text.split("\n", -1)) platform.log(line);
	}
}
