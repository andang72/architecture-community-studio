package architecture.community.image.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import architecture.community.image.DefaultImage;
import architecture.community.image.Image;

public class JsonImageDeserializer extends JsonDeserializer<Image> {

	@Override
	public Image deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);
		
		if( node.hasNonNull("imageId")) { 
			DefaultImage image = new DefaultImage(node.get("objectType").intValue(), node.get("objectId").longValue(), node.get("imageId").longValue());
			return image;
		}
		return null;
	}

}
