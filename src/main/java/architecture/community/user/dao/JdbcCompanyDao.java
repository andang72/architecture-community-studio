package architecture.community.user.dao;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.model.Models;
import architecture.community.user.Company;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;
import architecture.ee.util.StringUtils;

public class JdbcCompanyDao extends ExtendedJdbcDaoSupport implements CompanyDao {

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;	
	 
    private String companyPropertyTableName = "AC_UI_COMPANY_PROPERTY";
    private String companyPropertyPrimaryColumnName = "COMPANY_ID";

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
	
    /**
     * @param companyPropertyTableName
     */
    public void setCompanyPropertyTableName(String companyPropertyTableName) {
	this.companyPropertyTableName = companyPropertyTableName;
    }

    /**
     * @param userPropertyPrimaryColumnName
     */
    public void setCompanyPropertyPrimaryColumnName(String companyPropertyPrimaryColumnName) {
	this.companyPropertyPrimaryColumnName = companyPropertyPrimaryColumnName;
    }

	public long getNextCompanyId(){
		return sequencerFactory.getNextValue(Models.COMPANY.getObjectType(), Models.COMPANY.name());
	}
	
    public void createCompany(Company company) {
    	long companyId = getNextCompanyId();
		if ("".equals(company.getDescription()))
		    company.setDescription(null);

		getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.CREATE_COMPANY").getSql(),
		new SqlParameterValue(Types.NUMERIC, companyId),
		new SqlParameterValue(Types.VARCHAR, company.getName()),
		new SqlParameterValue(Types.VARCHAR, company.getDisplayName()),
		new SqlParameterValue(Types.VARCHAR, company.getDomain()),
		new SqlParameterValue(Types.VARCHAR, company.getDescription()),
		new SqlParameterValue(Types.DATE, company.getCreationDate()),
		new SqlParameterValue(Types.DATE, company.getModifiedDate()));
		company.setCompanyId(companyId);
		setCompanyProperties(company.getCompanyId(), company.getProperties());
    }

    public Company getCompanyByDomainName(String domainName) {
	if (StringUtils.isEmpty(domainName)) {
	    return null;
	}
	Company company = null;
	try {
		RowMapper<Company> mapper = getMapperSource("COMMUNITY_USER.COMPANY_ROWMAPPER").createRowMapper(Company.class);  
	    company = getExtendedJdbcTemplate().queryForObject(
		    getBoundSql("COMMUNITY_USER.SELECT_COMPANY_BY_DOMAIN_NAME").getSql(), mapper,
		    new SqlParameterValue(Types.VARCHAR, domainName));

	    company.setProperties(getCompanyProperties(company.getCompanyId()));
	} catch (IncorrectResultSizeDataAccessException e) {
	    if (e.getActualSize() > 1) {
	    	logger.warn((new StringBuilder()).append("Multiple occurrances of the same company domainName found: ")
			.append(domainName).toString());
		throw e;
	    }
	} catch (DataAccessException e) {
	    String message = (new StringBuilder()).append("Failure attempting to load company by domainName : ")
		    .append(domainName).append(".").toString();
	    logger.debug(message, e);
	}
	return company;
    }
 

    public Company getCompanyByName(String name, boolean caseInsensitive) {
	if (StringUtils.isEmpty(name)) {
	    return null;
	}
	Company company = null;
	try {
		
		RowMapper<Company> mapper = getMapperSource("COMMUNITY_USER.COMPANY_ROWMAPPER").createRowMapper(Company.class);  
	    company = getExtendedJdbcTemplate().queryForObject(
		    getBoundSqlWithAdditionalParameter("COMMUNITY_USER.SELECT_COMPANY_BY_NAME", new Boolean(caseInsensitive)).getSql(),
		    mapper, new SqlParameterValue(Types.VARCHAR, caseInsensitive ? name.toLowerCase() : name));

	    company.setProperties(getCompanyProperties(company.getCompanyId()));
	} catch (IncorrectResultSizeDataAccessException e) {
	    if (e.getActualSize() > 1) {
		logger.warn((new StringBuilder()).append("Multiple occurrances of the same company name found: ")
			.append(name).toString());
		throw e;
	    }
	} catch (DataAccessException e) {
	    String message = (new StringBuilder()).append("Failure attempting to load company by name : ").append(name).append(".").toString();
	    logger.debug(message, e);
	}
	return company;
    }

    public Company getCompanyById(long companyId) {
	Company company = null;
	try {
	    
		RowMapper<Company> mapper = getMapperSource("COMMUNITY_USER.COMPANY_ROWMAPPER").createRowMapper(Company.class); 
	    
	    logger.debug("using row  mapper : " + mapper.getClass().getName());
	    
	    company = getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_USER.SELECT_COMPANY_BY_ID").getSql(), mapper, new SqlParameterValue(Types.NUMERIC, companyId));
	    company.setProperties(getCompanyProperties(company.getCompanyId()));
	} catch (IncorrectResultSizeDataAccessException e) {
	    if (e.getActualSize() > 1) {
		logger.warn((new StringBuilder()).append("Multiple occurrances of the same company ID found: ").append(companyId).toString());
		throw e;
	    }
	} catch (DataAccessException e) {
	    String message = (new StringBuilder()).append("Failure attempting to load company by ID : ").append(companyId).append(".").toString();
	    logger.error(message, e);
	}
	return company;
    }

    public void updateCompany(Company company) {
    	getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.UPDATE_COMPANY").getSql(),
		new SqlParameterValue(Types.VARCHAR, company.getName()),
		new SqlParameterValue(Types.VARCHAR, company.getDisplayName()),
		new SqlParameterValue(Types.VARCHAR, company.getDomain()),
		new SqlParameterValue(Types.VARCHAR, company.getDescription()),
		new SqlParameterValue(Types.DATE, company.getModifiedDate()),
		new SqlParameterValue(Types.NUMERIC, company.getCompanyId()));
		setCompanyProperties(company.getCompanyId(), company.getProperties());
    }

     
	public void deleteCompany(Company company) {
		if(company.getCompanyId() > 0 ) {
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_USER.DELETE_COMPANY").getSql(),
			new SqlParameterValue(Types.NUMERIC, company.getCompanyId()));
			propertyDao.deleteProperties(companyPropertyTableName, companyPropertyPrimaryColumnName, company.getCompanyId());
		}
	}

	public List<Company> getCompanies() {
	throw new UnsupportedOperationException("Group creation not supported.");
    }

    public List<Company> getCompanies(int start, int maxResults) {
	throw new UnsupportedOperationException("Group creation not supported.");
    }

    public List<Long> getCompanyIds(int startIndex, int numResults) {
    	return getExtendedJdbcTemplate().query( 
			getBoundSql("COMMUNITY_USER.SELECT_ALL_COMPANY_IDS").getSql(), 
			startIndex, 
			numResults,
			Long.class);
    }

    public Map<String, String> getCompanyProperties(long companyId) {
	return propertyDao.getProperties(companyPropertyTableName, companyPropertyPrimaryColumnName, companyId);
    }

    public void setCompanyProperties(long companyId, Map<String, String> props) {
    	propertyDao.updateProperties(companyPropertyTableName, companyPropertyPrimaryColumnName, companyId, props);
    }

    public int getCompanyCount() {
	return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.COUNT_ALL_COMPANY").getSql(), Integer.class);
    }

    public int getCompanyGroupCount(long companyId) {
	return getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_USER.COUNT_COMPANY_GROUPS").getSql(), Integer.class,
		new SqlParameterValue(Types.NUMERIC, companyId));
    }

    public List<Long> getCompanyGroupIds(long companyId) {
	return getExtendedJdbcTemplate().queryForList(
		getBoundSql("COMMUNITY_USER.SELECT_COMPANY_GROUP_IDS").getSql(), Long.class,
		new SqlParameterValue(Types.NUMERIC, companyId));
    }

    public List<Long> getAllCompanyIds() {
	return getExtendedJdbcTemplate()
		.queryForList(getBoundSql("COMMUNITY_USER.SELECT_ALL_COMPANY_IDS").getSql(), Long.class);
    }
 
	public Company getCompanyByDomain(String domainName) { 
		return null;
	}

	@Override
	public List<Long> getCompanyGroupIds(long companyId, int start, int maxResults) { 
		return null;
	}
 
 
}