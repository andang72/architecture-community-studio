package architecture.community.attachment.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.DefaultAttachment;

public class JsonAttachmentDeserializer extends JsonDeserializer<Attachment> {

	@Override
	public Attachment deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);
		
		if( node.hasNonNull("attachmentId")) { 
			DefaultAttachment attachment = new DefaultAttachment();
			attachment.setAttachmentId( node.get("attachmentId").longValue());
			return attachment;
		}
		return null;
	}

}
