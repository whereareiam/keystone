package me.whereareiam.keystone.common.render;

import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.fixture.TestRecipient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageChainTest {
	private final Recipient german = TestRecipient.player("Alice", Locale.GERMAN);
	private final Recipient console = TestRecipient.console();

	@Test
	void clientLanguageComesBeforeTheFallback() {
		LanguageChain chain = new LanguageChain(List.of(), Locale.ENGLISH);

		assertEquals(List.of(Locale.GERMAN, Locale.ENGLISH), chain.resolve(german));
	}

	@Test
	void resolversComeFirstInOrder() {
		LanguageResolver french = recipient -> Optional.of(Locale.FRENCH);
		LanguageResolver none = recipient -> Optional.empty();
		LanguageResolver italian = recipient -> Optional.of(Locale.ITALIAN);
		LanguageChain chain = new LanguageChain(List.of(french, none, italian), Locale.ENGLISH);

		assertEquals(List.of(Locale.FRENCH, Locale.ITALIAN, Locale.GERMAN, Locale.ENGLISH), chain.resolve(german));
	}

	@Test
	void aLanguageAppearsOnceAtItsFirstPlace() {
		LanguageChain chain = new LanguageChain(List.of(recipient -> Optional.of(Locale.ENGLISH)), Locale.ENGLISH);

		assertEquals(List.of(Locale.ENGLISH, Locale.GERMAN), chain.resolve(german));
	}

	@Test
	void theConsoleHasTheFallback() {
		assertEquals(List.of(Locale.ENGLISH), new LanguageChain(List.of(), Locale.ENGLISH).resolve(console));
	}
}
