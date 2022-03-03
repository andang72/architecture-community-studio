package tests;


import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

public class DownloadImageFromUrlTest {

	
	public static void main(String args[]){
		 
		try {
			// 다운로드를 위한 이미지 URL 정보 

			
			 //api.getParameters();

			
			URL url = new URL("https://image.dcinside.com/download.php?no=24b0d769e1d32ca73feb83fa11d02831b9f69be2ae2fc8e2b8c80ee7de95e75b0091899aab42e8370825206eb79712e7c58a955e3c027b297af5df5e2a10f4421dd2a9f16f2b095d76dd210876c33577101cb9a7&f_no=20220201_161813.jpg");
			File image = readFileFromUrl( url );
			String contentType = getContentType(image);
			System.out.println( contentType );
			System.out.println( image.getPath() );

			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * get content type from file.
	 * @param image
	 * @return
	 */
	public static String getContentType(File image) {
		String contentType = null; 
		Tika tika = new Tika(); 
		try {
		    contentType = tika.detect(image);
		} catch (IOException e) {
		    contentType = null;
		} 
	    return contentType;
	}

	/**
	 * read image file from url.
	 * @param imageUrl
	 * @return
	 * @throws IOException
	 */	
	public static File readFileFromUrl(URL url) throws Exception { 
		InputStream inputStream = null;
		try { 

			Map<String, String> headers = new HashMap<String, String>();
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

			//String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"; 
			URLConnection con = url.openConnection(); 
			
			for( String name :  headers.keySet()){
				con.setRequestProperty(name, headers.get(name)); 
			}

			inputStream = con.getInputStream();
			File temp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
			
			FileUtils.copyToFile( inputStream, temp );
			return temp;
		}finally {
			IOUtils.closeQuietly(inputStream);
		}
	}


	
	
}
