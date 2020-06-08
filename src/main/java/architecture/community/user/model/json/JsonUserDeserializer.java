/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.user.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import architecture.community.user.User;
import architecture.community.user.User.Status;
import architecture.community.user.UserTemplate;

public class JsonUserDeserializer extends JsonDeserializer<User> {

	public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);
		UserTemplate user = new UserTemplate(node.get("userId").longValue());
		if (user.getUserId() > 0) {
			if (node.get("username") != null)
				user.setUsername(node.get("username").textValue());
			if (node.get("name") != null)
				user.setName(node.get("name").textValue());
			if (node.get("email") != null)
				user.setEmail(node.get("email").textValue());
			if (node.get("enabled") != null)
				user.setEnabled(node.get("enabled").asBoolean());
			if (node.get("emailVisible") != null)
				user.setEmailVisible(node.get("emailVisible").asBoolean());
			if (node.get("nameVisible") != null)
				user.setNameVisible(node.get("nameVisible").asBoolean());
			if( node.get("status") != null)
				user.setStatus( Status.valueOf( node.get("status").asText("NONE") ) ); 
		}
		return user;
	}

}
