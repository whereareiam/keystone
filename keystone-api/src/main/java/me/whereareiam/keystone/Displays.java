package me.whereareiam.keystone;

import me.whereareiam.keystone.exception.DisplayFileException;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The display files of a messenger, each added by an owner, such as the plugin or one of its modules.
 * <p>
 * A display file lists, by message key, the translated messages shown as more than chat; every other message is
 * shown in the chat alone. When two files list the same key, the file added last wins. An owner's file is taken
 * away with the owner, so a module that is disabled leaves no entries behind.
 * <pre>{@code
 * messenger.displays().add("mentionator", context.dataFolder().resolve("display.yml"));   // enable()
 * messenger.displays().remove("mentionator");                                             // disable()
 * }</pre>
 * <pre>{@code
 * commands.balance:
 *   chat: false
 *   actionbar: true                 # the message's own text
 * }</pre>
 */
public interface Displays {
	/**
	 * Reads an owner's display file and adds it, replacing the file the owner added before. A missing file has no
	 * entries.
	 *
	 * @param owner the owner, such as the plugin's or a module's name
	 * @param file  the display file
	 * @throws DisplayFileException when the file or one of its entries cannot be read; nothing changes then
	 */
	void add(@NotNull String owner, @NotNull Path file);

	/**
	 * Takes an owner's display file away.
	 *
	 * @param owner the owner
	 */
	void remove(@NotNull String owner);

	/**
	 * Reads every display file again.
	 *
	 * @throws DisplayFileException when a file or one of its entries cannot be read; the entries read before stay in
	 *                              use then
	 */
	void reload();
}
