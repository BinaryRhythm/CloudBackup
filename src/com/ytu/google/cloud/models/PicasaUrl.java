package com.ytu.google.cloud.models;

import com.google.api.client.googleapis.GoogleUrl;

public class PicasaUrl extends GoogleUrl {
	
	public static final String ROOT_URL = 
		"https://picasaweb.google.com/data/";
	
    public PicasaUrl(String encodeUrl) {
    	super(encodeUrl);
    }
    
    public static PicasaUrl relativeToRoot(String relative_path) {
    	return new PicasaUrl(ROOT_URL + relative_path);
    }
	
	
}
