package architecture.community.model;

import java.io.Serializable;

public class Property implements Serializable {

	private String name;

	private String value;

	public Property() {
	}

	public Property(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
