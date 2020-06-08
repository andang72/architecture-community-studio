package tests;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.model.Models;
import architecture.ee.jdbc.sequencer.SequencerFactory;

public class SequencerExample { 

	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
	
	public long getNextAnnounceId(){		
		return sequencerFactory.getNextValue(Models.ANNOUNCE.getObjectType(), Models.ANNOUNCE.name());
	}
	
	public void test() {
		long nextId = sequencerFactory.getNextValue("SEQUENCER_NAME");
		// DO SOMETHING
	}
}
