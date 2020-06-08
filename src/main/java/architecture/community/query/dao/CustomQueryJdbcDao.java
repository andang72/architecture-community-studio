package architecture.community.query.dao;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;

import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class CustomQueryJdbcDao extends ExtendedJdbcDaoSupport  {

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
	
	public CustomQueryJdbcDao() {
	}
	
	
	
	public long getNextId(String name){
		return sequencerFactory.getNextValue(name);
	}	
	
	public long getNextId(int objectType, String name){
		return sequencerFactory.getNextValue(objectType, name);
	}	
	
}
