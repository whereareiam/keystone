package me.whereareiam.keystone.common.display;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.keystone.common.configura.KeystoneModule;
import me.whereareiam.keystone.exception.DisplayFileException;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The display file, {@code display.yml}: the displays of the translated messages shown as more than chat, by the
 * message's key. Every other message is shown in the chat alone. A missing file has no entries.
 * <pre>{@code
 * commands.cooldown:
 *   chat: false
 *   actionbar: true                 # the message's own text
 * }</pre>
 */
public final class DisplayFile {
	private final @Nullable Path path;
	private final Configura configura;
	private volatile Map<String, Display> entries = Map.of();

	/**
	 * Creates the display file; {@link #load()} reads it.
	 *
	 * @param path the file, or {@code null} when the plugin keeps none
	 */
	public DisplayFile(@Nullable Path path) {
		this.path = path;
		this.configura = Configura.builder()
				.format(Format.YAML)
				.module(new KeystoneModule())
				.build();
	}

	/**
	 * Returns the display of a translated message.
	 *
	 * @param key the message's key
	 * @return its entry, or the chat alone when it has none
	 */
	public @NotNull Display of(@NotNull String key) {
		Display display = entries.get(key);
		if (display != null) return display;

		return Display.chat(Content.of(DisplayEntry.reference(key)));
	}

	/**
	 * Reads the file; the entries read before stay in use when it fails.
	 *
	 * @throws DisplayFileException when the file or an entry cannot be read
	 */
	public void load() {
		if (path == null || !Files.exists(path)) {
			entries = Map.of();
			return;
		}

		JsonNode root = readRoot(path);
		Map<String, Display> read = new LinkedHashMap<>();
		Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> field = fields.next();
			read.put(field.getKey(), entry(path, field.getKey(), field.getValue()));
		}

		entries = Collections.unmodifiableMap(read);
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
		ObjectMapper mapper = configura.mapper();
		try {
			return mapper.treeToValue(DisplayEntry.normalize(key, node, mapper), Display.class);
		} catch (Exception exception) {
			throw new DisplayFileException(path, key, exception.getMessage(), exception);
		}
	}
}
