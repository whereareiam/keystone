package me.whereareiam.keystone.common.delivery;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.model.Display;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Holds the texts of players a rule says to hold, such as before they log in, until they are released or
 * discarded. At most {@value #LIMIT} texts are held per player; past that the oldest is dropped. Recipients without
 * a unique id, such as the console, are never held.
 */
@RequiredArgsConstructor
public final class Holding {
	static final int LIMIT = 256;

	private final Map<UUID, Deque<Display>> held = new ConcurrentHashMap<>();
	private final Predicate<Recipient> rule;

	/**
	 * Holds a display for a recipient when the rule says so.
	 *
	 * @param recipient the recipient
	 * @param display   the display with its values
	 * @return whether it is held, and so not shown now
	 */
	public boolean hold(@NotNull Recipient recipient, @NotNull Display display) {
		Optional<UUID> uniqueId = recipient.uniqueId();
		if (uniqueId.isEmpty() || !rule.test(recipient)) return false;

		Deque<Display> queue = held.computeIfAbsent(uniqueId.get(), ignored -> new ArrayDeque<>());
		synchronized (queue) {
			if (queue.size() >= LIMIT) queue.removeFirst();
			queue.addLast(display);
		}

		return true;
	}

	/**
	 * Stops holding a player's texts.
	 *
	 * @param uniqueId the player's unique id
	 * @return the texts held, oldest first
	 */
	public @NotNull List<Display> release(@NotNull UUID uniqueId) {
		Deque<Display> queue = held.remove(uniqueId);
		if (queue == null) return List.of();

		synchronized (queue) {
			return List.copyOf(queue);
		}
	}

	/**
	 * Drops a player's held texts.
	 *
	 * @param uniqueId the player's unique id
	 */
	public void discard(@NotNull UUID uniqueId) {
		held.remove(uniqueId);
	}
}
