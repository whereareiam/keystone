package me.whereareiam.keystone.common.fixture;

import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.platform.Sender;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A platform whose senders are {@link TestSender}s; it logs into a list and runs later tasks when told to.
 */
public final class TestPlatform implements Platform<TestSender> {
	private final TestSender console = TestSender.console();
	private final List<TestSender> players = new ArrayList<>();
	private final List<String> log = new ArrayList<>();
	private final List<Map.Entry<Duration, Runnable>> later = new ArrayList<>();

	public @NotNull TestPlatform join(@NotNull TestSender player) {
		players.add(player);
		return this;
	}

	public @NotNull TestSender consoleSender() {
		return console;
	}

	public @NotNull List<String> log() {
		return log;
	}

	public @NotNull List<Map.Entry<Duration, Runnable>> later() {
		return later;
	}

	public void runLater() {
		List<Map.Entry<Duration, Runnable>> tasks = List.copyOf(later);
		later.clear();
		tasks.forEach(task -> task.getValue().run());
	}

	@Override
	public @NotNull Sender sender(@NotNull TestSender sender) {
		return sender;
	}

	@Override
	public @NotNull Sender console() {
		return console;
	}

	@Override
	public @NotNull Collection<Sender> players() {
		return List.copyOf(players);
	}

	@Override
	public @NotNull Optional<Sender> player(@NotNull UUID uniqueId) {
		return players.stream().filter(player -> uniqueId.equals(player.id())).map(Sender.class::cast).findFirst();
	}

	@Override
	public void log(@NotNull String line) {
		log.add(line);
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		later.add(Map.entry(delay, task));
	}
}
