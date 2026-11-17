package me.whereareiam.keystone.common.display;

import me.whereareiam.keystone.Displays;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The display files of a messenger by owner, merged into one lookup by message key; the file added last wins a key
 * several files list.
 */
public final class DisplayLayers implements Displays {
	private final DisplayFile reader = new DisplayFile();
	private final Map<String, Path> files = new LinkedHashMap<>();
	private final Map<String, Map<String, Display>> layers = new LinkedHashMap<>();
	private volatile Map<String, Display> entries = Map.of();

	@Override
	public synchronized void add(@NotNull String owner, @NotNull Path file) {
		Objects.requireNonNull(owner, "owner");
		Map<String, Display> read = reader.read(Objects.requireNonNull(file, "file"));

		files.remove(owner);
		layers.remove(owner);
		files.put(owner, file);
		layers.put(owner, read);
		merge();
	}

	@Override
	public synchronized void remove(@NotNull String owner) {
		files.remove(owner);
		layers.remove(owner);
		merge();
	}

	@Override
	public synchronized void reload() {
		Map<String, Map<String, Display>> read = new LinkedHashMap<>();
		files.forEach((owner, file) -> read.put(owner, reader.read(file)));

		layers.clear();
		layers.putAll(read);
		merge();
	}

	/**
	 * Returns the display of a translated message, with the message's values.
	 *
	 * @param message the message
	 * @return its entry, or the chat alone when no file lists it
	 */
	public @NotNull Display of(@NotNull Message message) {
		Display display = entries.get(message.getKey());
		if (display == null) display = Display.chat(Content.of(Message.of(message.getKey())));

		return display.with(message.getValues());
	}

	private void merge() {
		Map<String, Display> merged = new HashMap<>();
		layers.values().forEach(merged::putAll);

		entries = Map.copyOf(merged);
	}
}
