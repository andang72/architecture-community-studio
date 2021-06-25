package architecture.community.album.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.album.Album;
import architecture.community.album.AlbumContents;
import architecture.community.album.AlbumImage;
import architecture.community.album.AlbumNotFoundException;
import architecture.community.album.DefaultAlbum;
import architecture.community.image.Image;
import architecture.community.model.Models;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcAlbumDao extends ExtendedJdbcDaoSupport implements AlbumDao {

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;

	private String albumPropertyTableName = "AC_UI_ALBUM_PROPERTY";
	private String albumPropertyPrimaryColumnName = "ALBUM_ID";

	private Logger logger = LoggerFactory.getLogger(getClass().getName());
	

	private final RowMapper<Album> albumMapper = new RowMapper<Album>() {
		public Album mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultAlbum album = new DefaultAlbum();
			album.setAlbumId(rs.getLong("ALBUM_ID"));
			album.setName(rs.getString("NAME"));
			album.setDescription(rs.getString("DESCRIPTION"));
			album.setUser(new UserTemplate(rs.getLong("USER_ID")));
			album.setCreationDate(rs.getTimestamp("CREATION_DATE"));
			album.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));
			return album;
		}
	};
	

	public void deleteAlbumProperties(long albumId) {
		propertyDao.deleteProperties(albumPropertyTableName, albumPropertyPrimaryColumnName, albumId);
	}

	public void setAlbumProperties(long albumId, Map<String, String> props) {
		propertyDao.updateProperties(albumPropertyTableName, albumPropertyPrimaryColumnName, albumId, props);
	}
	
	public long getNextAlbumId() {
		return sequencerFactory.getNextValue(Models.ALBUM.getObjectType(), Models.ALBUM.name());
	}
	

	@Override
	public Album getById(long albumId) throws AlbumNotFoundException {
		try {
			return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_WEB.SELECT_ALBUM_BY_ID").getSql(),
					albumMapper, new SqlParameterValue(Types.NUMERIC, albumId));
		} catch (DataAccessException e) {
			throw new AlbumNotFoundException(e);
		}
	}

	public Album create(Album album) {
		Album toUse = album;
		if (toUse.getAlbumId() < 1L) {
			long albumId = getNextAlbumId();
			if (album instanceof DefaultAlbum) {
				DefaultAlbum impl = (DefaultAlbum) toUse;
				impl.setAlbumId(albumId);
			}
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_ALBUM").getSql(),
					new SqlParameterValue(Types.NUMERIC, toUse.getAlbumId()),
					new SqlParameterValue(Types.VARCHAR, toUse.getName()),
					new SqlParameterValue(Types.VARCHAR, toUse.getDescription()),
					new SqlParameterValue(Types.NUMERIC, toUse.getUser().getUserId()),
					new SqlParameterValue(Types.DATE, toUse.getCreationDate()),
					new SqlParameterValue(Types.DATE, toUse.getModifiedDate()));

			if (toUse.getProperties().size() > 0)
				setAlbumProperties(toUse.getAlbumId(), toUse.getProperties());

		}
		return toUse;
	}
	
	public List<AlbumImage> getImages(Album album){
		return getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_WEB.SELECT_ALBUM_IMAGE_IDS").getSql(), new RowMapper<AlbumImage>() { 
			public AlbumImage mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new AlbumImage(rs.getLong(1), rs.getInt(2), rs.getLong(3));
			} 
		}, new SqlParameterValue(Types.NUMERIC, album.getAlbumId()));
	}

 
	public List<AlbumContents> getContents(Album album) {
		return getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_WEB.SELECT_ALBUM_CONTENT_IDS").getSql(), new RowMapper<AlbumContents>() { 
			public AlbumContents mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new AlbumContents(rs.getLong(1), rs.getInt(2), rs.getLong(3), rs.getInt(4));
			} 
		}, new SqlParameterValue(Types.NUMERIC, album.getAlbumId()));
	}
	

	public Album update(Album album) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_ALBUM").getSql(),
				/// new SqlParameterValue (Types.NUMERIC, image.getImageId()),
				new SqlParameterValue(Types.VARCHAR, album.getName()),
				new SqlParameterValue(Types.VARCHAR, album.getDescription()),
				new SqlParameterValue(Types.DATE, album.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, album.getAlbumId()));

		deleteAlbumProperties(album.getAlbumId());
		if (album.getProperties().size() > 0)
			setAlbumProperties(album.getAlbumId(), album.getProperties());

		return album;
	}

	public void delete(Album album) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_ALBUM_BY_ID").getSql(),
				new SqlParameterValue(Types.NUMERIC, album.getAlbumId()));
		
		deleteAlbumProperties(album.getAlbumId());
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_ALBUM_IMAGES_BY_ID").getSql(), new SqlParameterValue(Types.NUMERIC, album.getAlbumId())); 
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_ALBUM_CONTENTS_BY_ID").getSql(), new SqlParameterValue(Types.NUMERIC, album.getAlbumId())); 

	}

	@Override
	public void update(Album album, List<Image> images) { 
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_ALBUM_IMAGES_BY_ID").getSql(), new SqlParameterValue(Types.NUMERIC, album.getAlbumId())); 
		final List<Image> imagesToUse = images; 
		final Album albumToUse = album;
		getExtendedJdbcTemplate().batchUpdate(
				getBoundSql("COMMUNITY_WEB.INSERT_ABLUM_IMAGE").getSql(),
				new BatchPreparedStatementSetter() { 
				    public void setValues(PreparedStatement ps, int i) throws SQLException {
				    	Image img  = imagesToUse.get(i); 
						ps.setLong(1, albumToUse.getAlbumId());
						ps.setLong(2, img.getImageId());
						ps.setInt(3, i);
				    }
				    public int getBatchSize() {
				    	return imagesToUse.size();
				    }
				}
		); 

	}
	
	public void update(Album album, List<AlbumContents> contents, boolean clearBeforeUpdate) { 
		
		if(clearBeforeUpdate )
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_ALBUM_CONTENTS_BY_ID").getSql(), new SqlParameterValue(Types.NUMERIC, album.getAlbumId())); 
		final Album albumToUse = album;
		final List<AlbumContents> contentsToUse = contents; 
		
		getExtendedJdbcTemplate().batchUpdate(
				getBoundSql("COMMUNITY_WEB.INSERT_ABLUM_CONTENTS").getSql(),
				new BatchPreparedStatementSetter() { 
				    public void setValues(PreparedStatement ps, int i) throws SQLException {
				    	AlbumContents item  = contentsToUse.get(i); 
				    	ps.setLong(1, albumToUse.getAlbumId());
						ps.setLong(2, item.getContentType());
						ps.setLong(3, item.getContentId());
						ps.setInt(4, i);
				    }
				    public int getBatchSize() {
				    	return contentsToUse.size();
				    }
				}
		); 

	}
}
