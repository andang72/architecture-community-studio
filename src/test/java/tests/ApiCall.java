package tests;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.model.Models;
import architecture.community.streams.StreamThread;
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsNotFoundException;
import architecture.community.streams.StreamsService;
import architecture.community.web.model.ItemList;

public class ApiCall {

	private Logger log = LoggerFactory.getLogger(ApiCall.class);
	
	public ApiCall() {
	}

	@Autowired(required = false) 
	@Qualifier("streamsService")
	private StreamsService streamsService;
	
	@RequestMapping(value = "/{streamsId:[\\p{Digit}]+}/threads/list.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public ItemList listThread (@PathVariable Long streamsId, 
			@RequestParam(value = "skip", defaultValue = "0", required = false) int skip,
			@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "pageSize", defaultValue = "0", required = false) int pageSize, 
			NativeWebRequest request) throws StreamsNotFoundException {	
		
		log.debug(" skip: {}, page: {}, pageSize: {}", skip, page, pageSize ); 
		Streams streams = streamsService.getStreamsById(streamsId); 
		List<StreamThread> list; 
		int totalSize = streamsService.getStreamThreadCount(Models.STREAMS.getObjectType(), streams.getStreamId()); 
		if( pageSize == 0 && page == 0){
			list = streamsService.getStreamThreads(Models.STREAMS.getObjectType(), streams.getStreamId() );
		}else{
			list = streamsService.getStreamThreads(Models.STREAMS.getObjectType(),  streams.getStreamId(), skip, pageSize);
		} 
		return new ItemList(list, totalSize);
	}
	
}
