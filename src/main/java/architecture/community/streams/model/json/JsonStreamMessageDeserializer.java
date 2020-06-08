package architecture.community.streams.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import architecture.community.streams.StreamMessage;

public class JsonStreamMessageDeserializer extends JsonDeserializer<StreamMessage>{
 
	public StreamMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException { 
		return null;
	}

}
