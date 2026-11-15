package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Content;

/**
 * Keystone's Jackson module for Configura: reads and writes a {@link Content} as one string and a {@link Display}
 * as a map of parts, so a plugin's config models hold them as fields.
 * <pre>{@code
 * Configura configura = Configura.builder()
 *         .format(Format.YAML)
 *         .module(new KeystoneModule())
 *         .build();
 *
 * public class Announcement {
 *     public Display display = Display.chat(Content.of("<gold>Welcome to the server!"));
 * }
 * }</pre>
 * <pre>{@code
 * display:
 *   chat: "{@chirper.vote}"
 *   actionbar: "<green>/vote"
 *   title: { title: "<gold>Vote", subtitle: "<gray>for rewards", times: { in: 10, stay: 60, out: 10 } }
 *   bossbar: { text: "<green>/vote", color: green, overlay: progress, progress: 1.0, seconds: 10 }
 *   sound: { name: block.note_block.pling, source: master, volume: 1, pitch: 1.2 }
 * }</pre>
 * Times of a title are in ticks, a boss bar's duration in seconds. A text may reference translated messages with
 * {@code {@key}}; check it with {@code semantica.check(content)} after loading.
 */
public final class KeystoneModule extends SimpleModule {
	/**
	 * Creates the module.
	 */
	public KeystoneModule() {
		super("Keystone");

		addSerializer(Content.class, new ContentSerializer());
		addDeserializer(Content.class, new ContentDeserializer());
		addSerializer(Display.class, new DisplaySerializer());
		addDeserializer(Display.class, new DisplayDeserializer());
	}
}
