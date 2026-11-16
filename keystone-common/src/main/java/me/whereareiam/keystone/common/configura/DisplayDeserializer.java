package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.whereareiam.keystone.model.Display;

import java.io.IOException;

/**
 * Reads a {@link Display} as its {@link DisplayDocument}.
 */
final class DisplayDeserializer extends StdDeserializer<Display> {
	DisplayDeserializer() {
		super(Display.class);
	}

	@Override
	public Display deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		DisplayDocument document = context.readValue(parser, DisplayDocument.class);

		try {
			return document.toDisplay();
		} catch (IllegalArgumentException exception) {
			return context.reportInputMismatch(Display.class, exception.getMessage());
		}
	}
}
