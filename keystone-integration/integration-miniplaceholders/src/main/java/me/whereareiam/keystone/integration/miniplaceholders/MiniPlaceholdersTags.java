package me.whereareiam.keystone.integration.miniplaceholders;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.recipient.Recipient;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * MiniPlaceholders' global tags, asked again for each text so expansions registered later apply.
 */
@RequiredArgsConstructor
final class MiniPlaceholdersTags implements Placeholders.Tags {
	private final Supplier<TagResolver> global;

	@Override
	public @NotNull TagResolver resolver(@NotNull Recipient recipient) {
		return global.get();
	}
}
