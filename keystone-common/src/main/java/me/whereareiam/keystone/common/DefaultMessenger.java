package me.whereareiam.keystone.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.whereareiam.keystone.Messenger;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.console.ConsoleWriter;
import me.whereareiam.keystone.common.delivery.Delivery;
import me.whereareiam.keystone.common.delivery.Holding;
import me.whereareiam.keystone.common.display.DisplayLayers;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Banner;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Message;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * The messenger: turns the plugin's senders into recipients and hands each text to the delivery.
 *
 * @param <S> the plugin's sender type
 */
@Accessors(fluent = true)
public final class DefaultMessenger<S> implements Messenger<S> {
	private final Function<? super S, ? extends Recipient> recipients;
	private final Holding holding;
	private final ConsoleWriter console;
	private final Delivery delivery;

	@Getter
	private final DisplayLayers displays = new DisplayLayers();

	/**
	 * Creates the messenger.
	 *
	 * @param platform   the platform it runs on
	 * @param recipients returns the recipient of each of the plugin's senders
	 * @param options    the plugin's choices
	 */
	public DefaultMessenger(
			@NotNull Platform platform,
			@NotNull Function<? super S, ? extends Recipient> recipients,
			@NotNull MessengerOptions options
	) {
		TextRenderer renderer = new TextRenderer(options);

		this.recipients = Objects.requireNonNull(recipients, "recipients");
		this.holding = new Holding(options.getHold());
		this.console = new ConsoleWriter(renderer, platform);
		this.delivery = new Delivery(renderer, holding, console, platform);
	}

	@Override
	public void send(@NotNull S sender, @NotNull Message message) {
		delivery.send(List.of(recipient(sender)), displays.of(message));
	}

	@Override
	public void send(@NotNull Iterable<? extends S> senders, @NotNull Message message) {
		delivery.send(recipients(senders), displays.of(message));
	}

	@Override
	public void send(@NotNull S sender, @NotNull Display display) {
		delivery.send(List.of(recipient(sender)), display);
	}

	@Override
	public void send(@NotNull Iterable<? extends S> senders, @NotNull Display display) {
		delivery.send(recipients(senders), display);
	}

	@Override
	public void print(@NotNull Banner banner) {
		console.print(banner);
	}

	@Override
	public void release(@NotNull S sender) {
		Recipient recipient = recipient(sender);
		recipient.uniqueId().ifPresent(uniqueId -> holding.release(uniqueId)
				.forEach(display -> delivery.show(List.of(recipient), display)));
	}

	@Override
	public void discard(@NotNull S sender) {
		recipient(sender).uniqueId().ifPresent(holding::discard);
	}

	private Recipient recipient(S sender) {
		return Objects.requireNonNull(recipients.apply(Objects.requireNonNull(sender, "sender")), "recipient");
	}

	private List<Recipient> recipients(Iterable<? extends S> senders) {
		List<Recipient> list = new ArrayList<>();
		for (S sender : senders) list.add(recipient(sender));

		return list;
	}
}
