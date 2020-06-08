package architecture.community.page.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import architecture.community.page.BodyContent;
import architecture.community.page.BodyType;
import architecture.community.page.DefaultBodyContent;

public class BodyContentDeserializer extends JsonDeserializer<BodyContent> {

	// private final Log log = LogFactory.getLog(BodyContentDeserializer.class);

	public BodyContent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		ObjectCodec oc = jsonParser.getCodec();

		JsonNode node = oc.readTree(jsonParser);
		if (node == null) {
			return new DefaultBodyContent();
		} else {
			long bodyId = node.get("bodyId").asLong();
			long pageId = node.get("pageId").asLong();

			String bodyText = "";
			if (node.has("bodyText")) {
				bodyText = node.get("bodyText").textValue();
			}
			BodyType bodyType = BodyType.valueOf(node.get("bodyType").textValue().toUpperCase());
			return new DefaultBodyContent(bodyId, pageId, bodyType, bodyText);
		}
	}

}
