package me.whereareiam.keystone.common.markup;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Markup;
import me.whereareiam.keystone.common.markup.legacy.LegacyCodeConverter;
import me.whereareiam.keystone.common.markup.legacy.LegacyInput;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.type.LegacyCode;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Reads player text with a tag policy, after turning the legacy codes a filter accepts into tags.
 */
@RequiredArgsConstructor
public final class DefaultMarkup implements Markup {
	private static final LegacyCodeConverter CONVERTER = new LegacyCodeConverter();
	private static final Predicate<LegacyCode> NONE = code -> false;

	private final AdventureFormat format;
	private final TagPolicy policy;
	private final Predicate<? super LegacyCode> legacyCodes;

	/**
	 * Creates a markup that reads no legacy codes.
	 *
	 * @param format the platform's Adventure format
	 * @param policy the tags players may use
	 */
	public DefaultMarkup(@NotNull AdventureFormat format, @NotNull TagPolicy policy) {
		this(format, policy, NONE);
	}

	@Override
	public @NotNull Component read(@NotNull String typed) {
		String text = legacyCodes == NONE ? typed : CONVERTER.toMiniMessage(typed, LegacyInput.MINIMESSAGE, legacyCodes);
		return format.read(policy, text);
	}

	@Override
	public @NotNull Markup legacyCodes(@NotNull Predicate<? super LegacyCode> allowed) {
		return new DefaultMarkup(format, policy, Objects.requireNonNull(allowed, "allowed"));
	}
}
