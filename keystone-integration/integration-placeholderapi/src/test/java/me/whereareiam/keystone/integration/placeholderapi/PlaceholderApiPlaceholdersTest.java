package me.whereareiam.keystone.integration.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.recipient.Recipient;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PlaceholderApiPlaceholdersTest {
	private final TestSender alice = TestSender.player("Alice", Locale.ENGLISH);
	private final TestPlatform platform = new TestPlatform();
	private final Recipient player = TestKeystone.create(platform, null).recipients().of(alice);
	private final Recipient console = TestKeystone.create(platform, null).console();
	private final PlaceholderApiPlaceholders placeholders = new PlaceholderApiPlaceholders();

	@Test
	void fillsThePlaceholdersOfTheReceivingPlayer() {
		OfflinePlayer offline = mock(OfflinePlayer.class);
		try (MockedStatic<PlaceholderAPI> api = mockStatic(PlaceholderAPI.class); MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
			api.when(() -> PlaceholderAPI.containsPlaceholders(anyString())).thenCallRealMethod();
			bukkit.when(() -> Bukkit.getOfflinePlayer(alice.id())).thenReturn(offline);
			api.when(() -> PlaceholderAPI.setPlaceholders(eq(offline), anyString())).thenReturn("Hi Alice");

			assertEquals("Hi Alice", placeholders.fill(player, "Hi %player_name%"));
		}
	}

	@Test
	void fillsPlaceholdersWithoutPlayerForTheConsole() {
		try (MockedStatic<PlaceholderAPI> api = mockStatic(PlaceholderAPI.class)) {
			api.when(() -> PlaceholderAPI.containsPlaceholders(anyString())).thenCallRealMethod();
			api.when(() -> PlaceholderAPI.setPlaceholders(isNull(OfflinePlayer.class), anyString())).thenReturn("20 TPS");

			assertEquals("20 TPS", placeholders.fill(console, "%server_tps% TPS"));
		}
	}

	@Test
	void leavesATextWithoutPlaceholdersAlone() {
		try (MockedStatic<PlaceholderAPI> api = mockStatic(PlaceholderAPI.class)) {
			api.when(() -> PlaceholderAPI.containsPlaceholders(anyString())).thenCallRealMethod();

			assertEquals("100% sure", placeholders.fill(player, "100% sure"));
			api.verify(() -> PlaceholderAPI.setPlaceholders(any(OfflinePlayer.class), anyString()), never());
		}
	}

	@Test
	void theIntegrationNeedsPlaceholderApiAndAddsTextPlaceholders() {
		PlaceholderApiIntegration integration = new PlaceholderApiIntegration();
		Installation installation = mock(Installation.class);

		integration.install(installation);

		assertEquals("PlaceholderAPI", integration.plugin());
		verify(installation).placeholders(any(PlaceholderApiPlaceholders.class));
		assertInstanceOf(Placeholders.Text.class, placeholders);
	}
}
