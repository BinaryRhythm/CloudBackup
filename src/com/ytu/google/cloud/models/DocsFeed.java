package com.ytu.google.cloud.models;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

public class DocsFeed extends Feed{
	
	@Key("entry")
	public List<DocsEntry> docs;
	
	public static DocsFeed executeGet(HttpTransport transport,DocsUrl url)throws IOException{
		//url.kinds = "";
		return (DocsFeed)Feed.executeGet(transport,url, DocsFeed.class);
	}

}
