package com.ytu.google.cloud.models;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

public class PicasaFeed extends Feed {

	
	@Key("entry")
	public List<AlbumEntry> albums;
	
	public static PicasaFeed executeGet(HttpTransport transport,
			PicasaUrl url) throws IOException { 
		return (PicasaFeed)Feed.executeGet(transport, url, PicasaFeed.class);
	}
	
}
