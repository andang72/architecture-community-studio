package architecture.community.services.database;

import java.io.Serializable;

public class ImageDef implements Serializable {

	String filename;
	String id;

	public ImageDef(String id, String filename) {
		this.id = id;
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImageDef [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (filename != null)
			builder.append("filename=").append(filename);
		builder.append("]");
		return builder.toString();
	}

}
