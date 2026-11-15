package me.whereareiam.keystone.integration.papiproxybridge;

import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.recipient.Recipient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PapiProxyBridgePlaceholdersTest {
	private final TestSender alice = TestSender.player("Alice", Locale.ENGLISH);
	private final TestPlatform platform = new TestPlatform();
	private final Recipient player = TestKeystone.create(platform, null).recipients().of(alice);
	private final Recipient console = TestKeystone.create(platform, null).console();
	private final List<String> asked = new ArrayList<>();

	@Test
	void asksTheBackendForThePlayersPlaceholders() {
		PapiProxyBridgePlaceholders placeholders = new PapiProxyBridgePlaceholders((text, uniqueId) -> {
			asked.add(text + " " + uniqueId);
			return CompletableFuture.completedFuture("Hi Alice");
		});

		assertEquals("Hi Alice", placeholders.fill(player, "Hi %player_name%"));
		assertEquals(List.of("Hi %player_name% " + alice.id()), asked);
	}

	@Test
	void showsTheTextAsWrittenWhenTheAnswerIsLate() {
		PapiProxyBridgePlaceholders placeholders = new PapiProxyBridgePlaceholders((text, uniqueId) -> new CompletableFuture<>());

		assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () -> assertEquals("Hi %player_name%", placeholders.fill(player, "Hi %player_name%")));
	}

	@Test
	void showsTheTextAsWrittenWhenTheBackendFails() {
		PapiProxyBridgePlaceholders placeholders = new PapiProxyBridgePlaceholders((text, uniqueId) ->
				CompletableFuture.failedFuture(new IllegalStateException("no server")));

		assertEquals("Hi %player_name%", placeholders.fill(player, "Hi %player_name%"));
	}

	@Test
	void asksNothingForTheConsoleOrATextWithoutPlaceholders() {
		PapiProxyBridgePlaceholders placeholders = new PapiProxyBridgePlaceholders((text, uniqueId) -> {
			asked.add(text);
			return CompletableFuture.completedFuture("filled");
		});

		assertEquals("Hi %player_name%", placeholders.fill(console, "Hi %player_name%"));
		assertEquals("100% sure", placeholders.fill(player, "100% sure"));
		assertTrue(asked.isEmpty());
	}

	@Test
	void theIntegrationNeedsPapiProxyBridge() {
		assertEquals("PAPIProxyBridge", new PapiProxyBridgeIntegration().plugin());
	}
}
