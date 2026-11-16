package me.whereareiam.keystone.platform;

import me.whereareiam.keystone.Messenger;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.fixture.TestFormat;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestRecipient;
import me.whereareiam.keystone.common.fixture.TestSemantica;
import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessengerBuilderTest {
	private final TestRecipient alice = TestRecipient.player("Alice", Locale.ENGLISH);
	private final TestPlatform platform = new TestPlatform();

	@Test
	void needsSemantica() {
		assertThrows(IllegalStateException.class, () -> new TestBuilder(Set.of()).build());
	}

	@Test
	void theFallbackIsSemanticasDefaultLocaleUnlessSet() {
		TestRecipient console = TestRecipient.console();
		new TestBuilder(Set.of()).semantica(TestSemantica.create()).build()
				.send(console, Message.of("greeting").with("player", "x"));
		new TestBuilder(Set.of()).semantica(TestSemantica.create())
				.languages(languages -> languages.fallback(Locale.GERMAN))
				.build()
				.send(console, Message.of("greeting").with("player", "x"));

		assertTrue(platform.log().get(0).contains("Hello, x!"), platform.log().get(0));
		assertTrue(platform.log().get(1).contains("Hallo, x!"), platform.log().get(1));
	}

	@Test
	void integrationsOfPresentPluginsComeBeforeThePluginsOwn() {
		Messenger<TestRecipient> messenger = new TestBuilder(Set.of("first", "second"))
				.semantica(TestSemantica.create())
				.languages(languages -> languages.resolver(recipient -> Optional.of(Locale.ENGLISH)))
				.placeholders(placeholders -> placeholders.add((Placeholders.Text) (recipient, text) -> text.replace("%own%", "own")))
				.build();

		messenger.send(alice, Display.chat(Content.of("text %first% %second% %own%")));
		messenger.send(alice, Message.of("greeting").with("player", "Alice"));

		assertEquals(List.of("chat: text first second own", "chat: Hallo, Alice!"), alice.audience().shown());
	}

	@Test
	void anIntegrationIsLeftOutWhenItsPluginIsMissingOrItIsDisabled() {
		Messenger<TestRecipient> messenger = new TestBuilder(Set.of("FIRST", "second"))
				.semantica(TestSemantica.create())
				.placeholders(placeholders -> placeholders.disable(Second.class))
				.build();

		messenger.send(alice, Display.chat(Content.of("text %first% %second% %own%")));

		assertEquals(List.of("chat: text first %second% %own%"), alice.audience().shown());
	}

	@Test
	void languagesCanDisableAnIntegrationToo() {
		Messenger<TestRecipient> messenger = new TestBuilder(Set.of("first"))
				.semantica(TestSemantica.create())
				.languages(languages -> languages.disable(First.class))
				.build();

		messenger.send(alice, Message.of("greeting").with("player", "Alice"));

		assertEquals(List.of("chat: Hello, Alice!"), alice.audience().shown());
	}

	@Test
	void appliesThePolicyAndTheHoldRule() {
		Messenger<TestRecipient> messenger = new TestBuilder(Set.of())
				.semantica(TestSemantica.create())
				.policy(TagPolicy.of())
				.hold(recipient -> recipient.name().equals("Held"))
				.build();
		TestRecipient held = TestRecipient.player("Held", Locale.ENGLISH);

		messenger.send(List.of(alice, held), Message.of("greeting").with("player", "Alice"));

		assertEquals(List.of("chat: <gold>Hello, Alice!"), alice.audience().shown());
		assertTrue(held.audience().shown().isEmpty());
	}

	private final class TestBuilder extends MessengerBuilder<TestRecipient, TestBuilder> {
		private final Set<String> plugins;

		private TestBuilder(Set<String> plugins) {
			super(Function.identity());
			this.plugins = plugins;
		}

		@Override
		protected @NotNull Platform platform() {
			return platform;
		}

		@Override
		protected @NotNull AdventureFormat format() {
			return new TestFormat();
		}

		@Override
		protected @NotNull List<Integration> integrations() {
			return List.of(new First(), new Second());
		}

		@Override
		protected boolean present(@NotNull String plugin) {
			return plugins.stream().anyMatch(plugin::equalsIgnoreCase);
		}

		@Override
		protected @NotNull TestBuilder self() {
			return this;
		}
	}

	private static final class First implements Integration {
		@Override
		public @NotNull String plugin() {
			return "first";
		}

		@Override
		public void install(@NotNull Installation installation) {
			installation.placeholders((Placeholders.Text) (recipient, text) -> text.replace("%first%", "first"));
			installation.resolver(recipient -> Optional.of(Locale.GERMAN));
		}
	}

	private static final class Second implements Integration {
		@Override
		public @NotNull String plugin() {
			return "second";
		}

		@Override
		public void install(@NotNull Installation installation) {
			installation.placeholders((Placeholders.Text) (recipient, text) -> text.replace("%second%", "second"));
		}
	}
}
