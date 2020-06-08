package architecture.community.image;

import java.io.Serializable;

public class ImageLink implements Serializable {

	private String filename;
	
	private Long imageId = -1L;

	private String linkId;

	private boolean publicShared = false;

	public ImageLink() {
		super();
	}


	/**
	 * @param linkId
	 * @param imageId
	 * @param publicShared
	 */
	public ImageLink(String linkId, Long imageId, boolean publicShared) {
		this.linkId = linkId;
		this.imageId = imageId;
		this.publicShared = publicShared;
		this.filename = null;
	}
	 

	public String getFilename() {
		return filename;
	}
 

	public void setFilename(String filename) {
		this.filename = filename;
	}

 
	/**
	 * @return imageId
	 */
	public Long getImageId() {
		return imageId;
	}

	/**
	 * @return linkId
	 */
	public String getLinkId() {
		return linkId;
	}

	/**
	 * @return publicShared
	 */
	public boolean isPublicShared() {
		return publicShared;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImageLink [");
		if (imageId != null)
			builder.append("imageId=").append(imageId).append(", ");
		if (linkId != null)
			builder.append("linkId=").append(linkId).append(", ");
		builder.append("publicShared=").append(publicShared).append("]");
		return builder.toString();
	}

}
