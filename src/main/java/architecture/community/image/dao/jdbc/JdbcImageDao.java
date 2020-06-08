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

package architecture.community.image.dao.jdbc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;

import architecture.community.image.DefaultImage;
import architecture.community.image.DefaultLogoImage;
import architecture.community.image.Image;
import architecture.community.image.ImageNotFoundException;
import architecture.community.image.LogoImage;
import architecture.community.image.dao.ImageDao;
import architecture.community.model.Models;
import architecture.community.user.UserTemplate;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;
import architecture.ee.spring.jdbc.InputStreamRowMapper;

public class JdbcImageDao extends ExtendedJdbcDaoSupport implements ImageDao { 
	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
	
	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;	
	
	private String propertyTableName = "AC_UI_IMAGE_PROPERTY";
	private String propertyPrimaryColumnName = "IMAGE_ID";
	
	private Logger logger = LoggerFactory.getLogger(getClass().getName());
	
	private final RowMapper<LogoImage> logoMapper = new RowMapper<LogoImage>() {
		public LogoImage mapRow(ResultSet rs, int rowNum) throws SQLException {
		    DefaultLogoImage image = new DefaultLogoImage();
		    image.setImageId(rs.getLong("LOGO_ID"));
		    image.setObjectType(rs.getInt("OBJECT_TYPE"));
		    image.setObjectId(rs.getLong("OBJECT_ID"));
		    image.setName(rs.getString("FILE_NAME"));
		    image.setPrimary((rs.getInt("PRIMARY_IMAGE") == 1 ? true : false));
		    image.setSize(rs.getInt("FILE_SIZE"));
		    image.setContentType(rs.getString("CONTENT_TYPE"));
		    image.setCreationDate(rs.getTimestamp("CREATION_DATE"));
		    image.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));
		    return image;
		}
	};
	
	private final RowMapper<Image> imageMapper = new RowMapper<Image>(){
		public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultImage image = new DefaultImage();
			image.setImageId(rs.getLong("IMAGE_ID"));
			image.setObjectType(rs.getInt("OBJECT_TYPE"));
			image.setObjectId(rs.getLong("OBJECT_ID"));
			image.setName(rs.getString("FILE_NAME"));
			image.setSize(rs.getInt("FILE_SIZE"));
			image.setUser(new UserTemplate(rs.getLong("USER_ID")));
			image.setContentType(rs.getString("CONTENT_TYPE"));
			image.setCreationDate(rs.getDate("CREATION_DATE"));
			image.setModifiedDate(rs.getDate("MODIFIED_DATE"));			
			return image;
		}		
	};
	
	public JdbcImageDao() {
	}

	public long getNextImageId(){
		return sequencerFactory.getNextValue(Models.IMAGE.getObjectType(), Models.IMAGE.name());
	}	
	
	public long getNextLogoId(){
		return sequencerFactory.getNextValue(Models.LOGO_IMAGE.getObjectType(), Models.LOGO_IMAGE.name());
	} 

	public Map<String, String> getImageProperties(long imageId) {
		return propertyDao.getProperties(propertyTableName, propertyPrimaryColumnName, imageId);
	}

	public void deleteImageProperties(long imageId) {
		propertyDao.deleteProperties(propertyTableName, propertyPrimaryColumnName, imageId);
	}
	
	public void setImageProperties(long imageId, Map<String, String> props) {
		propertyDao.updateProperties(propertyTableName, propertyPrimaryColumnName, imageId, props);
	}
	
	public Image createImage(Image image) {
		
		Image toUse = image;		
		if( toUse.getImageId() <1L){ 
			
			long imageId = getNextImageId();
			if( image instanceof DefaultImage){
				DefaultImage impl = (DefaultImage)toUse;
				impl.setImageId(imageId);
			}			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_IMAGE").getSql(), 	
					new SqlParameterValue (Types.NUMERIC, imageId), 
					new SqlParameterValue (Types.INTEGER, image.getObjectType() ), 
					new SqlParameterValue (Types.NUMERIC, image.getObjectId() ), 
					new SqlParameterValue (Types.VARCHAR, image.getName() ), 
					new SqlParameterValue (Types.INTEGER, image.getSize() ), 
					new SqlParameterValue (Types.VARCHAR, image.getContentType()), 
					new SqlParameterValue (Types.NUMERIC, image.getUser().getUserId()), 
					new SqlParameterValue(Types.DATE, image.getCreationDate()),
					new SqlParameterValue(Types.DATE, image.getModifiedDate()));	
			
			if( image.getProperties().size() > 0 )
				setImageProperties(image.getImageId(), image.getProperties());
			
		}		
		return  image;
	}

	public Image updateImage(Image image) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_IMAGE").getSql(), 	
				///new SqlParameterValue (Types.NUMERIC, image.getImageId()), 
				new SqlParameterValue (Types.INTEGER, image.getObjectType() ), 
				new SqlParameterValue (Types.INTEGER, image.getObjectId() ), 
				new SqlParameterValue (Types.VARCHAR, image.getName() ), 
				new SqlParameterValue (Types.INTEGER, image.getSize() ), 
				new SqlParameterValue (Types.VARCHAR, image.getContentType()), 
				//new SqlParameterValue(Types.DATE, image.getCreationDate()),
				new SqlParameterValue(Types.DATE, image.getModifiedDate()),
				new SqlParameterValue (Types.NUMERIC, image.getImageId()) );	
		
		deleteImageProperties(image.getImageId());
		if( image.getProperties().size() > 0 )
			setImageProperties(image.getImageId(), image.getProperties());
		
		return image;
	}
			
	public void deleteImage(Image image) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_IMAGE_BY_ID").getSql(), 	
				new SqlParameterValue (Types.NUMERIC, image.getImageId()));
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_IMAGE_DATA_BY_ID").getSql(), 	
				new SqlParameterValue (Types.NUMERIC, image.getImageId()));	
		deleteImageProperties(image.getImageId());
	}

	public InputStream getImageInputStream(Image image) {
		return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_WEB.SELECT_IMAGE_DATA_BY_ID").getSql(), new InputStreamRowMapper(), new SqlParameterValue (Types.NUMERIC, image.getImageId()));		
	}
	
	public void saveImageInputStream(Image image, InputStream inputStream) {
		
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_IMAGE_DATA_BY_ID").getSql(), new SqlParameterValue (Types.NUMERIC, image.getImageId()));
		
		if( getExtendedJdbcTemplate().getDBInfo() == DB.ORACLE ){						
			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_EMPTY_IMAGE_DATA").getSql(), new SqlParameterValue (Types.NUMERIC, image.getImageId()));
			
			
			getExtendedJdbcTemplate().update(
					getBoundSql("COMMUNITY_WEB.UPDATE_IMAGE_DATA").getSql(), 
					new Object[]{
						new SqlLobValue( inputStream , image.getSize(), getLobHandler()),
						image.getImageId()
					}, 
					new int[]{
						Types.BLOB,
						Types.NUMERIC
					});
		}else{			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_IMAGE_DATA").getSql(), 
					new SqlParameterValue ( Types.NUMERIC, image.getImageId()), 
					new SqlParameterValue ( Types.BLOB,  new SqlLobValue( inputStream , image.getSize(), getLobHandler() ) ) 
			);
		}		
	}

	public Image getImageById(long imageId) {		 
		Image image = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_WEB.SELECT_IMAGE_BY_ID").getSql(), imageMapper, new SqlParameterValue (Types.NUMERIC, imageId ));		
		Map<String, String> properties = getImageProperties(image.getImageId());
		image.getProperties().putAll(properties);
		return image;
	}
	
	
	//
	
	public void addLogoImage(LogoImage logoImage, File file) {
		try {
			if (logoImage.getSize() == 0)
				logoImage.setSize((int) FileUtils.sizeOf(file));
			
			addLogoImage(logoImage, file != null ? FileUtils.openInputStream(file) : null);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public void addLogoImage(LogoImage logoImage, InputStream is) {
		LogoImage toUse = logoImage;
		long logoIdToUse = logoImage.getImageId();
		if (logoIdToUse < 1) {
			logoIdToUse = getNextLogoId();
			((DefaultLogoImage)logoImage).setImageId(logoIdToUse);
		}
		getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.RESET_LOGO_IMAGE_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
				new SqlParameterValue(Types.INTEGER, toUse.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, toUse.getObjectId()));

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_LOGO_IMAGE").getSql(),
				new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()),
				new SqlParameterValue(Types.NUMERIC, logoImage.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, logoImage.getObjectId()),
				new SqlParameterValue(Types.NUMERIC, logoImage.isPrimary() ? 1 : 0),
				new SqlParameterValue(Types.VARCHAR, logoImage.getName()),
				new SqlParameterValue(Types.NUMERIC, logoImage.getSize()),
				new SqlParameterValue(Types.VARCHAR, logoImage.getContentType()),
				new SqlParameterValue(Types.DATE, logoImage.getModifiedDate()),
				new SqlParameterValue(Types.DATE, logoImage.getCreationDate()));
		
		updateImageImputStream(logoImage, is);
	}

	public void updateLogoImage(LogoImage logoImage, File file) {
		try {
			updateLogoImage(logoImage, file != null ? FileUtils.openInputStream(file) : null);
		} catch (IOException e1) {

		}
	}

	public void updateLogoImage(LogoImage logoImage, InputStream is) {

		if (logoImage.isPrimary()) {
			getExtendedJdbcTemplate().update(
					getBoundSql("COMMUNITY_WEB.RESET_LOGO_IMAGE_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
					new SqlParameterValue(Types.INTEGER, logoImage.getObjectType()),
					new SqlParameterValue(Types.NUMERIC, logoImage.getObjectId()));
		}

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_LOGO_IMAGE").getSql(),
				new SqlParameterValue(Types.NUMERIC, logoImage.getObjectType()),
				new SqlParameterValue(Types.NUMERIC, logoImage.getObjectId()),
				new SqlParameterValue(Types.NUMERIC, logoImage.isPrimary() ? 1 : 0),
				new SqlParameterValue(Types.VARCHAR, logoImage.getName()),
				new SqlParameterValue(Types.NUMERIC, logoImage.getSize()),
				new SqlParameterValue(Types.VARCHAR, logoImage.getContentType()),
				new SqlParameterValue(Types.DATE, logoImage.getModifiedDate()),
				new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()));
		if (is != null)
			updateImageImputStream(logoImage, is);
	}

	protected void updateImageImputStream(LogoImage logoImage, InputStream inputStream) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_LOGO_IMAGE_DATA_BY_ID").getSql(),
				new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()));
		
		if ( getExtendedJdbcTemplate().getDBInfo() == DB.ORACLE ){
			getExtendedJdbcTemplate().update(
					getBoundSql("COMMUNITY_WEB.INSERT_EMPTY_LOGO_IMAGE_DATA").getSql(),
					new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()));
			getExtendedJdbcTemplate().update(
					getBoundSql("COMMUNITY_WEB.UPDATE_LOGO_IMAGE_DATA").getSql(),
					new SqlParameterValue(Types.BLOB, new SqlLobValue(inputStream, logoImage.getSize(), getLobHandler())),
					new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()));
		} else {
			getExtendedJdbcTemplate().update(
					getBoundSql("COMMUNITY_WEB.INSERT_LOGO_IMAGE_DATA").getSql(),
					new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()), 
					new SqlParameterValue(Types.BLOB, new SqlLobValue(inputStream, logoImage.getSize(), getLobHandler())));
		}
	}

	public void removeLogoImage(LogoImage logoImage) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_LOGO_IMAGE_BY_ID").getSql(),
				new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()));
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_LOGO_IMAGE_DATA_BY_ID").getSql(),
				new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()));
	}

	public InputStream getInputStream(LogoImage logoImage) throws IOException {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_LOGO_IMAGE_DATA_BY_ID").getSql(),
				new InputStreamRowMapper(), 
				new SqlParameterValue(Types.NUMERIC, logoImage.getImageId()));
	}

	public Long getPrimaryLogoImageId(int objectType, long objectId) throws ImageNotFoundException {
		try {
			return getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_WEB.SELECT_PRIMARY_LOGO_IMAGE_ID_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
					Long.class,
					new SqlParameterValue(Types.NUMERIC, objectType), 
					new SqlParameterValue(Types.NUMERIC, objectId));
		} catch (DataAccessException e) {
			throw new ImageNotFoundException(e);
		}
	}

	public LogoImage getLogoImageById(long logoId) throws ImageNotFoundException {
		try {
			return getExtendedJdbcTemplate().queryForObject(
					getBoundSql("COMMUNITY_WEB.SELECT_LOGO_IMAGE_BY_ID").getSql(), logoMapper,
					new SqlParameterValue(Types.NUMERIC, logoId));
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new ImageNotFoundException(e);
		}
	}

	public List<Long> getLogoImageIds(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForList(
				getBoundSql("COMMUNITY_WEB.SELECT_LOGO_IMAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(), 
				Long.class,
				new SqlParameterValue(Types.NUMERIC, objectType), 
				new SqlParameterValue(Types.NUMERIC, objectId));
	}

	public int getLogoImageCount(int objectType, long objectId) {
		return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.COUNT_LOGO_IMAGE_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
				Integer.class,
				new SqlParameterValue(Types.NUMERIC, objectType), 
				new SqlParameterValue(Types.NUMERIC, objectId));
	}
	
}
