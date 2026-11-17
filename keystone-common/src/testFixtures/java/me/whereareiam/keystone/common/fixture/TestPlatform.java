package me.whereareiam.keystone.common.fixture;

import me.whereareiam.keystone.common.Platform;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A platform that logs into a list and runs later tasks when told to.
 */
public final class TestPlatform implements Platform {
	private final TestRecipient console = TestRecipient.console();
	private final List<String> log = new ArrayList<>();
	private final List<Map.Entry<Duration, Runnable>> later = new ArrayList<>();

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
	public @NotNull TestRecipient console() {
		return console;
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
