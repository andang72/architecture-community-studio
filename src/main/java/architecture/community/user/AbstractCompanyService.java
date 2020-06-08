package architecture.community.user;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.services.CommunitySpringEventPublisher;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public abstract class AbstractCompanyService implements CompanyService {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	@Qualifier("communityEventPublisher")
	protected CommunitySpringEventPublisher communitySpringEventPublisher;

	protected boolean caseInsensitiveCompanyNameMatch;

	@Inject
	@Qualifier("companyCache")
	protected Cache companyCache;

	@Inject
	@Qualifier("companyIdCache")
	protected Cache companyIdCache;

	public AbstractCompanyService() {
		this.caseInsensitiveCompanyNameMatch = true;
	}

	public void setCaseInsensitiveCompanyNameMatch(boolean caseInsensitiveCompanyNameMatch) {
		this.caseInsensitiveCompanyNameMatch = caseInsensitiveCompanyNameMatch;
	}

	public Company getCompany(String name) throws CompanyNotFoundException {
		String nameToUse = caseCompanyName(name);
		if (companyIdCache.get(nameToUse) != null) { 
			Long companyId = (Long) companyIdCache.get(nameToUse).getObjectValue();
			return getCompany(companyId);
		} else {
			Company c = lookupCompany(nameToUse);
			companyIdCache.put(new Element(nameToUse, c.getCompanyId()));
			return getCompany(c.getCompanyId());
		}
	}

	public Company getCompany(long companyId) throws CompanyNotFoundException {
		Company company = getCompanyInCache(companyId);
		if (company == null) {
			company = lookupCompany(companyId);
			companyCache.put(new Element(companyId, company));
		}
		return company;
	}

	protected Company getCompanyInCache(long companyId) {
		if (companyCache.get(companyId) != null)
			return (Company) companyCache.get(companyId).getObjectValue();
		else
			return null;
	}

	protected String caseCompanyName(String name) {
		return caseInsensitiveCompanyNameMatch ? name.toLowerCase() : name;
	}

	protected abstract Company lookupCompany(String name) throws CompanyNotFoundException;

	protected abstract Company lookupCompany(long groupId) throws CompanyNotFoundException;

	protected boolean nameEquals(Company c1, Company c2) {
		return c1.getName() != null && c2.getName() != null && caseCompanyName(c1.getName()).equals(caseCompanyName(c2.getName()));
	}

	protected void companyNameUpdated(String oldCompanyName) {
		companyIdCache.remove(caseCompanyName(oldCompanyName));
	}

	protected void clearCompanyFromCache(Company group) {
		companyCache.remove(group.getCompanyId());
		String nameToUse = caseCompanyName(group.getName());
		companyIdCache.remove(nameToUse);
	}

}
