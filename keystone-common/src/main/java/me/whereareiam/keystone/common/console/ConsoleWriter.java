package me.whereareiam.keystone.common.console;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Banner;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Writes texts for the console to the plugin's log, with ANSI colours: the text of each part, a title with its
 * subtitle, and no sound.
 */
@RequiredArgsConstructor
public final class ConsoleWriter {
	private final TextRenderer renderer;
	private final Platform platform;
	private final AnsiSerializer ansi = new AnsiSerializer();

	/**
	 * Writes a display.
	 *
	 * @param console the console the display was sent to
	 * @param display the display with its values
	 */
	public void write(@NotNull Recipient console, @NotNull Display display) {
		Map<String, Value> values = display.getValues();
		for (Part part : display.getParts()) {
			switch (part) {
				case Chat chat -> write(console, chat.getText(), values);
				case ActionBar bar -> write(console, bar.getText(), values);
				case Title title -> {
					write(console, title.getTitle(), values);
					if (!title.getSubtitle().getSource().isEmpty()) write(console, title.getSubtitle(), values);
				}
				case BossBar bar -> write(console, bar.getText(), values);
				case Sound ignored -> {
				}
			}
		}
	}

	/**
	 * Writes a banner between empty lines.
	 *
	 * @param banner the banner
	 */
	public void print(@NotNull Banner banner) {
		Recipient console = platform.console();

		platform.log("");
		for (Content line : banner.getLines()) write(console, line, banner.getValues());
		platform.log("");
	}

	private void write(Recipient console, Content content, Map<String, Value> values) {
		String text = ansi.serialize(renderer.render(content, values, console));
		for (String line : text.split("\n", -1)) platform.log(line);
	}
}
