package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A display as written: a string for the chat alone, or a map of its parts, shown as chat, action bar, title, boss
 * bar and sound.
 * <p>
 * In a display file's entry, a part may say {@code true} for the message's own text, a title or boss bar may leave
 * its text out for the same, and the chat shows the message's own text unless it says {@code false}.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"chat", "actionbar", "title", "bossbar", "sound"})
@Builder(access = AccessLevel.PRIVATE)
public final class DisplayDocument {
	private final @Nullable PartText chat;
	private final @Nullable PartText actionbar;
	private final @Nullable TitleDocument title;
	private final @Nullable BossBarDocument bossbar;
	private final @Nullable SoundDocument sound;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	static @NotNull DisplayDocument read(
			@JsonProperty("chat") @Nullable PartText chat,
			@JsonProperty("actionbar") @Nullable PartText actionbar,
			@JsonProperty("title") @Nullable TitleDocument title,
			@JsonProperty("bossbar") @Nullable BossBarDocument bossbar,
			@JsonProperty("sound") @Nullable SoundDocument sound
	) {
		return builder().chat(chat).actionbar(actionbar).title(title).bossbar(bossbar).sound(sound).build();
	}

	@JsonCreator
	static @NotNull DisplayDocument written(@NotNull String chat) {
		return builder().chat(PartText.written(chat)).build();
	}

	/**
	 * Returns how a display is written.
	 *
	 * @param display the display
	 * @return the document, leaving out what equals a part's default
	 */
	public static @NotNull DisplayDocument of(@NotNull Display display) {
		DisplayDocumentBuilder document = builder();
		for (Part part : display.getParts()) {
			switch (part) {
				case Chat chat -> document.chat(PartText.of(chat.getText()));
				case ActionBar bar -> document.actionbar(PartText.of(bar.getText()));
				case Title title -> document.title(TitleDocument.of(title));
				case BossBar bar -> document.bossbar(BossBarDocument.of(bar));
				case Sound sound -> document.sound(SoundDocument.of(sound));
			}
		}

		return document.build();
	}

	@JsonAnySetter
	void unknown(String name, JsonNode value) {
		throw new IllegalArgumentException("no such part " + name + "; a display has chat, actionbar, title, bossbar and sound");
	}

	/**
	 * Returns the display a config's field holds.
	 *
	 * @return the display
	 * @throws IllegalArgumentException when a part says {@code true} or leaves its text out
	 */
	public @NotNull Display toDisplay() {
		return Display.of(parts(null));
	}

	/**
	 * Returns the display of a display file's entry.
	 *
	 * @param key the message's key
	 * @return the display
	 */
	public @NotNull Display toEntry(@NotNull String key) {
		List<Part> parts = parts(key);
		if (chat == null) parts.addFirst(Chat.of(Content.of(Message.of(key))));

		return Display.of(parts);
	}

	private List<Part> parts(@Nullable String key) {
		List<Part> parts = new ArrayList<>(5);
		if (chat != null && chat.isShown()) parts.add(Chat.of(chat.content(key)));
		if (actionbar != null && actionbar.isShown()) parts.add(ActionBar.of(actionbar.content(key)));
		if (title != null) add(parts, title.toPart(key));
		if (bossbar != null) add(parts, bossbar.toPart(key));
		if (sound != null) parts.add(sound.toPart());

		return parts;
	}

	private static void add(List<Part> parts, @Nullable Part part) {
		if (part != null) parts.add(part);
	}
}
