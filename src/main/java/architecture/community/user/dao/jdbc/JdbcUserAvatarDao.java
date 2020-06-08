package architecture.community.user.dao.jdbc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;

import architecture.community.model.Models;
import architecture.community.user.AvatarImage;
import architecture.community.user.AvatarImageNotFoundException;
import architecture.community.user.dao.UserAvatarDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;
import architecture.ee.spring.jdbc.InputStreamRowMapper;

public class JdbcUserAvatarDao extends ExtendedJdbcDaoSupport implements UserAvatarDao {

	private final RowMapper<AvatarImage> imageMapper = new RowMapper<AvatarImage>() {
		public AvatarImage mapRow(ResultSet rs, int rowNum) throws SQLException {
			AvatarImage image = new AvatarImage();
			image.setAvatarImageId(rs.getLong("AVATAR_IMAGE_ID"));
			image.setUserId(rs.getLong("USER_ID"));
			image.setFilename(rs.getString("FILE_NAME"));
			image.setPrimary((rs.getInt("PRIMARY_IMAGE") == 1 ? true : false));
			image.setImageSize(rs.getInt("FILE_SIZE"));
			image.setImageContentType(rs.getString("CONTENT_TYPE"));
			image.setCreationDate(rs.getTimestamp("CREATION_DATE"));
			image.setModifiedDate(rs.getTimestamp("MODIFIED_DATE"));
			return image;
		}
	};

	@Inject
	@Qualifier("configService")
	private ConfigService configService;

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	public long getNextAvataImageId() {
		return sequencerFactory.getNextValue(Models.AVATAR_IMAGE.getObjectType(), Models.AVATAR_IMAGE.name());
	}

	public void removeAvatarImage(AvatarImage image) {
		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_AVATAR_IMAGE_BY_ID").getSql(),
				new SqlParameterValue(Types.NUMERIC, image.getAvatarImageId()));

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_AVATAR_IMAGE_DATA_BY_ID").getSql(),
				new SqlParameterValue(Types.NUMERIC, image.getAvatarImageId()));
	}

	public void addAvatarImage(AvatarImage image, File file) {
		try {
			AvatarImage toUse = image;
			if (toUse.getImageSize() == 0)
				toUse.setImageSize((int) FileUtils.sizeOf(file));			
			addAvatarImage(image, FileUtils.openInputStream(file));
		} catch (IOException e) {
		}
	}

	public void addAvatarImage(AvatarImage image, InputStream is) {
			AvatarImage toUse = image;
			if (toUse.getAvatarImageId() < 1L) {
				long imageId = getNextAvataImageId();
				if (image instanceof AvatarImage) {
					AvatarImage impl = (AvatarImage) toUse;
					impl.setAvatarImageId(imageId);
				}
			} else {
				Date now = Calendar.getInstance().getTime();
				toUse.setModifiedDate(now);
			}
			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.RESET_AVATAR_IMAGE_BY_USER").getSql(),
				new SqlParameterValue(Types.INTEGER, toUse.getUserId()));
			
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_AVATAR_IMAGE").getSql(),
				new SqlParameterValue(Types.NUMERIC, toUse.getAvatarImageId()),
				new SqlParameterValue(Types.INTEGER, toUse.getUserId()),
				new SqlParameterValue(Types.VARCHAR, toUse.getFilename()),
				new SqlParameterValue(Types.INTEGER, toUse.getImageSize()),
				new SqlParameterValue(Types.VARCHAR, toUse.getImageContentType()),
				new SqlParameterValue(Types.TIMESTAMP, toUse.getCreationDate()),
				new SqlParameterValue(Types.TIMESTAMP, toUse.getModifiedDate()));			
			updateAvatarImageImputStream(image, is);
	}

	
	public AvatarImage getAvatarImageById(Long profileImageId) throws AvatarImageNotFoundException {
		try {
			return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_AVATAR_IMAGE_BY_ID").getSql(), imageMapper,
				new SqlParameterValue(Types.NUMERIC, profileImageId));
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new AvatarImageNotFoundException(e);
		}
	}

	public List<Long> getAvatarImageIds(Long userId) {
		return getExtendedJdbcTemplate().queryForList(
			getBoundSql("COMMUNITY_WEB.SELECT_AVATAR_IMAGE_IDS_BY_USER").getSql(), Long.class,
			new SqlParameterValue(Types.NUMERIC, userId));
	}

	public Integer getAvatarImageCount(Long userId) {
		return getExtendedJdbcTemplate().queryForObject(
			getBoundSql("COMMUNITY_WEB.COUNT_AVATAR_IMAGE_BY_USER").getSql(),
			Integer.class,
			new SqlParameterValue(Types.NUMERIC, userId));
	}

	public InputStream getInputStream(AvatarImage image) {
		return getExtendedJdbcTemplate().queryForObject(
			getBoundSql("COMMUNITY_WEB.SELECT_AVATAR_IMAGE_DATA_BY_ID").getSql(),
			new InputStreamRowMapper(),
			new SqlParameterValue(Types.NUMERIC, image.getAvatarImageId()));
	}

	protected void updateAvatarImageImputStream(AvatarImage image, InputStream inputStream) {
		getExtendedJdbcTemplate().update(
			getBoundSql("COMMUNITY_WEB.DELETE_AVATAR_IMAGE_DATA_BY_ID").getSql(),
			new SqlParameterValue(Types.NUMERIC, image.getAvatarImageId()));
		
		if (getExtendedJdbcTemplate().getDBInfo() == DB.ORACLE) {
			getExtendedJdbcTemplate().update(
				getBoundSql("COMMUNITY_WEB.INSERT_EMPTY_AVATAR_IMAGE_DATA").getSql(),
				new SqlParameterValue(Types.NUMERIC, image.getAvatarImageId()));
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_AVATAR_IMAGE_DATA").getSql(),
				new Object[] { new SqlLobValue(inputStream, image.getImageSize(), getLobHandler()), image.getAvatarImageId() },
				new int[] { Types.BLOB, Types.NUMERIC });
		} else {
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.INSERT_AVATAR_IMAGE_DATA").getSql(),
				new SqlParameterValue(Types.NUMERIC, image.getAvatarImageId()), new SqlParameterValue(Types.BLOB,
				new SqlLobValue(inputStream, image.getImageSize(), getLobHandler())));
		}
	}

	public Long getPrimaryAvatarImageByUser(Long userId) throws AvatarImageNotFoundException {
		try {
			return getExtendedJdbcTemplate().queryForObject(
				getBoundSql("COMMUNITY_WEB.SELECT_PRIMARY_AVATAR_IMAGE_ID_BY_USER").getSql(),
				Long.class,
				new SqlParameterValue(Types.NUMERIC, userId));
		} catch (DataAccessException e) {
			throw new AvatarImageNotFoundException(e);
		}
	}
}
