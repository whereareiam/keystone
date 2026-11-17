package me.whereareiam.keystone.common.display;

import com.fasterxml.jackson.databind.JsonNode;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.keystone.common.configura.DisplayDocument;
import me.whereareiam.keystone.common.configura.KeystoneModule;
import me.whereareiam.keystone.exception.DisplayFileException;
import me.whereareiam.keystone.model.Display;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads a display file, such as {@code display.yml}: the displays of the translated messages shown as more than
 * chat, by the message's key. A missing file has no entries.
 * <pre>{@code
 * commands.balance:
 *   chat: false
 *   actionbar: true                 # the message's own text
 * }</pre>
 */
final class DisplayFile {
	private final Configura configura = Configura.builder()
			.format(Format.YAML)
			.module(new KeystoneModule())
			.build();

	/**
	 * Reads a file.
	 *
	 * @param path the file
	 * @return its entries by message key
	 * @throws DisplayFileException when the file or an entry cannot be read
	 */
	@NotNull Map<String, Display> read(@NotNull Path path) {
		if (!Files.exists(path)) return Map.of();

		JsonNode root = readRoot(path);
		Map<String, Display> read = new LinkedHashMap<>();
		Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> field = fields.next();
			read.put(field.getKey(), entry(path, field.getKey(), field.getValue()));
		}

		return Collections.unmodifiableMap(read);
	}

	private JsonNode readRoot(Path path) {
		JsonNode root;
		try {
			root = configura.readNode(path);
		} catch (RuntimeException exception) {
			throw new DisplayFileException(path, null, "cannot be read: " + exception.getMessage(), exception);
		}

		if (root == null || root.isNull() || root.isMissingNode()) return configura.mapper().createObjectNode();
		if (!root.isObject()) throw new DisplayFileException(path, null, "holds no entries by message key", null);

		return root;
	}

	private Display entry(Path path, String key, JsonNode node) {
		if (!node.isObject()) throw new DisplayFileException(path, key, "an entry is a map of parts, such as chat: false and actionbar: true", null);

		try {
			return configura.mapper().treeToValue(node, DisplayDocument.class).toEntry(key);
		} catch (Exception exception) {
			throw new DisplayFileException(path, key, exception.getMessage(), exception);
		}
	}
}
