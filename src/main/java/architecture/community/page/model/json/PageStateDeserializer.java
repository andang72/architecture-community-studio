package architecture.community.page.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import architecture.community.page.PageState;

public class PageStateDeserializer extends JsonDeserializer<PageState> {

    public PageState deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
	    throws IOException, JsonProcessingException {
    		return PageState.valueOf(jsonParser.getText().toUpperCase());
    }

}
