package com.google.cloud.models.picasa;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

public class AlbumFeed extends Feed{

	/**/
	@Key("entry")
	public List<PhotoEntry> photos;
	
	/*得到照片实体*/
	public static AlbumFeed executeGet(HttpTransport transport,PicasaUrl url
	) throws IOException{
		return (AlbumFeed)Feed.executeGet(transport, url, AlbumFeed.class);
	}
	
}
