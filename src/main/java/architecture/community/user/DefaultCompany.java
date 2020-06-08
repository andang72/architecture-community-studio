package architecture.community.user;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;

public class DefaultCompany extends PropertyAwareSupport implements Company{

	private Long companyId;
	private String name;
    private String displayName;
    private String description;
    private String domain;
    private int memberCount;
	private Date creationDate;
	private Date modifiedDate;
	
	public DefaultCompany() { 
		this.companyId = -1L;
		this.displayName = "unknown";
		Date now = Calendar.getInstance().getTime();
		this.setCreationDate(now);
		this.setModifiedDate(now);
		this.memberCount = 0;
	}

	


	public String getDescription() {
		return description;
	}




	public void setDescription(String description) {
		this.description = description;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public Long getCompanyId() {
		return companyId;
	}




	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}




	public String getDisplayName() {
		return displayName;
	}




	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}




	public String getDomain() {
		return domain;
	}




	public void setDomain(String domain) {
		this.domain = domain;
	}




	public int getMemberCount() {
		return memberCount;
	}




	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}




	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() {
		return modifiedDate;
	}
	
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

 
}
