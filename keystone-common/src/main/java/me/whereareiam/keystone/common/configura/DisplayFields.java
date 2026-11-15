package me.whereareiam.keystone.common.configura;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The names a display is written with.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class DisplayFields {
	static final String CHAT = "chat";
	static final String ACTION_BAR = "actionbar";
	static final String TITLE = "title";
	static final String BOSS_BAR = "bossbar";
	static final String SOUND = "sound";

	static final String SUBTITLE = "subtitle";
	static final String TIMES = "times";
	static final String FADE_IN = "in";
	static final String STAY = "stay";
	static final String FADE_OUT = "out";

	static final String TEXT = "text";
	static final String COLOR = "color";
	static final String OVERLAY = "overlay";
	static final String PROGRESS = "progress";
	static final String SECONDS = "seconds";

	static final String NAME = "name";
	static final String SOURCE = "source";
	static final String VOLUME = "volume";
	static final String PITCH = "pitch";

	static final long TICK_MILLIS = 50;
}
