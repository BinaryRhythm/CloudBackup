package com.ytu.google.cloud.models;

import java.io.IOException;
import java.util.List;

import com.google.api.client.googleapis.xml.atom.AtomPatchRelativeToOriginalContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DataUtil;
import com.google.api.client.util.Key;
import com.google.api.client.xml.atom.AtomContent;
import com.ytu.google.cloud.docs.DocsControl;

public class Entry implements Cloneable{

	  @Key("gd:etag")
	  public String etag;
	  
	  @Key("gd:resourceId")
	  public String gd_resourceId;
	  
	  @Key("content")
	  public Content content;

	  @Key("link")
	  public List<Link> links;

	  @Key("title")
	  public String title;

	  @Override
	  protected Entry clone() {
	    return DataUtil.clone(this);
	  }

	  public void executeDelete(HttpTransport transport) throws IOException {
	    HttpRequest request = transport.buildDeleteRequest();
	    request.setUrl(getEditLink());
	    request.headers.ifMatch = this.etag;
	    request.execute().ignore();
	  }

	  Entry executePatchRelativeToOriginal(HttpTransport transport, Entry original)
	      throws IOException {
	    HttpRequest request = transport.buildPatchRequest();
	    request.setUrl(getEditLink());
	    request.headers.ifMatch = this.etag;
	    AtomPatchRelativeToOriginalContent serializer =
	        new AtomPatchRelativeToOriginalContent();
	    serializer.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
	    serializer.originalEntry = original;
	    serializer.patchedEntry = this;
	    request.content = serializer;
	    return request.execute().parseAs(getClass());
	  }

	  static Entry executeInsert(HttpTransport transport, Entry entry,
	      String postLink) throws IOException {
	    HttpRequest request = transport.buildPostRequest();
	    request.setUrl(postLink);
	    AtomContent content = new AtomContent();
	    content.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
	    content.entry = entry;
	    request.content = content;
	    return request.execute().parseAs(entry.getClass());
	  }

	  private String getEditLink() {
	    return Link.find(links, "edit");
	  }
	
}
