package me.whereareiam.keystone.recipient;

import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.model.Values;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Several recipients, taken when the group was asked for. Sending to a group sends to each of them, each in their
 * own languages.
 * <pre>{@code
 * keystone.recipients().permission("socialismus.staff").send(messages.staffJoined(name));
 * }</pre>
 */
public interface RecipientGroup extends Iterable<Recipient> {
	/**
	 * Returns the recipients of the group.
	 *
	 * @return the recipients
	 */
	@NotNull List<Recipient> members();

	/**
	 * Sends a translated message to each recipient; see {@link Recipient#send(Message)}.
	 *
	 * @param message the message and its values
	 */
	default void send(@NotNull Message message) {
		forEach(recipient -> recipient.send(message));
	}

	/**
	 * Sends a text written in a config to each recipient's chat; see {@link Recipient#send(Content, Values)}.
	 *
	 * @param content the text
	 * @param values  the values its arguments and references see
	 */
	default void send(@NotNull Content content, @NotNull Values values) {
		forEach(recipient -> recipient.send(content, values));
	}

	/**
	 * Shows each part of a display to each recipient; see {@link Recipient#send(Display, Values)}.
	 *
	 * @param display the parts
	 * @param values  the values the texts of its parts see
	 */
	default void send(@NotNull Display display, @NotNull Values values) {
		forEach(recipient -> recipient.send(display, values));
	}
}
