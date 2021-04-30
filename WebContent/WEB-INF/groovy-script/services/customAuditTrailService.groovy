package architecture.community.audit.groovy;

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDate

import javax.annotation.PostConstruct
import java.util.*;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import org.springframework.util.Assert

import architecture.community.audit.AbstractStringAuditTrailService
import architecture.community.audit.AuditActionContext
import architecture.community.query.CustomQueryService
import architecture.community.query.CustomTransactionCallback
import architecture.community.query.CustomTransactionCallbackWithoutResult
import architecture.community.query.Utils
import architecture.community.query.dao.CustomQueryJdbcDao
import architecture.community.util.CommunityConstants
import architecture.ee.service.ConfigService

public class CustomAuditTrailsService extends AbstractStringAuditTrailService{

	private static final int DEFAULT_COLUMN_LENGTH = 100;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<AuditActionContext> queue;
	
	@Autowired(required = false)
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Autowired
	@Qualifier("configService")
	private ConfigService configService;
	
	public boolean isEnabled() {
		return configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_AUDIT_ENABLED_PROP_NAME, false);
	}
	
	public boolean isDebug() {
		return configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_AUDIT_DEBUG_ENABLED_PROP_NAME, false);
	}
	
	@PostConstruct
	public void initialize() throws Exception {
		logger.debug("Initializing queue..");
		this.queue = Collections.synchronizedList(new java.util.ArrayList<AuditActionContext>());
	}
	
	public void record(final AuditActionContext auditActionContext) {
		if( isDebug()) {
			logger.debug("AUDIT - {}", getMultiLineAuditString(auditActionContext));
		}
		if(isEnabled()) {
			logger.debug("record audit...")
			queue.add(auditActionContext);
		}
	}
	
	public void updateAll () {
		
		List<AuditActionContext> localQueue = queue;
		queue = Collections.synchronizedList(new java.util.ArrayList<AuditActionContext>());
		
		logger.debug("update all audit records : {}", localQueue.size() );
		if (localQueue.size() > 0) {
			final List<AuditActionContext> list = new java.util.ArrayList<AuditActionContext>(localQueue);
			customQueryService.execute(new CustomTransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(CustomQueryJdbcDao dao) {
					dao.getExtendedJdbcTemplate().batchUpdate(
							"INSERT INTO AC_UI_AUDIT_TRAIL (AUD_USER, AUD_CLIENT_IP, AUD_SERVER_IP, AUD_RESOURCE, AUD_ACTION, APPLIC_CD, AUD_DATE) VALUES (?, ?, ?, ?, ?, ?, ?)",
							new BatchPreparedStatementSetter() {
								public void setValues(PreparedStatement ps, int i) throws SQLException {
									AuditActionContext auditActionContext = list.get(i);
									final String userId = auditActionContext.getPrincipal().length() <= DEFAULT_COLUMN_LENGTH ? auditActionContext.getPrincipal() : auditActionContext.getPrincipal().substring(0, DEFAULT_COLUMN_LENGTH);
									final String resource = auditActionContext.getResourceOperatedUpon().length() <= DEFAULT_COLUMN_LENGTH ? auditActionContext.getResourceOperatedUpon() : auditActionContext.getResourceOperatedUpon().substring(0, DEFAULT_COLUMN_LENGTH);
									final String action = auditActionContext.getActionPerformed().length() <= DEFAULT_COLUMN_LENGTH ? auditActionContext.getActionPerformed() : auditActionContext.getActionPerformed().substring(0, DEFAULT_COLUMN_LENGTH);
									ps.setString(1, userId);
									ps.setString(2, auditActionContext.getClientIpAddress());
									ps.setString(3, auditActionContext.getServerIpAddress());
									ps.setString(4, resource);
									ps.setString(5, action);
									ps.setString(6, auditActionContext.getApplicationCode());
									ps.setTimestamp(7, new Timestamp(auditActionContext.getWhenActionWasPerformed().getTime()));
								}
								public int getBatchSize() {
									return list.size();
								}
							}
					);
				}
			});
		}
	}
	
	public List<? extends AuditActionContext> getAuditRecordsSince(LocalDate sinceDate) {
		return customQueryService.execute(new CustomTransactionCallback<java.util.List<AuditActionContext>>() {
					public List<AuditActionContext> doInTransaction(CustomQueryJdbcDao dao) throws DataAccessException {
						return dao.getJdbcTemplate().query(

								"SELECT * FROM AC_UI_AUDIT_TRAIL WHERE AUD_DATE>=? ORDER BY AUD_DATE DESC",
								new RowMapper<AuditActionContext>() {
									public AuditActionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
										final String principal = rs.getString("AUD_USER");
										final String resource = rs.getString("AUD_RESOURCE");
										final String clientIp = rs.getString("AUD_CLIENT_IP");
										final String serverIp = rs.getString("AUD_SERVER_IP");
										final Date audDate = rs.getDate("AUD_DATE");
										final String appCode = rs.getString("APPLIC_CD");
										final String action = rs.getString("AUD_ACTION");
										final String userAgent = rs.getString("AUD_CLIENT_USERAGENT");
										
										Assert.notNull(principal, "AUD_USER cannot be null");
										Assert.notNull(resource, "AUD_RESOURCE cannot be null");
										Assert.notNull(clientIp, "AUD_CLIENT_IP cannot be null");
										Assert.notNull(serverIp, "AUD_SERVER_IP cannot be null");
										Assert.notNull(audDate, "AUD_DATE cannot be null");
										Assert.notNull(appCode, "APPLIC_CD cannot be null");
										Assert.notNull(action, "AUD_ACTION cannot be null");

										final AuditActionContext audit = new AuditActionContext( principal, resource, action, appCode, audDate, clientIp, serverIp, userAgent);

										return audit;
									}
								},
								Utils.newSqlParameterValue(Types.TIMESTAMP, sinceDate));
					}
				});
	}

	public void removeAll() {
	}
}