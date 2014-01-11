package com.google.cloud.models.picasa;

import java.util.Map;

import com.google.api.client.xml.XmlNamespaceDictionary;

public class Util {

	public static final XmlNamespaceDictionary NAMESPACE_DICTIONARY = new XmlNamespaceDictionary();
	static {
		Map<String, String> map = NAMESPACE_DICTIONARY.namespaceAliasToUriMap;
		map.put("", "http://www.w3.org/2005/Atom");
		map.put("atom", "http://www.w3.org/2005/Atom");
		map.put("exif", "http://schemas.google.com/photos/exif/2007");
		map.put("gd", "http://schemas.google.com/g/2005");
		map.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		map.put("georss", "http://www.georss.org/georss");
		map.put("gml", "http://www.opengis.net/gml");
		map.put("gphoto", "http://schemas.google.com/photos/2007");
		map.put("media", "http://search.yahoo.com/mrss/");
		map.put("openSearch", "http://a9.com/-/spec/opensearch/1.1/");
		map.put("xml", "http://www.w3.org/XML/1998/namespace");
	}

	private Util() {
	}

}
