package architecture.community.image;

import java.io.Serializable;

public class AlbumImage implements Serializable {

	private Long albumId;

	private Integer order;

	private Long imageId;

	public AlbumImage() {
	}

	public Long getAlbumId() {
		return albumId;
	}

	public AlbumImage(Long albumId, Integer order, Long iamgeId) {
		super();
		this.albumId = albumId;
		this.order = order;
		this.imageId = iamgeId;
	}

	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}
 

}
