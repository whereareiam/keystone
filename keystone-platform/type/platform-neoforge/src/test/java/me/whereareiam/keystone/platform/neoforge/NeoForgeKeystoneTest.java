package me.whereareiam.keystone.platform.neoforge;

import me.whereareiam.keystone.Recipient;
import net.kyori.adventure.platform.modcommon.AdventureCommandSourceStack;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NeoForgeKeystoneTest {
	private final MinecraftServer server = mock(MinecraftServer.class);
	private final MinecraftServerAudiences audiences = mock(MinecraftServerAudiences.class);
	private final Logger logger = mock(Logger.class);
	private final NeoForgePlatform platform = new NeoForgePlatform(server, audiences, logger);

	@Test
	void aPlayerHasTheirIdTheirClientsLanguageAndTheirAudience() {
		UUID id = UUID.randomUUID();
		ServerPlayer player = mock(ServerPlayer.class);
		when(player.getUUID()).thenReturn(id);
		when(player.clientInformation()).thenReturn(information("de_de"));
		CommandSourceStack source = source(player, "Alice");
		AdventureCommandSourceStack audience = mock(AdventureCommandSourceStack.class);
		when(audiences.audience(source)).thenReturn(audience);

		Recipient recipient = platform.recipient(source);

		assertEquals("Alice", recipient.name());
		assertEquals(Optional.of(id), recipient.uniqueId());
		assertEquals(Optional.of(Locale.GERMANY), recipient.clientLanguage());
		assertSame(audience, recipient.audience());
		assertFalse(recipient.isConsole());
	}

	@Test
	void aLanguageThatIsNoLocaleIsNone() {
		ServerPlayer player = mock(ServerPlayer.class);
		when(player.clientInformation()).thenReturn(information("??"));
		CommandSourceStack source = source(player, "Alice");

		assertTrue(platform.recipient(source).clientLanguage().isEmpty());
	}

	@Test
	void theServerItselfIsTheConsole() {
		when(server.createCommandSourceStack()).thenReturn(source(server, "Server"));

		Recipient console = platform.console();

		assertTrue(console.isConsole());
		assertEquals("Server", console.name());
		assertTrue(console.uniqueId().isEmpty());
		assertTrue(console.clientLanguage().isEmpty());
	}

	@Test
	void aSourceThatIsNoPlayerHasNoIdAndIsNoConsole() {
		Recipient recipient = platform.recipient(source(CommandSource.NULL, "@"));

		assertFalse(recipient.isConsole());
		assertTrue(recipient.uniqueId().isEmpty());
		assertTrue(recipient.clientLanguage().isEmpty());
	}

	@Test
	void logsToTheModsLogger() {
		platform.log("hello");

		verify(logger).info("hello");
	}

	@Test
	void runsALaterTaskOnTheServersThreadOnceItsDelayHasPassed() {
		Runnable task = () -> {
		};

		platform.later(Duration.ofMillis(200), task);

		verify(server, never()).execute(any());
		verify(server, timeout(2000)).execute(task);
	}

	@Test
	void neoForgeHasNoIntegrations() {
		NeoForgeKeystone.Builder<CommandSourceStack> builder = new NeoForgeKeystone.Builder<>(platform, platform::recipient);

		assertTrue(builder.integrations().isEmpty());
		assertSame(platform, builder.platform());
	}

	private static ClientInformation information(String language) {
		return new ClientInformation(language, 8, ChatVisiblity.FULL, true, 0, HumanoidArm.RIGHT, false, true);
	}

	private CommandSourceStack source(CommandSource sender, String name) {
		Entity entity = sender instanceof Entity self ? self : null;
		return new CommandSourceStack(sender, Vec3.ZERO, Vec2.ZERO, null, 4, name, Component.literal(name), server, entity);
	}
}
