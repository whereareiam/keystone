package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.whereareiam.keystone.model.Display;

import java.io.IOException;

/**
 * Writes a {@link Display} as its {@link DisplayDocument}.
 */
final class DisplaySerializer extends StdSerializer<Display> {
	DisplaySerializer() {
		super(Display.class);
	}

	@Override
	public void serialize(Display display, JsonGenerator generator, SerializerProvider provider) throws IOException {
		provider.defaultSerializeValue(DisplayDocument.of(display), generator);
	}
}
