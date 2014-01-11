package com.ytu.google.cloud.models;

import java.io.IOException;
import java.util.List;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.xml.atom.GData;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

public class Feed implements Cloneable{

	@Key("link")
	public List<Link> links;
	
//	@Key("entry")
//	public Entry entry;
	
	@Key("feed")
	public FeedTag feedTag;
	
	
	
	public String getPostLink(){
		return Link.find(links,"http://schemas.google.com/g/2005#post");
		
	}
	
	public String getNextLink(){
		return Link.find(links,"next");
	}
	
	public static Feed executeGet(HttpTransport transport,GenericUrl url
			,Class<? extends Feed> feedClass) throws IOException{
	//	url.fields = GData.getFieldsFor(feedClass);
		HttpRequest request = transport.buildGetRequest();
		request.url = url;
		return request.execute().parseAs(feedClass);
	}
	
}
