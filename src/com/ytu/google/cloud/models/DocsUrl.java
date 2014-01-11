package com.ytu.google.cloud.models;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;

public class DocsUrl extends GoogleUrl{
	
	@Key
	public String kinds;
	
	public DocsUrl (String encodedUrl){
		super(encodedUrl);
		
	}
	
	public static DocsUrl fromRelativePath(String relativePath) {
		DocsUrl result = new DocsUrl("http://docs.google.com");
		result.appendRawPath(relativePath);
		return result;
	}

}
