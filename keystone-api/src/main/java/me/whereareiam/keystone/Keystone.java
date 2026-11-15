package me.whereareiam.keystone;

import me.whereareiam.keystone.recipient.Console;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.recipient.Recipients;
import me.whereareiam.keystone.type.Gate;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Delivers the texts of one plugin: it finds the recipients, resolves each text in a recipient's languages through
 * Semantica, fills in the placeholders of that recipient, renders it and shows it as the parts its display names.
 * <p>
 * A platform's builder makes it, such as {@code PaperKeystone.builder(plugin)}; Keystone keeps no settings file of
 * its own.
 * <pre>{@code
 * Keystone<CommandSender> keystone = PaperKeystone.builder(plugin)
 *         .semantica(semantica)
 *         .languages(languages -> languages.fallback(Locale.ENGLISH))
 *         .display(dataFolder.resolve("display.yml"))
 *         .build();
 *
 * keystone.recipients().of(sender).send(messages.cooldown(3.5));
 * keystone.console().send(messages.reloaded(version, millis));
 * }</pre>
 *
 * @param <S> the platform's command sender, such as Bukkit's {@code CommandSender}
 */
public interface Keystone<S> {
	/**
	 * Returns the recipients of this platform: one sender, every player or the players with a permission.
	 *
	 * @return the recipients
	 */
	@NotNull Recipients<S> recipients();

	/**
	 * Returns the server's console, which writes what it is sent to the plugin's log with ANSI colours.
	 *
	 * @return the console
	 */
	@NotNull Console console();

	/**
	 * Decides for each text sent to a player whether it is shown now or held until {@link #release(Recipient)},
	 * such as while the player has not logged in yet. The console is never held.
	 * <pre>{@code
	 * keystone.gate(recipient -> loggedIn(recipient) ? Gate.SEND : Gate.HOLD);
	 * }</pre>
	 *
	 * @param gate the decision for a recipient, asked for each text; it replaces the previous one
	 */
	void gate(@NotNull Function<? super Recipient, Gate> gate);

	/**
	 * Shows what was held for a recipient, in the order it was sent, in the languages the recipient has now.
	 *
	 * @param recipient the recipient
	 */
	void release(@NotNull Recipient recipient);

	/**
	 * Drops what was held for a recipient without showing it, such as when the player leaves.
	 *
	 * @param recipient the recipient
	 */
	void discard(@NotNull Recipient recipient);

	/**
	 * Returns the markup that reads what players write, with only the tags a policy allows.
	 * <pre>{@code
	 * Markup markup = keystone.markup(TagPolicy.of(TagPolicy.Group.COLOR, TagPolicy.Group.DECORATION))
	 *         .legacyCodes(code -> !(code instanceof LegacyCode.Reset));
	 * Component text = markup.read(typed);
	 * }</pre>
	 *
	 * @param policy the tags players may use
	 * @return the markup, reading no legacy codes
	 */
	@NotNull Markup markup(@NotNull TagPolicy policy);

	/**
	 * Reads the display file again, so changes to {@code display.yml} apply to the next text sent.
	 */
	void reload();
}
