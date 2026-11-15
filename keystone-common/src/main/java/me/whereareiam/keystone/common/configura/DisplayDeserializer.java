package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Reads a {@link Display} from a map of parts, shown in the order they are written.
 */
final class DisplayDeserializer extends StdDeserializer<Display> {
	DisplayDeserializer() {
		super(Display.class);
	}

	@Override
	public Display deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		JsonNode node = context.readTree(parser);
		if (!node.isObject()) return context.reportInputMismatch(Display.class, "A display is a map of parts: chat, actionbar, title, bossbar and sound");

		List<Part> parts = new ArrayList<>();
		Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> field = fields.next();
			try {
				parts.add(PartReader.read(field.getKey(), field.getValue()));
			} catch (IllegalArgumentException exception) {
				return context.reportInputMismatch(Display.class, field.getKey() + ": " + exception.getMessage());
			}
		}

		return Display.of(parts);
	}
}
