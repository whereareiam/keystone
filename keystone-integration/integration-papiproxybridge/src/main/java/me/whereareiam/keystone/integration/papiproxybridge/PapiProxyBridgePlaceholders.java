package me.whereareiam.keystone.integration.papiproxybridge;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.recipient.Recipient;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * Asks the receiving player's backend server for PlaceholderAPI's placeholders. The answer travels over the network,
 * so a text waits for it at most {@value #WAIT_MILLIS} milliseconds and is otherwise shown with its placeholders as
 * written; PAPIProxyBridge keeps answers for a while, so the next text finds them at once. The console and senders
 * that are no player keep their placeholders as written.
 */
@RequiredArgsConstructor
final class PapiProxyBridgePlaceholders implements Placeholders.Text {
	static final long WAIT_MILLIS = 100;

	private static final Pattern PLACEHOLDER = Pattern.compile("%[^%\\s]+%");

	private final BiFunction<String, UUID, CompletableFuture<String>> format;

	@Override
	public @NotNull String fill(@NotNull Recipient recipient, @NotNull String text) {
		Optional<UUID> uniqueId = recipient.uniqueId();
		if (uniqueId.isEmpty() || !PLACEHOLDER.matcher(text).find()) return text;

		return format.apply(text, uniqueId.get())
				.completeOnTimeout(text, WAIT_MILLIS, TimeUnit.MILLISECONDS)
				.exceptionally(failure -> text)
				.join();
	}
}
