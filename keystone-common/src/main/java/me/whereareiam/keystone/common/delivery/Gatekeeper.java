package me.whereareiam.keystone.common.delivery;

import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.type.Gate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Holds the texts the gate keeps from a player until the plugin releases them. A player has at most
 * {@value #LIMIT} held texts; past that the oldest is dropped.
 */
public final class Gatekeeper {
	static final int LIMIT = 256;

	private final Map<UUID, Deque<Held>> held = new ConcurrentHashMap<>();
	private volatile Function<? super Recipient, Gate> gate = recipient -> Gate.SEND;

	/**
	 * Replaces the gate.
	 *
	 * @param gate the decision for a recipient
	 */
	public void gate(@NotNull Function<? super Recipient, Gate> gate) {
		this.gate = Objects.requireNonNull(gate, "gate");
	}

	/**
	 * Holds a text when the gate says so. Senders that are no player are never held.
	 *
	 * @param recipient the recipient
	 * @param display   the text's parts
	 * @param values    the values its texts see
	 * @return {@code true} when the text was held and must not be shown now
	 */
	public boolean hold(@NotNull Recipient recipient, @NotNull Display display, @NotNull Map<String, ?> values) {
		Optional<UUID> uniqueId = recipient.uniqueId();
		if (uniqueId.isEmpty() || gate.apply(recipient) != Gate.HOLD) return false;

		Deque<Held> queue = held.computeIfAbsent(uniqueId.get(), ignored -> new ArrayDeque<>());
		synchronized (queue) {
			if (queue.size() >= LIMIT) queue.removeFirst();
			queue.addLast(new Held(display, Map.copyOf(values)));
		}

		return true;
	}

	/**
	 * Takes what was held for a player.
	 *
	 * @param uniqueId the player
	 * @return the held texts, oldest first
	 */
	public @NotNull List<Held> release(@NotNull UUID uniqueId) {
		Deque<Held> queue = held.remove(uniqueId);
		if (queue == null) return List.of();

		synchronized (queue) {
			return List.copyOf(queue);
		}
	}

	/**
	 * Drops what was held for a player.
	 *
	 * @param uniqueId the player
	 */
	public void discard(@NotNull UUID uniqueId) {
		held.remove(uniqueId);
	}

	/**
	 * A held text.
	 *
	 * @param display its parts
	 * @param values  the values its texts see
	 */
	public record Held(@NotNull Display display, @NotNull Map<String, ?> values) {
	}
}
