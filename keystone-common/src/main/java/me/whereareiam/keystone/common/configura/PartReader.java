package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

/**
 * Reads one part of a display; each failure is an {@link IllegalArgumentException} that says what is wrong.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PartReader {
	private static final Set<String> TITLE_FIELDS = Set.of(DisplayFields.TITLE, DisplayFields.SUBTITLE, DisplayFields.TIMES);
	private static final Set<String> TIMES_FIELDS = Set.of(DisplayFields.FADE_IN, DisplayFields.STAY, DisplayFields.FADE_OUT);
	private static final Set<String> BOSS_BAR_FIELDS = Set.of(DisplayFields.TEXT, DisplayFields.COLOR, DisplayFields.OVERLAY,
			DisplayFields.PROGRESS, DisplayFields.SECONDS);
	private static final Set<String> SOUND_FIELDS = Set.of(DisplayFields.NAME, DisplayFields.SOURCE, DisplayFields.VOLUME,
			DisplayFields.PITCH);

	static @NotNull Part read(@NotNull String name, @NotNull JsonNode node) {
		return switch (name) {
			case DisplayFields.CHAT -> Chat.of(text(node));
			case DisplayFields.ACTION_BAR -> ActionBar.of(text(node));
			case DisplayFields.TITLE -> title(node);
			case DisplayFields.BOSS_BAR -> bossBar(node);
			case DisplayFields.SOUND -> sound(node);
			default -> throw new IllegalArgumentException("no such part; a display has chat, actionbar, title, bossbar and sound");
		};
	}

	private static Title title(JsonNode node) {
		if (node.isTextual()) return Title.of(text(node));

		fields(node, TITLE_FIELDS);
		Title.TitleBuilder title = Title.builder().title(text(required(node, DisplayFields.TITLE)));
		if (node.has(DisplayFields.SUBTITLE)) title.subtitle(text(node.get(DisplayFields.SUBTITLE)));
		if (!node.has(DisplayFields.TIMES)) return title.build();

		JsonNode times = node.get(DisplayFields.TIMES);
		fields(times, TIMES_FIELDS);
		if (times.has(DisplayFields.FADE_IN)) title.fadeIn(ticks(times.get(DisplayFields.FADE_IN)));
		if (times.has(DisplayFields.STAY)) title.stay(ticks(times.get(DisplayFields.STAY)));
		if (times.has(DisplayFields.FADE_OUT)) title.fadeOut(ticks(times.get(DisplayFields.FADE_OUT)));

		return title.build();
	}

	private static BossBar bossBar(JsonNode node) {
		if (node.isTextual()) return BossBar.of(text(node));

		fields(node, BOSS_BAR_FIELDS);
		BossBar.BossBarBuilder bar = BossBar.builder().text(text(required(node, DisplayFields.TEXT)));
		if (node.has(DisplayFields.COLOR))
			bar.color(named(node.get(DisplayFields.COLOR), net.kyori.adventure.bossbar.BossBar.Color.NAMES));
		if (node.has(DisplayFields.OVERLAY))
			bar.overlay(named(node.get(DisplayFields.OVERLAY), net.kyori.adventure.bossbar.BossBar.Overlay.NAMES));
		if (node.has(DisplayFields.PROGRESS)) bar.progress(number(node.get(DisplayFields.PROGRESS)));
		if (node.has(DisplayFields.SECONDS))
			bar.duration(Duration.ofMillis(Math.round(number(node.get(DisplayFields.SECONDS)) * 1000D)));

		return bar.build();
	}

	private static Sound sound(JsonNode node) {
		if (node.isTextual()) return Sound.of(key(node));

		fields(node, SOUND_FIELDS);
		Sound.SoundBuilder sound = Sound.builder().name(key(required(node, DisplayFields.NAME)));
		if (node.has(DisplayFields.SOURCE))
			sound.source(named(node.get(DisplayFields.SOURCE), net.kyori.adventure.sound.Sound.Source.NAMES));
		if (node.has(DisplayFields.VOLUME)) sound.volume(number(node.get(DisplayFields.VOLUME)));
		if (node.has(DisplayFields.PITCH)) sound.pitch(number(node.get(DisplayFields.PITCH)));

		return sound.build();
	}

	private static Content text(JsonNode node) {
		if (!node.isValueNode() || node.isNull()) throw new IllegalArgumentException("a text is one string");
		return Content.of(node.asText());
	}

	private static Key key(JsonNode node) {
		if (!node.isTextual()) throw new IllegalArgumentException("a sound is named by a string, such as block.note_block.pling");

		try {
			return Key.key(node.asText());
		} catch (RuntimeException exception) {
			throw new IllegalArgumentException("no valid sound name: " + node.asText(), exception);
		}
	}

	private static float number(JsonNode node) {
		if (!node.isNumber()) throw new IllegalArgumentException("not a number: " + node);
		return node.floatValue();
	}

	private static Duration ticks(JsonNode node) {
		if (!node.canConvertToInt() || node.intValue() < 0) throw new IllegalArgumentException("times are ticks, 0 or more: " + node);
		return Duration.ofMillis(node.intValue() * DisplayFields.TICK_MILLIS);
	}

	private static <E extends Enum<E>> E named(JsonNode node, Index<String, E> names) {
		E value = node.isTextual() ? names.value(node.asText().toLowerCase(java.util.Locale.ROOT)) : null;
		if (value == null) throw new IllegalArgumentException(node + " is none of " + names.keys());

		return value;
	}

	private static JsonNode required(JsonNode node, String field) {
		JsonNode value = node.get(field);
		if (value == null) throw new IllegalArgumentException("needs " + field);

		return value;
	}

	private static void fields(JsonNode node, Set<String> allowed) {
		if (!node.isObject()) throw new IllegalArgumentException("a map or a text is expected");

		Iterator<String> names = node.fieldNames();
		while (names.hasNext()) {
			String name = names.next();
			if (!allowed.contains(name)) throw new IllegalArgumentException("unknown field " + name + "; known are " + allowed);
		}
	}
}
