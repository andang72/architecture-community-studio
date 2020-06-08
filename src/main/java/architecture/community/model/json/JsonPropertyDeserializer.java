package architecture.community.model.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonPropertyDeserializer extends JsonDeserializer<Map<String, String>> {
	
	public Map<String, String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		Iterator<JsonNode> iter = node.elements();
		Map<String, String> map = new HashMap<String, String>();

		while (iter.hasNext()) {
			JsonNode child = iter.next();
			String key = child.get("name").textValue();
			String value = child.get("value").textValue();
			map.put(key, value);
		}
		return map;
	}
}
