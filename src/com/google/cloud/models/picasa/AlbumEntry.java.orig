package com.google.cloud.models.picasa;

import java.io.IOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

/*用来获取*/
public class AlbumEntry extends Entry {

	/*
	 * 从父类继承5个量：
	 * id,包含相册Id的完整URI
	 * published,相册创建的时间
	 * title,相册的名字
	 * gd:etag,
	 * link
	 * */
	@Key("icon")
	public String album_icon;
	@Key("updated")
	public String album_updated;
	@Key("author")
	public Author author;
	@Key("gphoto:id")
	public String album_id;
	
	@Key("gphoto:numphotos")
	public String numphotos;
	
	
	@Key("gphoto:numphotosremaining")
	public String num_photos_remaining;
	@Key
	public String access;
	
	@Key("media:thumbnail")
	public String photo_entry_thumbnail;
	
	
	
	
    @Override
    public AlbumEntry clone(){
    	return (AlbumEntry) super.clone();
    }
	
    /**/
    public AlbumEntry executePatchRelativeToOriginal (HttpTransport transport,AlbumEntry original) throws IOException {
    	return (AlbumEntry) super.executePatchRelativeToOriginal(transport, original);
    	
    }
	/*增加相册*/
    public static AlbumEntry executeInsert(HttpTransport transport,AlbumEntry entry,String postLink) throws IOException {
    	return (AlbumEntry) Entry.executeInsert(transport, entry, postLink);
    }
    
    /*删除相册*/
    public void executeDelete(HttpTransport transport) throws IOException {
    	super.executeDelete(transport);
    }
    
    /*向相册中上传照片*/
    
    public static void executeUpload(HttpTransport transport) throws IOException {
    	
    }
    
    
    
	
}
