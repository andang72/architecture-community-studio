package architecture.community.user;

import java.util.List;

public interface CompanyService {

	public abstract Company createCompany(String name) throws CompanyAlreadyExistsException;

	public abstract Company createCompany(String name, String displayName) throws CompanyAlreadyExistsException;

	public abstract Company createCompany(String name, String displayName, String description) throws CompanyAlreadyExistsException;

	public abstract Company createCompany(String name, String displayName, String domainName, String description) throws CompanyAlreadyExistsException;

	public abstract Company getCompany(long groupId) throws CompanyNotFoundException;

	public abstract Company getCompany(String name) throws CompanyNotFoundException;

	public abstract void updateCompany(Company company) throws CompanyNotFoundException, CompanyAlreadyExistsException;

	public abstract void deleteCompany(Company company) throws CompanyNotFoundException;

	
	public abstract int getTotalCompanyCount();

	public abstract List<Company> getCompanies();

	public abstract List<Company> getCompanies(int startIndex, int numResults);

	//public abstract int getTotalCompanyGroupCount(Company group);

	/**
	public abstract List<Group> getCompanyGroups(Company group);

	public abstract List<Group> getCompanyGroups(Company group, int startIndex, int numResults);

	public abstract int getTotalCompanyUserCount(Company company);

	public abstract List<User> getCompanyUsers(Company company);

	public abstract List<User> getCompanyUsers(Company company, int startIndex, int numResults);

	public abstract List<User> findCompanyUsers(Company company, String nameOrEmail);

	public abstract List<User> findCompanyUsers(Company company, String nameOrEmail, int startIndex, int numResults);

	public abstract int getFoundCompanyUserCount(Company company, String nameOrEmail);
	**/
}
