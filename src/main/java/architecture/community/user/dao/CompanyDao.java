package architecture.community.user.dao; 

import java.util.List;

import architecture.community.user.Company;

public interface CompanyDao {

    public void createCompany(Company company);

    /**
     * Company Name 으로 검색 ..
     * @param name
     * @param caseInsensitive
     * @return
     */
    public Company getCompanyByName(String name, boolean caseInsensitive);

    public Company getCompanyById(long companyId);

    public void updateCompany(Company company);
    
    public void deleteCompany(Company company);
    

    public Company getCompanyByDomain(String domainName);
 
    /**
     * 제공되지 않는 기능
     * 
     * @return
     */
    public List<Company> getCompanies();

    /**
     * 제공되지 않는 기능
     * 
     * @return
     */
    public List<Company> getCompanies(int start, int maxResults);

    public int getCompanyCount();

    public List<Long> getAllCompanyIds();

    public abstract List<Long> getCompanyIds(int start, int maxResults);

    /**
     * Company 에 해당하는 모든 그룹 수를 리턴한다.
     * 
     * @return
     */
    public int getCompanyGroupCount(long companyId);

    /**
     * Company 에 해당하는 모든 그룹 아이디를 리턴한다.
     * 
     * @return
     */
    public abstract List<Long> getCompanyGroupIds(long companyId);

    /**
     * Company 에 해당하는 모든 그룹 아이디를 리턴한다.
     * 
     * @return
     */
    public abstract List<Long> getCompanyGroupIds(long companyId, int start, int maxResults);

 

}