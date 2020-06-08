package architecture.community.model.json;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import architecture.ee.exception.RuntimeError;

public class JsonDateDeserializer extends JsonDeserializer<Date> {
 
    private static final StdDateFormat formatter = new StdDateFormat();

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext)
	    throws IOException, JsonProcessingException {

	String date = jsonparser.getText();

	try {
	    return formatter.parse(date);
	} catch (ParseException e) {
	    throw new RuntimeError(e);
	}

    }

}
