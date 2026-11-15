package me.whereareiam.keystone.type;

/**
 * What happens to a text sent to a player: shown now, or held until the plugin releases the player.
 *
 * @see me.whereareiam.keystone.Keystone#gate(java.util.function.Function)
 */
public enum Gate {
	/**
	 * The text is shown now.
	 */
	SEND,

	/**
	 * The text is held, and shown in order when {@link me.whereareiam.keystone.Keystone#release} is called.
	 */
	HOLD
}
