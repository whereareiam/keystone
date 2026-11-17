package me.whereareiam.keystone;

import me.whereareiam.keystone.model.Banner;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Delivers texts to the plugin's senders, each in the sender's own language, with their placeholders, as the parts
 * the admins chose.
 * <p>
 * A platform's {@code *Keystone} class builds the messenger. The plugin chooses who receives a text; the messenger
 * only shows it. A text for several senders is rendered once for each distinct result, so players who share a
 * language and whose placeholders fill in alike share one rendering.
 * <pre>{@code
 * Messenger<CommandSender> messenger = PaperKeystone.messenger(plugin)
 *         .semantica(semantica)
 *         .build();
 * messenger.displays().add("myplugin", dataFolder.resolve("display.yml"));
 *
 * messenger.send(sender, messages.balance(amount));                 // display.yml decides its parts
 * messenger.send(Bukkit.getOnlinePlayers(), messages.restart(30));
 * messenger.send(player, config.getWelcome().with("player", name)); // a Display from a config
 * }</pre>
 *
 * @param <S> the plugin's sender type, such as Paper's {@code CommandSender} or a plugin's own player type
 */
public interface Messenger<S> {
	/**
	 * Sends a translated message, shown as its entry in the displays says, or in the chat when it has none.
	 *
	 * @param sender  the receiver
	 * @param message the message with its values
	 */
	void send(@NotNull S sender, @NotNull Message message);

	/**
	 * Sends a translated message to several senders.
	 *
	 * @param senders the receivers
	 * @param message the message with its values
	 * @see #send(Object, Message)
	 */
	void send(@NotNull Iterable<? extends S> senders, @NotNull Message message);

	/**
	 * Sends a display with its values, such as one read from a config. A text written in a config alone is sent as
	 * {@code Display.chat(content)}.
	 *
	 * @param sender  the receiver
	 * @param display the display
	 */
	void send(@NotNull S sender, @NotNull Display display);

	/**
	 * Sends a display to several senders.
	 *
	 * @param senders the receivers
	 * @param display the display
	 * @see #send(Object, Display)
	 */
	void send(@NotNull Iterable<? extends S> senders, @NotNull Display display);

	/**
	 * Writes a banner to the plugin's log, between empty lines, in the fallback language.
	 *
	 * @param banner the banner
	 */
	void print(@NotNull Banner banner);

	/**
	 * Shows the texts held for a sender, in the order they were sent, and stops holding them. Texts are held while
	 * the rule the messenger was built with says so, such as before a player logs in.
	 *
	 * @param sender the sender
	 */
	void release(@NotNull S sender);

	/**
	 * Drops the texts held for a sender, such as when they leave without logging in.
	 *
	 * @param sender the sender
	 */
	void discard(@NotNull S sender);

	/**
	 * Returns how the messenger sees a sender: its name, unique id, client language and whether it is the console.
	 * A library that works with the plugin's senders, such as a command framework keeping cooldowns per player, asks
	 * the messenger rather than mapping senders a second time.
	 *
	 * @param sender the sender
	 * @return its recipient
	 */
	@NotNull Recipient recipient(@NotNull S sender);

	/**
	 * Returns the display files the messenger takes translated messages' parts from.
	 *
	 * @return the display files
	 */
	@NotNull Displays displays();
}
