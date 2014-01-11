package com.ytu.google.cloud.models;

import java.io.IOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

public class AlbumEntry extends Entry{
	
	@Key("gphoto:id")
	public String album_id;
	
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
    
    public static void executeUpload(HttpTransport 
    		transport) throws IOException {
    	
    }
	

}
