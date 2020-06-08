package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import architecture.community.util.DateUtils;
import architecture.ee.util.maven.MavenVersion;

public class LibraryObject {
	/**
	 * Name of the lib.
	 */
	private String name;
	/**
	 * Version of the lib.
	 */
	private MavenVersion mavenVersion;

	/**
	 * Last modified timestamp.
	 */
	private Date lastModified; 
	 
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MavenVersion getMavenVersion() {
		return mavenVersion;
	}

	public void setMavenVersion(MavenVersion mavenVersion) {
		this.mavenVersion = mavenVersion;
	}

	public String getTimestamp() {
		return mavenVersion == null ? DateUtils.toISO8601String(lastModified) : mavenVersion.getFileTimestamp() ;
	}

	public String getGroup() {
		return mavenVersion == null ? "-" : mavenVersion.getGroup();
	}

	public String getArtifact() {
		return mavenVersion == null ? "-" : mavenVersion.getArtifact();
	}

	public String getVersion() {
		return mavenVersion == null ? "-" : mavenVersion.getVersion();
	}

	@Override
	public String toString() {
		return "LibraryObject{" + "name='" + name + '\'' + ", mavenVersion=" + mavenVersion.getVersion() + ", lastModified='" + getTimestamp() + '\'' + '}';
	}
}