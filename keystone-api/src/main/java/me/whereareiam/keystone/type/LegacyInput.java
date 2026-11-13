package me.whereareiam.keystone.type;

/**
 * What the text around the legacy codes of a {@link me.whereareiam.keystone.serializer.LegacyCodeConverter}
 * input is, which decides whether it is escaped.
 */
public enum LegacyInput {
	/**
	 * Plain text: everything that is not a converted code is escaped, so {@code <player>} stays text and the result
	 * holds no tags but those of the codes.
	 */
	PLAIN_TEXT,

	/**
	 * MiniMessage text: everything that is not a converted code is kept as it is, so its tags still apply when the
	 * result is parsed, such as what a player wrote for a reader that allows some tags.
	 */
	MINIMESSAGE
}
