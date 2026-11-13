package me.whereareiam.keystone.common.serializer.adapter;

import me.whereareiam.keystone.common.serializer.legacy.DefaultLegacyCodeConverter;
import me.whereareiam.keystone.serializer.SerializerAdapter;
import me.whereareiam.keystone.type.LegacyInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * MiniMessage adapter for serializing components.
 * With legacy input enabled, the legacy codes in its text are applied as {@link DefaultLegacyCodeConverter} reads
 * them, every code allowed.
 */
public final class MiniMessageAdapter implements SerializerAdapter {
	private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
	private static final MiniMessage LEGACY_AWARE_MINI_MESSAGE = MiniMessage.builder()
			.postProcessor(MiniMessageAdapter::applyLegacyFormatting)
			.build();
	private static final DefaultLegacyCodeConverter LEGACY_CODES = new DefaultLegacyCodeConverter();

	@Override
	@NotNull
	public String getId() {
		return "MINIMESSAGE";
	}

	@Override
	@NotNull
	public Component deserialize(@NotNull String input, boolean legacyInputEnabled) {
		if (!legacyInputEnabled) return MINI_MESSAGE.deserialize(input);

		return LEGACY_AWARE_MINI_MESSAGE.deserialize(input);
	}

	@NotNull
	private static Component applyLegacyFormatting(@NotNull Component component) {
		List<Component> rewrittenChildren = new ArrayList<>(component.children().size());
		for (Component child : component.children()) {
			rewrittenChildren.add(applyLegacyFormatting(child));
		}

		if (component instanceof TextComponent textComponent) {
			Component rewrittenText = rewriteLegacyText(textComponent.content());
			if (rewrittenText == null)
				return textComponent.children(rewrittenChildren);

			Component result = rewrittenText.applyFallbackStyle(textComponent.style());
			for (Component child : rewrittenChildren) {
				result = result.append(child);
			}
			return result;
		}

		return component.children(rewrittenChildren);
	}

	@Nullable
	private static Component rewriteLegacyText(@NotNull String input) {
		String miniMessage = LEGACY_CODES.convert(input, LegacyInput.PLAIN_TEXT, code -> true);
		if (miniMessage == null)
			return null;

		return MINI_MESSAGE.deserialize(miniMessage);
	}

	@Override
	@NotNull
	public String serialize(@NotNull Component component) {
		return MINI_MESSAGE.serialize(component);
	}
}
