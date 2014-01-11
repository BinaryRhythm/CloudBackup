package com.google.cloud.models.picasa;

import com.google.api.client.util.Key;

public class PhotoEntry extends Entry{

	
	/*
	 * 从父类继承5个量：
	 * id,包含相册Id的完整URI
	 * published,相册创建的时间
	 * title,相册的名字
	 * gd:etag,
	 * link
	 * */

	@Key("gphoto:id")
	public String photo_id;
	@Key("gphoto:access")
	public String access;
//	@Key("")
	
	
	
	
}
