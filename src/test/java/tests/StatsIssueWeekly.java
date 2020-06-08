package tests;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;

import architecture.community.query.CustomTransactionCallback;
import architecture.community.query.CustomTransactionCallbackWithoutResult;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.web.spring.view.script.AbstractDataView;

/**
* StatsIssueWeekly
*
* script type : data
* created : 2019. 10. 15 오후 4:21:45
*/
public class StatsIssueWeekly extends AbstractDataView 
{
	@Autowired(required=false)
	@Qualifier("customQueryService")
	private architecture.community.query.CustomQueryService customQueryService;  
	
	@Autowired(required=false)
	@Qualifier("customQueryJdbcDao")
	private architecture.community.query.dao.CustomQueryJdbcDao customQueryJdbcDao;  
	
	public Object handle(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug( "StatsIssueWeekly" );
	
		// Transaction code with return.
		customQueryService.execute(new CustomTransactionCallback<List>() { 
			public List doInTransaction(CustomQueryJdbcDao dao) throws DataAccessException {
				return dao.getJdbcTemplate().queryForList("select * from ac_ui_sequencer");
			}  
		});
		
		// Transaction code with no return.
		customQueryService.execute(new CustomTransactionCallbackWithoutResult() { 
			protected void doInTransactionWithoutResult(CustomQueryJdbcDao dao) {
				
			}	
		});
		
		return "welcome to island.";
	}
}
