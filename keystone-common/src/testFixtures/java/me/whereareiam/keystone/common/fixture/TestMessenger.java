package me.whereareiam.keystone.common.fixture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.DefaultMessenger;
import me.whereareiam.keystone.common.MessengerOptions;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A messenger on the test platform, with {@link TestSemantica}'s texts and English as the fallback.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestMessenger {
	public static @NotNull DefaultMessenger<TestRecipient> create(@NotNull TestPlatform platform, @NotNull Placeholders... placeholders) {
		return create(platform, recipient -> false, placeholders);
	}

	public static @NotNull DefaultMessenger<TestRecipient> create(
			@NotNull TestPlatform platform,
			@NotNull Predicate<Recipient> hold,
			@NotNull Placeholders... placeholders
	) {
		return new DefaultMessenger<>(platform, Function.identity(), options(hold, placeholders));
	}

	public static @NotNull MessengerOptions options(@NotNull Placeholders... placeholders) {
		return options(recipient -> false, placeholders);
	}

	private static MessengerOptions options(Predicate<Recipient> hold, Placeholders... placeholders) {
		MessengerOptions.MessengerOptionsBuilder options = MessengerOptions.builder()
				.semantica(TestSemantica.create())
				.format(new TestFormat())
				.policy(TagPolicy.defaults())
				.fallback(Locale.ENGLISH)
				.hold(hold);
		for (Placeholders source : placeholders) options.placeholder(source);

		return options.build();
	}
}
