package me.whereareiam.keystone.integration.miniplaceholders;

import me.whereareiam.keystone.common.integration.Installation;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MiniPlaceholdersTagsTest {

	@Test
	void asksForTheGlobalTagsForEachText() {
		AtomicInteger asked = new AtomicInteger();
		TagResolver online = Placeholder.unparsed("server_online", "12");
		MiniPlaceholdersTags tags = new MiniPlaceholdersTags(() -> {
			asked.incrementAndGet();
			return online;
		});

		assertSame(online, tags.resolver());
		tags.resolver();
		assertEquals(2, asked.get());
	}

	@Test
	void theIntegrationNeedsMiniPlaceholdersAndAddsTags() {
		MiniPlaceholdersIntegration integration = new MiniPlaceholdersIntegration();
		Installation installation = mock(Installation.class);

		integration.install(installation);

		assertEquals("MiniPlaceholders", integration.plugin());
		verify(installation).placeholders(any(MiniPlaceholdersTags.class));
	}
}
