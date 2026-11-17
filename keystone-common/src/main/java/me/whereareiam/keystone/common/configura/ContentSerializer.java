package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import me.whereareiam.semantica.Content;

import java.io.IOException;

/**
 * Writes a {@link Content} as its source.
 */
final class ContentSerializer extends StdScalarSerializer<Content> {
	ContentSerializer() {
		super(Content.class);
	}

	@Override
	public void serialize(Content content, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeString(content.getSource());
	}
}
