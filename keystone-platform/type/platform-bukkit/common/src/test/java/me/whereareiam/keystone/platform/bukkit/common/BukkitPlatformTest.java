package me.whereareiam.keystone.platform.bukkit.common;

import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.fixture.RecordingAudience;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BukkitPlatformTest {
	private final Plugin plugin = mock(Plugin.class);
	private final Server server = mock(Server.class);
	private final BukkitPlatform platform = new NamingPlatform(plugin);

	BukkitPlatformTest() {
		when(plugin.getServer()).thenReturn(server);
		when(server.getConsoleSender()).thenReturn(mock(ConsoleCommandSender.class));
	}

	@Test
	void theConsoleIsTheServersConsoleSender() {
		assertTrue(platform.console().isConsole());
	}

	@Test
	void logsToThePluginsLogger() {
		Logger logger = Logger.getAnonymousLogger();
		logger.setUseParentHandlers(false);
		List<String> lines = new ArrayList<>();
		logger.addHandler(new Handler() {
			@Override
			public void publish(LogRecord record) {
				lines.add(record.getLevel() + " " + record.getMessage());
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() {
			}
		});
		when(plugin.getLogger()).thenReturn(logger);

		platform.log("hello");

		assertEquals(List.of("INFO hello"), lines);
	}

	private static final class NamingPlatform extends BukkitPlatform {
		private NamingPlatform(Plugin plugin) {
			super(plugin);
		}

		@Override
		public @NotNull Recipient recipient(@NotNull CommandSender sender) {
			return new Recipient() {
				@Override
				public @NotNull String name() {
					return String.valueOf(sender.getName());
				}

				@Override
				public @NotNull Optional<UUID> uniqueId() {
					return Optional.empty();
				}

				@Override
				public @NotNull Optional<Locale> clientLanguage() {
					return Optional.empty();
				}

				@Override
				public net.kyori.adventure.audience.@NotNull Audience audience() {
					return new RecordingAudience();
				}

				@Override
				public boolean isConsole() {
					return sender instanceof ConsoleCommandSender;
				}
			};
		}

		@Override
		public void later(@NotNull Duration delay, @NotNull Runnable task) {
			task.run();
		}
	}
}
