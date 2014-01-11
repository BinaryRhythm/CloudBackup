package com.google.cloud.models.picasa;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

public class UserFeed extends Feed{

	@Key("entry")
	public List<AlbumEntry> albums;
	
	/*得到相册实体*/
	public static UserFeed executeGet(HttpTransport transport,PicasaUrl url
			) throws IOException{
		return (UserFeed) Feed.executeGet(transport, url, UserFeed.class);
	}
	
	
}
