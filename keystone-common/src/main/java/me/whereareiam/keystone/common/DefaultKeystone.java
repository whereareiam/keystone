package me.whereareiam.keystone.common;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.whereareiam.keystone.Keystone;
import me.whereareiam.keystone.Markup;
import me.whereareiam.keystone.common.console.DefaultConsole;
import me.whereareiam.keystone.common.delivery.Delivery;
import me.whereareiam.keystone.common.delivery.Gatekeeper;
import me.whereareiam.keystone.common.display.DisplayFile;
import me.whereareiam.keystone.common.markup.DefaultMarkup;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.recipient.DefaultRecipient;
import me.whereareiam.keystone.common.recipient.DefaultRecipients;
import me.whereareiam.keystone.common.recipient.LanguageChain;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.recipient.Console;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.recipient.Recipients;
import me.whereareiam.keystone.type.Gate;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Keystone on one platform. It reads the display file when it is made.
 *
 * @param <S> the platform's command sender
 */
@Accessors(fluent = true)
public final class DefaultKeystone<S> implements Keystone<S> {
	@Getter
	private final Recipients<S> recipients;
	@Getter
	private final Console console;
	private final AdventureFormat format;
	private final DisplayFile displays;
	private final Gatekeeper gatekeeper = new Gatekeeper();
	private final Delivery delivery;

	/**
	 * Creates Keystone and reads the display file.
	 *
	 * @param platform the platform
	 * @param options  what the builder was told
	 * @throws me.whereareiam.keystone.exception.DisplayFileException when the display file cannot be read
	 */
	public DefaultKeystone(@NotNull Platform<S> platform, @NotNull KeystoneOptions options) {
		TextRenderer renderer = new TextRenderer(options.getSemantica(), options.getFormat(), options.getPolicy(), options.getPlaceholders());
		LanguageChain languages = new LanguageChain(options.getResolvers(), options.getFallback());

		this.format = options.getFormat();
		this.displays = new DisplayFile(options.getDisplay());
		this.delivery = new Delivery(renderer, gatekeeper, platform);
		this.console = new DefaultConsole(platform.console(), platform, renderer, languages, displays);
		this.recipients = new DefaultRecipients<>(platform, console, sender -> new DefaultRecipient(sender, languages, delivery, displays));

		displays.load();
	}

	@Override
	public void gate(@NotNull Function<? super Recipient, Gate> gate) {
		gatekeeper.gate(gate);
	}

	@Override
	public void release(@NotNull Recipient recipient) {
		recipient.uniqueId().ifPresent(uniqueId -> gatekeeper.release(uniqueId)
				.forEach(held -> delivery.show(recipient, held.display(), held.values())));
	}

	@Override
	public void discard(@NotNull Recipient recipient) {
		recipient.uniqueId().ifPresent(gatekeeper::discard);
	}

	@Override
	public @NotNull Markup markup(@NotNull TagPolicy policy) {
		return new DefaultMarkup(format, policy);
	}

	@Override
	public void reload() {
		displays.load();
	}
}
