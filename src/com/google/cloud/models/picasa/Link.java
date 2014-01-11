package com.google.cloud.models.picasa;

import java.util.List;

import com.google.api.client.util.Key;

public class Link {
	
	@Key("@rel")
	private String rel;
	@Key("@href")
	private String href;
	
	/*通过判断rel得到相应的href*/
	public static String find(List<Link> links,String rel) {
		if(links != null) {
			for(Link link : links) {
				if(rel.equals(link.rel)) {
					return link.href;
				}
			}
		}
		return null;
	}
	
	

}
