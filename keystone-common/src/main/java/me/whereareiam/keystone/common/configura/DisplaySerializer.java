package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;

import java.io.IOException;
import java.time.Duration;

/**
 * Writes a {@link Display} as a map of parts, leaving out what equals a part's default.
 */
final class DisplaySerializer extends StdSerializer<Display> {
	private static final Title TITLE = Title.of(me.whereareiam.semantica.Content.of(""));
	private static final BossBar BOSS_BAR = BossBar.of(me.whereareiam.semantica.Content.of(""));
	private static final Sound SOUND = Sound.of(net.kyori.adventure.key.Key.key("ui.button.click"));

	DisplaySerializer() {
		super(Display.class);
	}

	@Override
	public void serialize(Display display, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeStartObject();
		for (Part part : display.getParts()) write(part, generator);
		generator.writeEndObject();
	}

	private static void write(Part part, JsonGenerator generator) throws IOException {
		switch (part) {
			case Chat chat -> generator.writeStringField(DisplayFields.CHAT, chat.getText().getSource());
			case ActionBar bar -> generator.writeStringField(DisplayFields.ACTION_BAR, bar.getText().getSource());
			case Title title -> writeTitle(title, generator);
			case BossBar bar -> writeBossBar(bar, generator);
			case Sound sound -> writeSound(sound, generator);
		}
	}

	private static void writeTitle(Title title, JsonGenerator generator) throws IOException {
		generator.writeObjectFieldStart(DisplayFields.TITLE);
		generator.writeStringField(DisplayFields.TITLE, title.getTitle().getSource());
		if (!title.getSubtitle().getSource().isEmpty())
			generator.writeStringField(DisplayFields.SUBTITLE, title.getSubtitle().getSource());

		if (!sameTimes(title, TITLE)) {
			generator.writeObjectFieldStart(DisplayFields.TIMES);
			generator.writeNumberField(DisplayFields.FADE_IN, ticks(title.getFadeIn()));
			generator.writeNumberField(DisplayFields.STAY, ticks(title.getStay()));
			generator.writeNumberField(DisplayFields.FADE_OUT, ticks(title.getFadeOut()));
			generator.writeEndObject();
		}

		generator.writeEndObject();
	}

	private static void writeBossBar(BossBar bar, JsonGenerator generator) throws IOException {
		generator.writeObjectFieldStart(DisplayFields.BOSS_BAR);
		generator.writeStringField(DisplayFields.TEXT, bar.getText().getSource());
		if (bar.getColor() != BOSS_BAR.getColor())
			generator.writeStringField(DisplayFields.COLOR, net.kyori.adventure.bossbar.BossBar.Color.NAMES.key(bar.getColor()));
		if (bar.getOverlay() != BOSS_BAR.getOverlay())
			generator.writeStringField(DisplayFields.OVERLAY, net.kyori.adventure.bossbar.BossBar.Overlay.NAMES.key(bar.getOverlay()));
		if (bar.getProgress() != BOSS_BAR.getProgress()) generator.writeNumberField(DisplayFields.PROGRESS, bar.getProgress());
		if (!bar.getDuration().equals(BOSS_BAR.getDuration()))
			generator.writeNumberField(DisplayFields.SECONDS, bar.getDuration().toMillis() / 1000D);

		generator.writeEndObject();
	}

	private static void writeSound(Sound sound, JsonGenerator generator) throws IOException {
		generator.writeObjectFieldStart(DisplayFields.SOUND);
		generator.writeStringField(DisplayFields.NAME, sound.getName().asMinimalString());
		if (sound.getSource() != SOUND.getSource())
			generator.writeStringField(DisplayFields.SOURCE, net.kyori.adventure.sound.Sound.Source.NAMES.key(sound.getSource()));
		if (sound.getVolume() != SOUND.getVolume()) generator.writeNumberField(DisplayFields.VOLUME, sound.getVolume());
		if (sound.getPitch() != SOUND.getPitch()) generator.writeNumberField(DisplayFields.PITCH, sound.getPitch());

		generator.writeEndObject();
	}

	private static boolean sameTimes(Title title, Title defaults) {
		return title.getFadeIn().equals(defaults.getFadeIn())
				&& title.getStay().equals(defaults.getStay())
				&& title.getFadeOut().equals(defaults.getFadeOut());
	}

	private static long ticks(Duration duration) {
		return duration.toMillis() / DisplayFields.TICK_MILLIS;
	}
}
