package me.whereareiam.keystone.recipient;

import me.whereareiam.keystone.model.Banner;
import org.jetbrains.annotations.NotNull;

/**
 * The server's console. It writes each text to the plugin's log with ANSI colours, in the fallback language;
 * of a display it writes the texts of the chat, action bar, title and boss bar, and plays no sound.
 */
public interface Console extends Recipient {
	/**
	 * Writes a banner: an empty line, each of its lines, and an empty line.
	 * <pre>{@code
	 * keystone.console().print(Banner.of(
	 *         Content.of("<gold>  █▀ █▀▀   <white>Socialismus v<gray>{version}"),
	 *         Content.of("<gold>  ▄█ █▄▄   <white>Platform: <gray>{platform}"))
	 *         .values(Values.of("version", version).and("platform", "Paper")));
	 * }</pre>
	 *
	 * @param banner the banner
	 */
	void print(@NotNull Banner banner);
}
