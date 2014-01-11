package com.google.cloud.models.picasa;

import java.io.IOException;
import java.util.List;

import com.google.api.client.googleapis.xml.atom.AtomPatchRelativeToOriginalContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DataUtil;
import com.google.api.client.util.Key;
import com.google.api.client.xml.atom.AtomContent;

/*所有实体的基类
 * 包含有相册的实体
 * 包含有照片的相册实体
 * 的公共属性和方法*/
public class Entry implements Cloneable {

	/*
	 * 对于每个Entry 都有： id,published,updated,app:edited,
	 * category(2个Entry有区别),title,summary,rights
	 */
	@Key("id")
	public String entry_id;
	@Key("published")
	private String entry_published;
	@Key("title")
	public String entry_title;

	@Key("@gd:etag")
	private String entry_etag;
	@Key("link")
	private List<Link> links;
    
	
	@Override
	protected Entry clone() {
		return DataUtil.clone(this);
	}

	/*执行删除操作*/
	public void executeDelete(HttpTransport transport) throws IOException {
		HttpRequest request = transport.buildDeleteRequest();
		request.setUrl(getEditLink());
		request.headers.ifMatch = this.entry_etag;///判断请求后改相册是否被更改
		request.execute().ignore();
	}

	/*key/value化返回的XML*/
	public Entry executePatchRelativeToOriginal(HttpTransport transport,
			Entry original) throws IOException{
		HttpRequest request = transport.buildPatchRequest();
		request.setUrl(getEditLink());
		request.headers.ifMatch = this.entry_etag;
		AtomPatchRelativeToOriginalContent serializer = new AtomPatchRelativeToOriginalContent();

		serializer.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
		serializer.originalEntry = original;
		serializer.patchedEntry = this;
		request.content = serializer;
		return request.execute().parseAs(getClass());

	}
    /*向entry中执行插入*/
	public static Entry executeInsert(HttpTransport transport, Entry entry,
			String postLink) throws IOException{
		HttpRequest request = transport.buildPostRequest();
		request.setUrl(postLink);
		AtomContent content = new AtomContent();
		content.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
		content.entry = entry;
		request.content = content;
		return request.execute().parseAs(entry.getClass());
	}
	
	protected String getEditLink() {
		return Link.find(links, "edit");
	}

}
