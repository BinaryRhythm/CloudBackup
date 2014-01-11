package com.ytu.google.cloud.models;

import com.google.api.client.util.Key;

public class PhotoEntry extends Entry {
	
	@Key("media:thumbnail")
	public String thumbnail;
	
	@Key("gphoto:id")
	public String photo_id;
	@Key("gphoto:access")
	public String access;

}
