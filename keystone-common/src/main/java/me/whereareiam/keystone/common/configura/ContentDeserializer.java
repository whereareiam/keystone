package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import me.whereareiam.semantica.Content;

import java.io.IOException;

/**
 * Reads a {@link Content} from a string; numbers and booleans are read as their text.
 */
final class ContentDeserializer extends StdScalarDeserializer<Content> {
	ContentDeserializer() {
		super(Content.class);
	}

	@Override
	public Content deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		if (!parser.currentToken().isScalarValue() || parser.currentToken() == JsonToken.VALUE_NULL)
			return (Content) context.handleUnexpectedToken(Content.class, parser);

		return Content.of(parser.getText());
	}
}
