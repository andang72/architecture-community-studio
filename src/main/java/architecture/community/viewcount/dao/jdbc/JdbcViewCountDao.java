/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.viewcount.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.viewcount.ViewCountInfo;
import architecture.community.viewcount.dao.ViewCountDao;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcViewCountDao extends ExtendedJdbcDaoSupport implements ViewCountDao {

	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	

	public JdbcViewCountDao() {
		
	}
 
	public int getViewCount(int entityType, long entityId) {
		int count = 0;
		try {
			count = getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_WEB.VIEW_COUNT_BY_ENTITY_TYPE_AND_ENTITY_ID").getSql(), 
					Integer.class, 
					new SqlParameterValue(Types.NUMERIC, entityType ),	
					new SqlParameterValue(Types.NUMERIC, entityId ));
		} catch (IncorrectResultSizeDataAccessException e) {
			insertInitialViewCount(entityType, entityId, 0);
		}		
		return count;
	}

 
	public void updateViewCounts(List<ViewCountInfo> views) {
		final List<ViewCountInfo> viewsToUser = views;
		getExtendedJdbcTemplate().batchUpdate(
				getBoundSql("COMMUNITY_WEB.UPDATE_VIEW_COUNT").getSql(),
				new BatchPreparedStatementSetter() { 
				    public void setValues(PreparedStatement ps, int i) throws SQLException {
				    	ViewCountInfo c = viewsToUser.get(i);
						ps.setInt(1, c.getCount());
						ps.setInt(2, c.getEntityType());
						ps.setLong(3, c.getEntityId());
				    }
				    public int getBatchSize() {
				    	return viewsToUser.size();
				    }
				}
		);
	}

 
	public void deleteViewCount(int entityType, long entityId) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_VIEW_COUNT").getSql(), 
				new SqlParameterValue(Types.NUMERIC, entityType ),	
				new SqlParameterValue(Types.NUMERIC, entityId )
		);
	}

 
	public void insertInitialViewCount(int entityType, long entityId, int count) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_VIEW_COUNT").getSql(), 
				new SqlParameterValue(Types.NUMERIC, entityType ),	
				new SqlParameterValue(Types.NUMERIC, entityId ),
				new SqlParameterValue(Types.NUMERIC, count )
		);
	}

}
