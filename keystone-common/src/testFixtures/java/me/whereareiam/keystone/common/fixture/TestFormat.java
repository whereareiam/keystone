package me.whereareiam.keystone.common.fixture;

import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import me.whereareiam.semantica.format.adventure.v5.AdventureRenderer;
import me.whereareiam.semantica.format.adventure.v5.AdventureTags;
import me.whereareiam.semantica.format.adventure.v5.Rendering;
import me.whereareiam.semantica.message.ResolvedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Semantica's format for Adventure 5, as Paper and Velocity use it.
 */
public final class TestFormat implements AdventureFormat {
	@Override
	public @NotNull Component render(
			@NotNull TagPolicy policy,
			@NotNull ResolvedMessage message,
			@NotNull UnaryOperator<String> text,
			@NotNull List<TagResolver> tags
	) {
		Rendering rendering = Rendering.builder().text(text).tags(tags.toArray(TagResolver[]::new)).build();
		return AdventureRenderer.of(policy).render(message, rendering);
	}

	@Override
	public @NotNull Component read(@NotNull TagPolicy policy, @NotNull String text) {
		return MiniMessage.builder().tags(AdventureTags.INSTANCE.resolver(policy)).build().deserialize(text);
	}
}
