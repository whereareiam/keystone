package me.whereareiam.keystone.common.display;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Turns an entry of the display file into a display as a config writes it. In the display file a part may say
 * {@code true} for the message's own text, a title or boss bar may leave its text out for the same, and chat is
 * shown unless it says {@code false}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class DisplayEntry {
	private static final String CHAT = "chat";
	private static final String ACTION_BAR = "actionbar";
	private static final String TITLE = "title";
	private static final String BOSS_BAR = "bossbar";
	private static final String TEXT = "text";

	/**
	 * Returns the content of a message's own text.
	 *
	 * @param key the message's key
	 * @return the reference, {@code {@key}}
	 */
	static @NotNull String reference(@NotNull String key) {
		return "{@" + key + "}";
	}

	/**
	 * Normalizes an entry.
	 *
	 * @param key    the message's key
	 * @param node   the entry as written
	 * @param mapper the mapper that makes new nodes
	 * @return the entry as a display
	 * @throws IllegalArgumentException when the entry is no map of parts
	 */
	static @NotNull JsonNode normalize(@NotNull String key, @NotNull JsonNode node, @NotNull ObjectMapper mapper) {
		if (!node.isObject()) throw new IllegalArgumentException("An entry is a map of parts, such as chat: false and actionbar: true");

		String own = reference(key);
		ObjectNode entry = mapper.createObjectNode();
		if (!node.has(CHAT)) entry.put(CHAT, own);
		entry.setAll((ObjectNode) node.deepCopy());

		text(entry, CHAT, own);
		text(entry, ACTION_BAR, own);
		titled(entry, TITLE, TITLE, own, mapper);
		titled(entry, BOSS_BAR, TEXT, own, mapper);

		return entry;
	}

	private static void text(ObjectNode entry, String part, String own) {
		JsonNode value = entry.get(part);
		if (value == null || !value.isBoolean()) return;

		if (value.booleanValue()) entry.put(part, own);
		else entry.remove(part);
	}

	private static void titled(ObjectNode entry, String part, String field, String own, ObjectMapper mapper) {
		JsonNode value = entry.get(part);
		if (value == null) return;

		if (value.isBoolean() && !value.booleanValue()) {
			entry.remove(part);
			return;
		}

		if (value.isBoolean()) {
			entry.set(part, mapper.createObjectNode().put(field, own));
			return;
		}

		if (value.isObject() && !value.has(field)) ((ObjectNode) value).put(field, own);
	}
}
