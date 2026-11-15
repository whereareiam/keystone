package me.whereareiam.keystone.platform.format;

import me.whereareiam.keystone.common.fixture.TestSemantica;
import me.whereareiam.semantica.Message;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import me.whereareiam.semantica.message.ResolvedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdventureV5FormatTest {
	private final AdventureV5Format format = new AdventureV5Format();

	@Test
	void rendersLiteralPartsWithThePolicyAndPlaceholders() {
		ResolvedMessage message = TestSemantica.create().resolve(Message.of("greeting").with("player", "<red>%x%"), Locale.GERMAN);

		Component rendered = format.render(
				TagPolicy.defaults(),
				message,
				text -> text.replace("Hallo", "Hallo <online>"),
				List.of(Placeholder.unparsed("online", "%x%"))
		);

		assertEquals("Hallo %x%, <red>%x%!", plain(rendered));
		assertEquals(NamedTextColor.GOLD, rendered.color());
	}

	@Test
	void readsOnlyTheTagsThePolicyAllows() {
		assertEquals("<bold>x", plain(format.read(TagPolicy.of(TagPolicy.Group.COLOR), "<red><bold>x")));
		assertEquals(NamedTextColor.RED, format.read(TagPolicy.of(TagPolicy.Group.COLOR), "<red>x").color());
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}
}
