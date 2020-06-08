package architecture.community.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.user.dao.CompanyDao;

public class CommunityCompanyService extends AbstractCompanyService {

	@Inject
	@Qualifier("companyDao")
	private CompanyDao companyDao;

	@Inject
	@Qualifier("userManager")
	private UserManager userManager;

	public CommunityCompanyService() {
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Company createCompany(String name) throws CompanyAlreadyExistsException {
		try {
			getCompany(name);
			throw new CompanyAlreadyExistsException();
		} catch (CompanyNotFoundException unfe) {
			Company company = new DefaultCompany();
			company.setDescription(name);
			company.setName(name);
			company.setDisplayName(name);
			Date companyCreateDate = new Date();
			company.setCreationDate(companyCreateDate);
			company.setModifiedDate(companyCreateDate);
			companyDao.createCompany(company);
			return company;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Company createCompany(String name, String displayName) throws CompanyAlreadyExistsException {
		try {
			getCompany(name);
			throw new CompanyAlreadyExistsException();
		} catch (CompanyNotFoundException unfe) {
			Company company = new DefaultCompany();
			company.setDescription(name);
			company.setDisplayName(displayName);
			company.setName(name);
			Date groupCreateDate = new Date();
			company.setCreationDate(groupCreateDate);
			company.setModifiedDate(groupCreateDate);
			companyDao.createCompany(company);
			return company;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Company createCompany(String name, String displayName, String description)
			throws CompanyAlreadyExistsException {
		try {
			getCompany(name);
			throw new CompanyAlreadyExistsException();
		} catch (CompanyNotFoundException unfe) {
			DefaultCompany company = new DefaultCompany();
			company.setDescription(description);
			company.setDisplayName(displayName);
			company.setName(name);
			Date groupCreateDate = new Date();
			company.setCreationDate(groupCreateDate);
			company.setModifiedDate(groupCreateDate);
			companyDao.createCompany(company);
			return company;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Company createCompany(String name, String displayName, String domainName, String description)
			throws CompanyAlreadyExistsException {
		try {
			getCompany(name);
			throw new CompanyAlreadyExistsException();
		} catch (CompanyNotFoundException unfe) {
			Company company = new DefaultCompany();
			company.setDescription(description);
			company.setDisplayName(displayName);
			company.setName(name);
			Date now = new Date();
			company.setCreationDate(now);
			company.setModifiedDate(now);
			companyDao.createCompany(company);
			return company;
		}
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCompany(Company company) throws CompanyNotFoundException {
		Company companyToUse = companyDao.getCompanyById(company.getCompanyId());
		if (companyToUse == null)
			throw new CompanyNotFoundException(); 
		clearCompanyFromCache(companyToUse);
		companyDao.deleteCompany(companyToUse);
	}

	public int getTotalCompanyCount() {
		try {
			return companyDao.getCompanyCount();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	public List<Company> getCompanies() {
		List<Long> companyIds = companyDao.getAllCompanyIds();
		List<Company> list = new ArrayList<Company>(companyIds.size());
		for (Long companyId : companyIds)
			try {
				list.add(getCompany(companyId));
			} catch (CompanyNotFoundException e) {
			}
		return list;
	}

	public List<Company> getCompanies(int startIndex, int numResults) {
		List<Long> companyIDs = companyDao.getCompanyIds(startIndex, numResults);
		List<Company> list = new ArrayList<Company>(companyIDs.size());
		for (Long companyId : companyIDs)
			try {
				list.add(getCompany(companyId));
			} catch (CompanyNotFoundException e) {
			}

		return list;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateCompany(Company company) throws CompanyNotFoundException, CompanyAlreadyExistsException {

		Company original = companyDao.getCompanyById(company.getCompanyId());
		if (original == null)
			throw new CompanyAlreadyExistsException(); // CodeableException.newException(CompanyAlreadyExistsException.class,
														// 5141, company.getCompanyId()); // new
		// GroupNotFoundException();

		String oldCompanyName = null;
		String newCompanyName = null;

		if (!nameEquals(original, company)) {
			try {
				Company checked = getCompany(caseCompanyName(company.getName()));
				if (checked.getCompanyId() == company.getCompanyId()) {
					throw new CompanyAlreadyExistsException(); // CodeableException.newException(CompanyAlreadyExistsException.class,
																// 5143); // new
					// GroupAlreadyExistsException("Group
					// with
					// this
					// name
					// already
					// exists.");
				}
			} catch (CompanyNotFoundException e) {
				oldCompanyName = original.getName();
				newCompanyName = company.getName();
			}
		}
		company.setModifiedDate(new Date());
		companyDao.updateCompany(company);
		if (oldCompanyName != null && newCompanyName != null) {
			companyNameUpdated(oldCompanyName);
		}
		clearCompanyFromCache(company);
	}

	@Override
	protected Company lookupCompany(String name) throws CompanyNotFoundException {
		Company g = companyDao.getCompanyByName(name, caseInsensitiveCompanyNameMatch);
		if (g == null)
			throw new CompanyNotFoundException(); // CodeableException.newException(CompanyNotFoundException.class,
													// 5142, name);// new
		// GroupNotFoundException((new
		// StringBuilder()).append("No
		// group
		// found
		// for
		// with
		// name
		// ").append(name).toString());
		else
			return g;
	}

	/*
	 * @Override protected Company lookupCompanyByDomainName(String name) throws
	 * CompanyNotFoundException { Company g =
	 * companyDao.getCompanyByDomainName(name); if(g == null) throw
	 * CodeableException.newException(CompanyNotFoundException.class, 5142,
	 * name);//new GroupNotFoundException((new StringBuilder()).append(
	 * "No group found for with name ").append(name).toString()); else return g; }
	 */

	@Override
	protected Company lookupCompany(long companyId) throws CompanyNotFoundException {
		if (companyId == -2L)
			return null; // new RegisteredUsersGroup();
		Company foundCompany = companyDao.getCompanyById(companyId);
		if (foundCompany == null)
			throw new CompanyNotFoundException(); // throw
													// CodeableException.newException(CompanyNotFoundException.class,
													// 5141, companyId); // new
		// GroupNotFoundException((new
		// StringBuilder()).append("No
		// group
		// found
		// for
		// with
		// id
		// ").append(groupId).toString());
		else
			return foundCompany;
	}

	public int getTotalCompanyUserCount(Company company) {
		return 0;// userManager.getUserCount(company);
	}

	public List<User> getCompanyUsers(Company company) {
		return userManager.getUsers();
	}

	public List<User> getCompanyUsers(Company company, int startIndex, int numResults) {
		return null; // return userManager.getUsers(company, startIndex, numResults);
	}

	public List<User> findCompanyUsers(Company company, String nameOrEmail) {
		return null; // userManager.findUsers(company, nameOrEmail);
	}

	public List<User> findCompanyUsers(Company company, String nameOrEmail, int startIndex, int numResults) {
		return null; // userManager.findUsers(company, nameOrEmail, startIndex, numResults);
	}

	public int getFoundCompanyUserCount(Company company, String nameOrEmail) {
		return 0; // userManager.getFoundUserCount(company, nameOrEmail);
	}

}
