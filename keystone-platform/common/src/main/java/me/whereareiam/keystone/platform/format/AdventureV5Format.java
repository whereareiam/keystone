package me.whereareiam.keystone.platform.format;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

/**
 * Semantica's format for Adventure 5, {@code format-adventure-v5}. It keeps one renderer and one reader per tag policy.
 */
public final class AdventureV5Format implements AdventureFormat {
	private final Map<TagPolicy, AdventureRenderer> renderers = new ConcurrentHashMap<>();
	private final Map<TagPolicy, MiniMessage> readers = new ConcurrentHashMap<>();

	@Override
	public @NotNull Component render(
			@NotNull TagPolicy policy,
			@NotNull ResolvedMessage message,
			@NotNull UnaryOperator<String> text,
			@NotNull List<TagResolver> tags
	) {
		Rendering rendering = Rendering.builder()
				.text(text)
				.tags(tags.toArray(TagResolver[]::new))
				.build();

		return renderers.computeIfAbsent(policy, AdventureRenderer::of).render(message, rendering);
	}

	@Override
	public @NotNull Component read(@NotNull TagPolicy policy, @NotNull String text) {
		return readers.computeIfAbsent(policy, AdventureV5Format::reader).deserialize(text);
	}

	private static MiniMessage reader(TagPolicy policy) {
		return MiniMessage.builder()
				.tags(AdventureTags.INSTANCE.resolver(policy))
				.build();
	}
}
