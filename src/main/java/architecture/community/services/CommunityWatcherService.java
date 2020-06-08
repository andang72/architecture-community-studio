package architecture.community.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.services.watch.Watcher;
import architecture.community.user.UserManager;


public class CommunityWatcherService {

	private Logger log = LoggerFactory.getLogger(CommunityWatcherService.class);
	
	@Autowired
	@Qualifier("customQueryJdbcDao")
	private CustomQueryJdbcDao customQueryJdbcDao;
	
	@Autowired
	@Qualifier("mailService")
	private MailService mailService;
	
	
	@Inject
	@Qualifier("userManager")
	private UserManager userManager;	
	
	private com.google.common.cache.LoadingCache<Integer, List<Watcher>> watchersCache = null;
	
	public CommunityWatcherService() { 
		log.debug("creating cache ...");		
		watchersCache = CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess( 60 * 100, TimeUnit.MINUTES).build(		
				new CacheLoader<Integer, List<Watcher>>(){			
					@Override
					public List<Watcher> load(Integer key) throws Exception {
						return customQueryJdbcDao.getExtendedJdbcTemplate().query(
								customQueryJdbcDao.getBoundSql("SERVICE_DESK.SELECT_WATCHERS_BY_OBJECT_TYPE").getSql(), new RowMapper<Watcher>() {
	 								public Watcher mapRow(ResultSet rs, int rowNum) throws SQLException {
										Watcher watcher = new Watcher();
										watcher.setObjectType(rs.getInt("OBJECT_TYPE"));
										watcher.setObjectId(rs.getLong("OBJECT_ID"));
										watcher.setWatchType(rs.getInt("WATCH_TYPE"));
										watcher.setUserId(rs.getLong("USER_ID"));
										return watcher;
									}
								}, 
								new SqlParameterValue(Types.INTEGER, key) 
							);
					}
				}
			);		
	}
	
	/**
	@EventListener
	public void onIssueStateChangeEvent(IssueStateChangeEvent event) {
		
		log.debug("event do something ..: {}", event);
		
		Issue issue = ( Issue ) event.getSource();
		
		for( Watcher w : getWatchers(Models.PROJECT.getObjectType()) ) {
			if( w.getObjectType() == Models.PROJECT.getObjectType() && w.getObjectId()  == issue.getObjectId() ) {
				
				try {
					User user = userManager.getUser(w.getUserId());
					log.debug("{} : {} : {} send to {} ", event.getState().name(), issue.getRepoter(),  issue.getSummary(), user.getName() );
					try {
						mailService.send(
						"no-reply@podosw.com",
						"HELPDESK (발신전용메일입니다)",	
						user.getEmail(),
						(new StringBuilder( "[ISSUE-"+ issue.getIssueId() )).append("]").append(event.getState()  == IssueStateChangeEvent.State.CREATED ?  "새로운 이슈가 있습니다." : "이슈가 변경되었습니다.").toString(),
						String.format(
							"<a href='http://helpdesk.podosw.com/display/pages/issue.html?projectId=%s&issueId=%s' >%s</a> - Repoter : %s", 
							issue.getObjectId(), issue.getIssueId(), issue.getSummary() , issue.getRepoter()!= null ? issue.getRepoter().getName() : "")
						, 
						true);
					} catch (Exception e) { 
						e.printStackTrace();
					}
					
				} catch (UserNotFoundException e) {
				}
			}
		}
		
		
		
		
		
	}	
	*/
	

	public List<Watcher> getWatchers(int objectType){
		try {
			return watchersCache.get(objectType);
		} catch (ExecutionException e) {
			log.error(e.getMessage(), e);
			return Collections.EMPTY_LIST;
		}
	}
}
