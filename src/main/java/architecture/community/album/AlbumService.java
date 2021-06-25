package architecture.community.album;

import java.util.List;

import architecture.community.image.Image;

public interface AlbumService {

	/** ALBUM API **/
	
	public abstract void saveOrUpdate( Album album ); 
	 
	public abstract void delete(Album album) ; 
	
	public abstract void saveOrUpdate( Album album , List<Image> images ); 
	
	public abstract void saveOrUpdate( Album album , List<AlbumContents> contents, boolean clearBeforeUpdate ); 
	
	
	public abstract Album getAlbum(long albumId) throws AlbumNotFoundException;
	
	public abstract List<Album> getAlbums();
	
	public abstract List<AlbumImage> getAlbumImages(Album album);
	
	public abstract List<AlbumContents> getAlbumContents(Album album);
	
}
