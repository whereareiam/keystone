package me.whereareiam.keystone.integration.intercept;

import me.whereareiam.intercept.InterceptAPI;
import me.whereareiam.intercept.language.LanguageService;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.fixture.TestRecipient;
import me.whereareiam.keystone.common.integration.Installation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterceptLanguagesTest {
	private final TestRecipient alice = TestRecipient.player("Alice", Locale.ENGLISH);
	private final Recipient player = alice;
	private final Recipient console = TestRecipient.console();
	private final LanguageService service = mock(LanguageService.class);

	@AfterEach
	void stopIntercept() {
		InterceptAPI.unregister();
	}

	@Test
	void thePlayersChoiceComesFromIntercept() {
		when(service.chosen(alice.id())).thenReturn(Optional.of(Locale.GERMAN));

		assertEquals(Optional.of(Locale.GERMAN), new InterceptLanguages(() -> Optional.of(service)).resolve(player));
	}

	@Test
	void noChoiceForThoseWhoFollowTheirClientOrHaveNoId() {
		when(service.chosen(alice.id())).thenReturn(Optional.empty());
		InterceptLanguages languages = new InterceptLanguages(() -> Optional.of(service));

		assertTrue(languages.resolve(player).isEmpty());
		assertTrue(languages.resolve(console).isEmpty());
	}

	@Test
	void theIntegrationAsksInterceptsApiForEachText() {
		Installation installation = mock(Installation.class);
		InterceptIntegration integration = new InterceptIntegration();
		integration.install(installation);
		ArgumentCaptor<me.whereareiam.keystone.LanguageResolver> resolver = ArgumentCaptor.forClass(me.whereareiam.keystone.LanguageResolver.class);
		verify(installation).resolver(resolver.capture());

		assertTrue(resolver.getValue().resolve(player).isEmpty());

		InterceptAPI api = mock(InterceptAPI.class);
		when(api.languages()).thenReturn(service);
		when(service.chosen(alice.id())).thenReturn(Optional.of(Locale.FRENCH));
		InterceptAPI.register(api);

		assertEquals(Optional.of(Locale.FRENCH), resolver.getValue().resolve(player));
		assertEquals("Intercept", integration.plugin());
	}
}
