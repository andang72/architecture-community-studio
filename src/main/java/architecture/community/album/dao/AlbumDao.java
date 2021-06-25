package architecture.community.album.dao;

import java.util.List;

import architecture.community.album.Album;
import architecture.community.album.AlbumContents;
import architecture.community.album.AlbumImage;
import architecture.community.album.AlbumNotFoundException;
import architecture.community.image.Image;

public interface AlbumDao {

	public abstract Album getById(long albumId) throws AlbumNotFoundException;
	
	public abstract Album create(Album album);
	
	public abstract Album update(Album album);
	
	public abstract void delete(Album album);
	
	public abstract void update(Album album, List<Image> images );
	
	public abstract void update(Album album, List<AlbumContents> contents, boolean clearBeforeUpdate );
	
	public abstract List<AlbumImage> getImages(Album album); 
	
	public abstract List<AlbumContents> getContents(Album album); 
	
}
