package com.google.cloud.models.picasa;

import java.io.IOException;
import java.util.List;

import com.google.api.client.googleapis.xml.atom.GData;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;


public class Feed {
	
	/*关于Feed的基本信息*/
	@Key("id")
	private String feed_id;
	@Key("updated")
	private String feed_updated;
	@Key("title")
	private String feed_title;
	
	@Key("link")
	private List<Link> links;
	
	public String getFeedId(){
		return feed_id;
	}
	public String getFeedUpdated() {
		return feed_updated;
	}
	public String getFeedTitle() {
		return feed_title;
	}
	
	public String getPostLink() {
		return Link.find(links, "http://schemas.google.com/g/2005#post");
	}
	
	
	/*处理如果一个Feed足够大的话会包含有下页*/
	public String getNextLink() {
		return Link.find(links,"next");
	}
	
	
	public static Feed executeGet(HttpTransport transport,PicasaUrl url,
			Class<? extends Feed> feedClass) throws IOException {
//		url.fields = GData.getFieldsFor(feedClass);
		HttpRequest request = transport.buildGetRequest();
		request.url = url;
		return request.execute().parseAs(feedClass);
	}
	   

}
