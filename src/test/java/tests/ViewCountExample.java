package tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.viewcount.ViewCountService;

public class ViewCountExample {

	@Autowired(required=false)
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	
	public void addViewCount() {
		
		// 1. object type for view 
		int objectType = 0  ;
		
		// 2. object id for view 
		long objectId = 0 ;
		
		int viewCount = viewCountService.getViewCount(objectType, objectId);
		
		
	}
}
