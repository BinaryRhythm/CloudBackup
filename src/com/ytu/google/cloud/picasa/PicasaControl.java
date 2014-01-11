package com.ytu.google.cloud.picasa;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.accounts.AccountManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.api.client.apache.ApacheHttpTransport;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.xml.atom.AtomParser;
import com.ytu.google.cloud.frame.MainUI;
import com.ytu.google.cloud.models.AlbumEntry;
import com.ytu.google.cloud.models.AlbumFeed;
import com.ytu.google.cloud.models.PhotoEntry;
import com.ytu.google.cloud.models.PicasaFeed;
import com.ytu.google.cloud.models.PicasaUrl;
import com.ytu.google.cloud.models.Util;

public class PicasaControl extends ListActivity {

	private List<AlbumEntry> albums;
	private static HttpTransport transport;
	private String postLink;
	private List<PhotoEntry> photos;
	String[] AlbumNames_tmp = null;
	private ProgressDialog dlg_refresh;

	public PicasaControl() {
		albums = new ArrayList<AlbumEntry>();

		HttpTransport.setLowLevelHttpTransport(ApacheHttpTransport.INSTANCE);
		transport = GoogleTransport.create();
		GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
		headers.setApplicationName("CloudBackup-YTU-1.0");
		headers.gdataVersion = "2";
		SharedPreferences pref = MainUI.pref;
		// AccountManager.get(this).invalidateAuthToken("com.google",MainUI.PICASA_TOKEN);
		// headers.authenticate = pref.getString(MainUI.PICASA_TOKEN,null);
		headers.setGoogleLogin(pref.getString(MainUI.PICASA_TOKEN, null));
		// headers.setGoogleLogin(pref.getString(MainUI.PICASA_TOKEN, null));
		AtomParser parser = new AtomParser();
		parser.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
		transport.addParser(parser);

	}

	/*
	 * private void authenticatedLogin(String authToken) { this.authToken =
	 * authToken; ((GoogleHeaders))
	 * transport.defaultHeaders.setGoogleLogin(authToken); authenticated(); }
	 */

	/* Refresh the feed */
	protected void executeRefreshPicasa() {

		// final List<AlbumEntry> albums = this.albums;
		albums.clear();

		dlg_refresh = ProgressDialog.show(this, "Please waiting...", "获取中...",
				true);

		new PicasaRetriveThread().start();

	}

	// 检索多线程
	private class PicasaRetriveThread extends Thread {
		@Override
		public void run() {
			try {
				PicasaUrl url = PicasaUrl
						.relativeToRoot("feed/api/user/default");
				while (true) {

					// AccountManager.get(MainUI.class)
					// .invalidateAuthToken("com.google",MainUI.pref.getString(MainUI.PICASA_TOKEN,
					// null));
					PicasaFeed userFeed = PicasaFeed.executeGet(transport, url);
					// HttpRequest request = new HttpRequest(transport,);
					postLink = userFeed.getPostLink();
					if (userFeed.albums != null) {
						albums.addAll(userFeed.albums);
					}
					String nextLink = userFeed.getNextLink();
					if (nextLink == null) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			int numAlbums = albums.size();
			AlbumNames_tmp = new String[numAlbums];
			for (int i = 0; i < numAlbums; i++) {
				AlbumNames_tmp[i] = albums.get(i).title;
			}
			picasaHandler.sendEmptyMessage(0);
		}
	}

	private Handler picasaHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dlg_refresh.dismiss();
			setMyAdapter();
		}
	};

	private void setMyAdapter() {
		getListView().setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, AlbumNames_tmp));
	}

	/* show the photos in the album */
	public void displayPhotos(long id) {

		String[] photoNames;
		photos = new ArrayList<PhotoEntry>();
		photos.clear();
		while (true) {
			AlbumFeed albumfeed;
			String url = "feed/api/user/default/albumid/"
					+ albums.get((int) id).album_id;
			try {
				albumfeed = AlbumFeed.executeGet(transport,
						PicasaUrl.relativeToRoot(url));

				this.postLink = albumfeed.getPostLink();

				if (albumfeed.photos != null) {
					photos.addAll(albumfeed.photos);

				}
				String nextLink = albumfeed.getNextLink();
				if (nextLink == null) {
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		int numPhotos = photos.size();
		photoNames = new String[numPhotos];
		for (int i = 0; i < numPhotos; i++) {
			photoNames[i] = photos.get(i).title;
		}

		// 把相册信息赋给photos

		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, photoNames));
		setTitle(String.valueOf(id));
	}

	/* 响应单击相册或图片的方法 */
	@Override
	public void onListItemClick(ListView listview, View v, int position, long id) {
		super.onListItemClick(listview, v, position, id);

		if (this.getListView().getItemAtPosition(position) == albums
				.get(position)) {
			// 写个函数刷新每个相册
			displayPhotos(id);
		} else if (this.getListView().getItemAtPosition(position) == photos
				.get(position)) {
			// 显示图片
			// displayBigPhotos(position);
		}

		/*
		 * 删除相册的功能 AlbumEntry album = albums.get((int)id);
		 * 
		 * HttpRequest request = transport.buildDeleteRequest();
		 * request.setUrl(album.getEditLink());
		 * 
		 * request.headers.ifMatch = album.etag; //////设置ifMatch header （the
		 * original album's ETag value） try { request.execute().ignore(); }
		 * catch (IOException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 * 
		 * 
		 * executeRefreshAlbums();
		 */
		// UserFeed userFeed;
		// HttpRequest request = transport.buildGetRequest();
		// request.url =
		// PicasaUrl.relativeToRoot("feed/api/user/default/albumid");
		// try {
		// userFeed = request.execute().parseAs(UserFeed.class);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	// /**/
	// public void displayBigPhotos(int id) {
	//
	// URL aryURI = new URL(myImageURL[position]);
	// /* 打开连接 */
	// URLConnection conn = aryURI.openConnection();
	// conn.connect();
	// /* 转变为 InputStream */
	// InputStream is = conn.getInputStream();
	// /* 将InputStream转变为Bitmap */
	// Bitmap bm = BitmapFactory.decodeStream(is);
	// /* 关闭InputStream */
	// is.close();
	// /*添加图片*/
	// imageView.setImageBitmap(bm);
	//
	//
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setTextFilterEnabled(true);

		registerForContextMenu(getListView());

		executeRefreshPicasa();

	}

}
