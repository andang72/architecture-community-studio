package tests;

import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.web.gateway.annotation.ScriptData;
import architecture.community.web.model.ItemList;

public class AnnotationDataViewTest {

	public AnnotationDataViewTest() {
	}

	@ScriptData
	public ItemList select (NativeWebRequest request) throws NotFoundException {
		//TODO
		
		return null;
	}
}
