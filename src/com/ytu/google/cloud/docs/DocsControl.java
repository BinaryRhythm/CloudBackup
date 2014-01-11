package com.ytu.google.cloud.docs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.api.client.apache.ApacheHttpTransport;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.DateTime;
import com.google.api.client.xml.atom.AtomContent;
import com.google.api.client.xml.atom.AtomParser;
import com.ytu.google.cloud.frame.MainUI;
import com.ytu.google.cloud.models.DocsEntry;
import com.ytu.google.cloud.models.DocsFeed;
import com.ytu.google.cloud.models.DocsUrl;
import com.ytu.google.cloud.models.PicasaFeed;
import com.ytu.google.cloud.models.PicasaUrl;
import com.ytu.google.cloud.models.Util;

public class DocsControl extends ListActivity {

	private List<DocsEntry> docs;
	private static HttpTransport transport;
	private String postLink;
	private static SharedPreferences pref = MainUI.pref;
	private static GoogleHeaders headers = null;
	private static DocsFeed docsFeed = null;
	private static ProgressDialog dlg_refresh;
	private String[] DocsNames_tmp = null;

	public DocsControl() {
		docs = new ArrayList<DocsEntry>();
		HttpTransport.setLowLevelHttpTransport(ApacheHttpTransport.INSTANCE);
		transport = GoogleTransport.create();
		headers = (GoogleHeaders) transport.defaultHeaders;
		headers.setApplicationName("CloudBackup-YTU-1.0");
		headers.gdataVersion = "3.0";
		// headers.set("If-None-Match", );
		headers.setGoogleLogin(pref.getString(MainUI.DOCS_TOKEN, null));
		AtomParser parser = new AtomParser();
		parser.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
		transport.addParser(parser);

	}

	/*
	 * private void authenticatedLogin(String authToken) { this.authToken =
	 * authToken; ((GoogleHeaders))
	 * transport.defaultHeaders.setGoogleLogin(authToken); authenticated(); }
	 */

	public List<DocsEntry> getDocsRoot(boolean filter) {

		docs = new ArrayList<DocsEntry>();
		try {
			DocsUrl url;
			if (filter) {
				url = DocsUrl
						.fromRelativePath("/feeds/default/private/full/folder%3Aroot/contents/-/folder");
			} else {
				url = DocsUrl.fromRelativePath("/feeds/default/private/full");
			}
			DocsFeed userFeed = docsFeed = DocsFeed.executeGet(transport, url);
			while (true) {
				// transport.defaultHeaders.set("If-None-Match",);
				this.postLink = userFeed.getPostLink();
				if (userFeed.docs != null) {
					docs.addAll(userFeed.docs);
				}
				String nextLink = userFeed.getNextLink();
				if (nextLink == null) {
					break;
				}
			}
			return docs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void executeRefreshDocs() {

		docs.clear();
		dlg_refresh = ProgressDialog.show(this, "Please waiting...", "获取中...",
				true);
		new DocsRetriveThread().start();

	}

	// 检索线程
	private class DocsRetriveThread extends Thread {
		public void run() {
			try {
				docs = getDocsRoot(false);

			} catch (Exception e) {
				e.printStackTrace();
			}

			int numDocs = docs.size();
			DocsNames_tmp = new String[numDocs];
			for (int i = 0; i < numDocs; i++) {
				DocsNames_tmp[i] = docs.get(i).title;

			}

			docsHandler.sendEmptyMessage(0);
		}
	}

	private Handler docsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dlg_refresh.dismiss();
			setMyAdapter();
		}
	};

	private void setMyAdapter() {
		getListView().setAdapter(
				new ArrayAdapter<String>(DocsControl.this,
						android.R.layout.simple_list_item_1, DocsNames_tmp));
	}

	// ///////////////////////////////////////////////////////

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());

		getListView().setTextFilterEnabled(true);
		executeRefreshDocs();

	}
    
//   private static ProgressDialog dlg_tmp ;
   
    
	public static void uploadFile(String path) {
		// 弹出Docs文件列表对话框
		new DocsControl();
		try {
			File file = new File(path);
			HttpRequest request = transport.buildPostRequest();
			request.url = DocsUrl
					.fromRelativePath("/feeds/default/private/full");
			headers.setSlugFromFileName(file.getName());
			request.headers = headers;
			// 定义Content
			InputStreamContent content = new InputStreamContent(); // 用的是流
			content.inputStream = new FileInputStream(path);
			content.type = "text/plain";
			content.length = file.length();
			request.content = content;
			request.execute().ignore();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {
		createFolder();
	}

	// 新建文件夹
	public void createFolder() {
		DocsEntry folders = new DocsEntry();
		folders.access = "private";
		folders.title = "New Folder" + new DateTime(new Date());

		HttpRequest request = transport.buildPostRequest();
		request.url = DocsUrl.fromRelativePath("/feeds/default/private/full");

		request.headers = headers;
		// InputStreamContent content = new InputStreamContent(); //用的是流

		try {
			AtomContent content = new AtomContent();
			content.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
			content.entry = folders;
			request.content = content;
			request.execute().ignore();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// .parseAs();
		executeRefreshDocs();

	}

	// 下载，还原
	public static void downloadFile() {

	}
}
