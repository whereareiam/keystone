package me.whereareiam.keystone.platform;

import me.whereareiam.keystone.Keystone;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.fixture.TestFormat;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSemantica;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeystoneBuilderTest {
	@TempDir
	Path folder;

	private final TestSender alice = TestSender.player("Alice", Locale.ENGLISH);
	private final TestPlatform platform = new TestPlatform().join(alice);

	@Test
	void needsSemantica() {
		assertThrows(IllegalStateException.class, () -> new TestBuilder(Set.of()).build());
	}

	@Test
	void theFallbackIsSemanticasDefaultLocaleUnlessSet() {
		Keystone<TestSender> keystone = new TestBuilder(Set.of()).semantica(TestSemantica.create()).build();
		Keystone<TestSender> german = new TestBuilder(Set.of()).semantica(TestSemantica.create())
				.languages(languages -> languages.fallback(Locale.GERMAN))
				.build();

		assertEquals(List.of(Locale.ENGLISH), keystone.console().languages());
		assertEquals(List.of(Locale.GERMAN), german.console().languages());
	}

	@Test
	void integrationsOfPresentPluginsComeBeforeThePluginsOwn() {
		Keystone<TestSender> keystone = new TestBuilder(Set.of("first", "second"))
				.semantica(TestSemantica.create())
				.languages(languages -> languages.resolver(recipient -> Optional.of(Locale.ITALIAN)))
				.placeholders(placeholders -> placeholders.add((Placeholders.Text) (recipient, text) -> text + " own"))
				.build();

		keystone.recipients().of(alice).send(Content.of("text"));

		assertEquals(List.of("chat: text first second own"), alice.audience().shown());
		assertEquals(List.of(Locale.FRENCH, Locale.ITALIAN, Locale.ENGLISH), keystone.recipients().of(alice).languages());
	}

	@Test
	void anIntegrationIsLeftOutWhenItsPluginIsMissingOrItIsDisabled() {
		Keystone<TestSender> keystone = new TestBuilder(Set.of("FIRST", "second"))
				.semantica(TestSemantica.create())
				.placeholders(placeholders -> placeholders.disable(Second.class))
				.build();

		keystone.recipients().of(alice).send(Content.of("text"));

		assertEquals(List.of("chat: text first"), alice.audience().shown());
	}

	@Test
	void languagesCanDisableAnIntegrationToo() {
		Keystone<TestSender> keystone = new TestBuilder(Set.of("first"))
				.semantica(TestSemantica.create())
				.languages(languages -> languages.disable(First.class))
				.build();

		assertEquals(List.of(Locale.ENGLISH), keystone.recipients().of(alice).languages());
	}

	@Test
	void readsTheDisplayFileAndThePolicy() throws IOException {
		Files.writeString(folder.resolve("display.yml"), "greeting:\n  chat: false\n  actionbar: true\n");
		Keystone<TestSender> keystone = new TestBuilder(Set.of())
				.semantica(TestSemantica.create())
				.policy(TagPolicy.of())
				.display(folder.resolve("display.yml"))
				.build();

		keystone.recipients().of(alice).send(Message.of("greeting").with("player", "Alice"));

		assertEquals(List.of("actionbar: <gold>Hello, Alice!"), alice.audience().shown());
	}

	private final class TestBuilder extends KeystoneBuilder<TestSender, TestBuilder> {
		private final Set<String> plugins;

		private TestBuilder(Set<String> plugins) {
			this.plugins = plugins;
		}

		@Override
		protected @NotNull Platform<TestSender> platform() {
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
			installation.placeholders((Placeholders.Text) (recipient, text) -> text + " first");
			installation.resolver(recipient -> Optional.of(Locale.FRENCH));
		}
	}

	private static final class Second implements Integration {
		@Override
		public @NotNull String plugin() {
			return "second";
		}

		@Override
		public void install(@NotNull Installation installation) {
			installation.placeholders((Placeholders.Text) (recipient, text) -> text + " second");
		}
	}
}
