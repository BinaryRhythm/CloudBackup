package com.ytu.google.cloud.models;

import java.util.List;

import com.google.api.client.util.Key;

public class Link {

	@Key("rel")
	public String rel;
	
	@Key("href")
	public String href;
	
	public static String find(List<Link> links,String rel) {
		if(links != null){
			for(Link link:links){
				if(rel.equals(link.rel)){
					return link.href;
				}
			}
		}
		return null;
	}
}
