package tests;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.community.model.Models;
import architecture.community.streams.StreamMessage;
import architecture.community.streams.StreamThread;
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsNotFoundException;
import architecture.community.streams.StreamsService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { 
		"classpath:application-community-context.xml",	
		"classpath:context/community-streams-context.xml",
		"classpath:context/community-utils-context.xml",
		"classpath:context/community-user-context.xml",
		"classpath:context/community-core-context.xml" })
public class StreamsTest {
	
	private static Logger log = LoggerFactory.getLogger(StreamsTest.class);
	
	@Autowired
	private StreamsService streamsService;
	
	@Test
	public void test() {
		//fail("Not yet implemented"); 
		List<Streams> list = streamsService.getAllStreams();
		for( Streams s : list ) { 
			log.debug("streams {}", s.getName());
			int cnt = streamsService.getStreamThreadCount(s);
			log.debug("size {}", cnt);
		}
		//if( list.size() == 0 )
		//	streamsService.createStreams("MY", "Personal Streams", "Streams for personal.");
		
	}

	
	public void testThread() throws StreamsNotFoundException { 
		Streams stream = streamsService.getStreamsByName("MY");
		
		StreamMessage message = streamsService.createMessage(Models.STREAMS.getObjectType(), stream.getStreamId());
		message.setSubject("test message subject");
		message.setBody("test message body");
		StreamThread thread = streamsService.createThread(message.getObjectType(), message.getObjectId(), message); 
		
		log.debug("hread {}", thread);
		streamsService.addThread(thread.getObjectType(), thread.getObjectId(), thread);
		
		log.debug("hread id {}", thread.getThreadId());
		
		streamsService.deleteThread(thread);
	}
	
	
	@Test
	public void testMessage() {
		
		
	}
}
