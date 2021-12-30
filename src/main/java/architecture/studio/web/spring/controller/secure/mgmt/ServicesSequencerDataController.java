package architecture.community.web.spring.controller.data.secure.mgmt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.query.CustomQueryService;
import architecture.community.query.ParameterValue;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@Controller("community-mgmt-services-sequencers-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/sequencers")
public class ServicesSequencerDataController {

	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("repository")
	private Repository repository;	
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/list.json", method = { RequestMethod.POST })
	@ResponseBody
	public ItemList list (NativeWebRequest request) throws NotFoundException {   
		List<Sequencer> list = getSequencers();
		return new ItemList(list, list.size() );
	}

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/0/next.json", method = { RequestMethod.POST })
	@ResponseBody
	public Sequencer next ( 
			@RequestBody Sequencer sequencerToUse, 
			NativeWebRequest request) throws NotFoundException {   
		
		List<Sequencer> list = getSequencers();
		boolean isUpdate = isExists(sequencerToUse);
		
		sequencerToUse.value = customQueryService.getCustomQueryJdbcDao().getNextId( sequencerToUse.getSequencerId(), sequencerToUse.getName());
		return sequencerToUse;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/0/create.json", method = { RequestMethod.POST })
	@ResponseBody
	public Result create ( 
			@RequestBody Sequencer sequencerToUse, 
			NativeWebRequest request) throws NotFoundException {   
		
		List<Sequencer> list = getSequencers();
		boolean isUpdate = isExists(sequencerToUse);
		
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		dataSourceRequest.setStatement("FRAMEWORK_EE.CREATE_SEQUENCER_WITH_DISPLAYNAME");
		dataSourceRequest.getParameters().add(new ParameterValue (0, "VALUE", java.sql.Types.INTEGER, sequencerToUse.getValue()));
		dataSourceRequest.getParameters().add(new ParameterValue (1, "NAME", java.sql.Types.VARCHAR, sequencerToUse.getName()));
		dataSourceRequest.getParameters().add(new ParameterValue (2, "SEQUENCER_ID", java.sql.Types.INTEGER, sequencerToUse.getSequencerId()));
		dataSourceRequest.getParameters().add(new ParameterValue (3, "DISPLAY_NAME", java.sql.Types.VARCHAR, sequencerToUse.getDisplayName()));
		customQueryService.update(dataSourceRequest); 
		return Result.newResult();
	} 
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/0/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
	public Result saveOrUpdate ( 
			@RequestBody Sequencer sequencerToUse, 
			NativeWebRequest request) throws NotFoundException {   
		
		List<Sequencer> list = getSequencers();
		boolean isExist = isExists(sequencerToUse);
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		if( isExist ) { 
			dataSourceRequest.setStatement("FRAMEWORK_EE.UPDATE_SEQUENCER_WITH_DISPLAYNAME");
			dataSourceRequest.getParameters().add(new ParameterValue (0, "DISPLAY_NAME", java.sql.Types.VARCHAR, sequencerToUse.getDisplayName()));
			dataSourceRequest.getParameters().add(new ParameterValue (1, "VALUE", java.sql.Types.INTEGER, sequencerToUse.getValue()));
			dataSourceRequest.getParameters().add(new ParameterValue (2, "SEQUENCER_ID", java.sql.Types.INTEGER, sequencerToUse.getSequencerId()));  
		}else { 
			dataSourceRequest.setStatement("FRAMEWORK_EE.CREATE_SEQUENCER_WITH_DISPLAYNAME");
			dataSourceRequest.getParameters().add(new ParameterValue (0, "VALUE", java.sql.Types.INTEGER, sequencerToUse.getValue()));
			dataSourceRequest.getParameters().add(new ParameterValue (1, "NAME", java.sql.Types.VARCHAR, sequencerToUse.getName()));
			dataSourceRequest.getParameters().add(new ParameterValue (2, "SEQUENCER_ID", java.sql.Types.INTEGER, sequencerToUse.getSequencerId()));
			dataSourceRequest.getParameters().add(new ParameterValue (3, "DISPLAY_NAME", java.sql.Types.VARCHAR, sequencerToUse.getDisplayName()));
			
		}
		customQueryService.update(dataSourceRequest); 
		
		return Result.newResult();
	} 
	
	private boolean isExists(Sequencer seq) {
		List<Sequencer> list = getSequencers();
		boolean isUpdate = false;
		for( Sequencer sequencerToUse : list ) {
			if( seq.getSequencerId() ==  sequencerToUse.sequencerId) {
				isUpdate = true ;
				break;
			}
		} 
		return isUpdate;
	}
	
	
	private List<Sequencer> getSequencers (){ 
		DataSourceRequest dataSourceRequest = new DataSourceRequest();
		dataSourceRequest.setStatement("FRAMEWORK_EE.SELECT_ALL_SEQUENCER");
		List<Sequencer> list = customQueryService.list(dataSourceRequest, new RowMapper<Sequencer> (){  
			public Sequencer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Sequencer(rs.getLong("VALUE"), rs.getString("NAME"), rs.getInt("SEQUENCER_ID"), rs.getString("DISPLAY_NAME"));
			}
		});  
		return list;
	}
	
	
	public static class Sequencer implements java.io.Serializable{
		
		long value ;
		String name;
		int sequencerId;
		String displayName; 
		
		public Sequencer(long value, String name, int sequencerId, String displayName) { 
			this.value = value;
			this.name = name;
			this.sequencerId = sequencerId;
			this.displayName = displayName;
		} 

		public Sequencer() {
			super(); 
		}
		
		
		public long getValue() {
			return value;
		}
		public void setValue(long value) {
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getSequencerId() {
			return sequencerId;
		}
		public void setSequencerId(int sequencerId) {
			this.sequencerId = sequencerId;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		} 
	}
	
	
}
