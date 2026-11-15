package me.whereareiam.keystone.common.fixture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.DefaultKeystone;
import me.whereareiam.keystone.common.KeystoneOptions;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Keystone on the test platform, with {@link TestSemantica}'s texts.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestKeystone {
	public static @NotNull DefaultKeystone<TestSender> create(@NotNull TestPlatform platform, @Nullable Path display, @NotNull Placeholders... placeholders) {
		KeystoneOptions.KeystoneOptionsBuilder options = KeystoneOptions.builder()
				.semantica(TestSemantica.create())
				.format(new TestFormat())
				.policy(TagPolicy.defaults())
				.fallback(Locale.ENGLISH)
				.display(display);
		for (Placeholders source : placeholders) options.placeholder(source);

		return new DefaultKeystone<>(platform, options.build());
	}
}
