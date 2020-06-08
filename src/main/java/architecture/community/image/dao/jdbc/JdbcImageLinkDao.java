package architecture.community.image.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.dao.ImageLinkDao;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcImageLinkDao extends ExtendedJdbcDaoSupport implements ImageLinkDao {

	private final RowMapper<ImageLink> imageLinkMapper = new RowMapper<ImageLink>(){
		public ImageLink mapRow(ResultSet rs, int rowNum) throws SQLException {
			ImageLink link = new ImageLink(
				rs.getString("LINK_ID"),	
				rs.getLong("IMAGE_ID"),
				rs.getInt("PUBLIC_SHARED") == 1 
			);			
			return link;
		}		
	};
	
	public ImageLink getImageLinkByImageId(Long imageId) {				
		ImageLink link =  getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_IMAGE_LINK_BY_IMAGE_ID").getSql(), 
				imageLinkMapper, 
				new SqlParameterValue (Types.NUMERIC, imageId ));			
		return link;		
	}
	
	public ImageLink getImageLink(String linkId) {				
		ImageLink link =  getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_IMAGE_LINK_BY_LINK_ID").getSql(), 
				imageLinkMapper, 
				new SqlParameterValue (Types.VARCHAR, linkId ));			
		return link;		
	}
	
	public void saveImageLink(ImageLink link) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_IMAGE_LINK").getSql(), 	
					new SqlParameterValue (Types.VARCHAR, link.getLinkId() ), 
					new SqlParameterValue (Types.NUMERIC, link.getImageId() ), 
					new SqlParameterValue (Types.INTEGER, link.isPublicShared() ? 1 : 0  ) );
	}	
	
	public void removeImageLink(ImageLink link) {
		getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.DELETE_IMAGE_LINK_BY_LINK_ID").getSql(), 	
				new SqlParameterValue (Types.VARCHAR, link.getLinkId() ));		
	}
	
	public void removeImageLink(Image image) {
		getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.DELETE_IMAGE_LINK_BY_IMAGE_ID").getSql(), 	
				new SqlParameterValue (Types.NUMERIC, image.getImageId() ));		
		
	}

	@Override
	public void update(ImageLink link) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_IMAGE_LINK").getSql(),	
			new SqlParameterValue (Types.INTEGER, link.isPublicShared() ? 1 : 0  ), 
			new SqlParameterValue (Types.NUMERIC, link.getImageId() ));
	}
}
