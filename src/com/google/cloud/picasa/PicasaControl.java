package com.google.cloud.picasa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.api.client.apache.ApacheHttpTransport;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.Base64;
import com.google.api.client.util.DateTime;
import com.google.api.client.xml.atom.AtomParser;
import com.google.cloud.clientlogin.LoginControl;
import com.google.cloud.frame.BrowseActivity;
import com.google.cloud.frame.R;
import com.google.cloud.models.picasa.AlbumEntry;
import com.google.cloud.models.picasa.AlbumFeed;
import com.google.cloud.models.picasa.PhotoEntry;
import com.google.cloud.models.picasa.PicasaUrl;
import com.google.cloud.models.picasa.UserFeed;
import com.google.cloud.models.picasa.Util;

public class PicasaControl extends ListActivity implements LoginControl {

	// public static final String PREF = "MyPrefs";
	//
	// private static final String AUTH_TOKEN_TYPE = "lh2";
	// private static final String TAG = "PicasaControl";
	//
	// private static final int MENU_ADD = Menu.FIRST;
	// private static final int MENU_ACCOUNTS = MENU_ADD + 1;
	//
	// private static final int CONTEXT_EDIT = 0;
	// private static final int CONTEXT_DELETE = 1;
	// private static final int CONTEXT_LOGGING = 2;
	//
	// private static final int REQUEST_AUTHENTICATE = 0;
	//
	// private static final int DIALOG_ACCOUNTS = 0;

	public static HttpTransport transport;
	private String authToken;// /////取值
	private String postLink;
	private GoogleHeaders headers;
	
	HttpResponse response;

	private final List<AlbumEntry> albums = new ArrayList<AlbumEntry>();// /相册实体

	private final List<PhotoEntry> photos = new ArrayList<PhotoEntry>();

	static SendData sendData;// /发送的数据

	/* 构造函数中初始化Transport，并加入相关的head */
	public PicasaControl() {

		HttpTransport.setLowLevelHttpTransport(ApacheHttpTransport.INSTANCE);
		transport = GoogleTransport.create();
		// ////添加必要部分的header
		headers = (GoogleHeaders) transport.defaultHeaders;
		headers.setApplicationName("CloudBackup-YTU-V1.0");
		headers.gdataVersion = "2";

		AtomParser parser = new AtomParser();
		// //为解析器设置命名空间
		parser.namespaceDictionary = Util.NAMESPACE_DICTIONARY;// //1.1版本弃用
		transport.addParser(parser);

	}

	/* 所发送的Data的类 */
	public static class SendData {

		String fileName; // //////文件名
		Uri uri; // //////统一资源标识符
		String contentType;// /////////////////////////////////
		long contentLength;// /记录发送数据的长度

		SendData(Intent intent, ContentResolver contentResolver) {
			// ///获取当前Intent的Bundle的Extras
			Bundle extras = intent.getExtras();
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				// /////////////////////////得到uri
				Uri uri = this.uri = (Uri) extras
						.getParcelable(Intent.EXTRA_STREAM);

				String scheme = uri.getScheme();

				if (scheme.equals("content")) {
					Cursor cursor = contentResolver.query(uri, null, null,
							null, null);
					cursor.moveToFirst();

					this.fileName = cursor.getString(cursor
							.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME));
					this.contentType = intent.getType(); // /////////////////////////////////取到的值
					this.contentLength = cursor.getLong(cursor
							.getColumnIndexOrThrow(Images.Media.SIZE));
				}
			}
		}
	}

	// //////为所有的transport加上auth header
	private void authenticatedClientLogin(String auth) {
		this.authToken = auth;
		
		((GoogleHeaders) transport.defaultHeaders).setGoogleLogin(authToken);
		authenticated();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_AUTHENTICATE: // ///表示启动的startActivityForResult()
			if (resultCode == RESULT_OK) {
				gotAccount(false);
			} else {
				showDialog(DIALOG_ACCOUNTS);
			}
			break;
		}
	}
	/* 一运行程序即进行账户验证,必须先登录Google账户才能使用 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select a Google account");
		final Account[] accounts = AccountManager.get(this).getAccountsByType(
				"com.google"); // 列出所有谷歌账号
		final int size = accounts.length;
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = accounts[i].name;
		}
		// 列出列表
		builder.setItems(names, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 响应单击的账户
//				
				getAccount(AccountManager.get(PicasaControl.this),accounts[which]);
			}
		});
		return builder.create(); // 返回创建的Dialog

	}

	private void getAccount(final AccountManager manager, final Account account) {
		// 多线程获取账号信息，并存入Pref中
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accountName", account.name).commit();//存入用户名
		

//		try {
//			Bundle bundle = manager.getAuthToken(account, AUTH_TOKEN_TYPE,
//					true, null, null).getResult();
//			if (bundle.containsKey(AccountManager.KEY_INTENT)) {
//				Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
//				int flags = intent.getFlags();
//				flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
//				intent.setFlags(flags);
//				startActivityForResult(intent, REQUEST_AUTHENTICATE);
//			} else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
//				authenticatedClientLogin(bundle
//						.getString(AccountManager.KEY_AUTHTOKEN));
//			}
//		} catch (Exception e) {
//			handleException(e);
//			return;
//		}
// //////////////////////多线程
		
		new Thread() {
		  @Override public void run() {
		try { 
				////是UI Thread 直接运行，否则放入UI Thread 队列		  
		  final Bundle bundle = manager.getAuthToken(account, AUTH_TOKEN_TYPE, true, null,null).getResult(); 
				  
		  runOnUiThread(new Runnable() {
		        public void run() { 
		        	try { 
		        		if (bundle.containsKey(AccountManager.KEY_INTENT)) { 
		        			Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT); 
		        			int flags = intent.getFlags(); flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;////??
		                    intent.setFlags(flags); ////启动一个新的Intent
		                    startActivityForResult(intent, REQUEST_AUTHENTICATE); //为以上设置返回处理函数 
		                    
		        		}else if (bundle .containsKey(AccountManager.KEY_AUTHTOKEN)){
		        			
		                      authenticatedClientLogin(bundle.getString(AccountManager.KEY_AUTHTOKEN));
		        		
		        		}
		        		   
		              } catch (Exception e) {
		                    handleException(e);
		           }
                        } 
		        	});
		  
		  } catch (Exception e) {
		            handleException(e);
		  }
		 }
	}.start();
		
	}
	

	private void gotAccount(boolean token) {
		// SharedPreference
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		String accountName = settings.getString("accountName", null);
		/* 如果XML中账户名为空，则从系统中获取绑定的账户 */
		if (accountName != null) {
			AccountManager manager = AccountManager.get(this);
			Account[] accounts = manager.getAccountsByType("com.google");
			int size = accounts.length;
			for (int i = 0; i < size; i++) {
				Account account = accounts[i];
				if (accountName.equals(account.name)) {
					if (token) {
						manager.invalidateAuthToken("com.google",
								this.authToken);
					}
					getAccount(manager, account);
					return;
				}
			}
		}
		showDialog(DIALOG_ACCOUNTS);
	}

	// 刷新相册
	private void executeRefreshAlbums() {

		ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		String[] albumNames;
		List<AlbumEntry> albums = this.albums;
		albums.clear();

		try {

			PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
			// page through results
			while (true) {
				UserFeed userFeed = UserFeed.executeGet(transport, url);
				/*
				 * // 向地址发送request UserFeed userFeed; HttpRequest request =
				 * transport.buildGetRequest(); request.url = url; userFeed =
				 * request.execute().parseAs(UserFeed.class);
				 */
				this.postLink = userFeed.getPostLink();

				/* 把所有相册对象都加到albums中 */
				if (userFeed.albums != null) {
					albums.addAll(userFeed.albums);

				}
				String nextLink = userFeed.getNextLink();
				if (nextLink == null) {
					break;
				}
			}
			int numAlbums = albums.size();
			/* 写入Map中 */

			for (int t = 0; t < numAlbums; t++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("Folder", R.drawable.file);
				item.put("AlbumName", albums.get(t).entry_title);
				item.put("PhotoNum", albums.get(t).numphotos);
				items.add(item);
			}

		} catch (IOException e) {
			handleException(e);
			albumNames = new String[] { e.getMessage() };
			albums.clear();
		}

		SimpleAdapter adapter = new SimpleAdapter(this, items,
				R.layout.list_albums, new String[] { "Folder", "AlbumName",
						"PhotoNum" }, new int[] { R.id.img_album,
						R.id.textview_album_name, R.id.textview_photo_num });
		setListAdapter(adapter);

	}

	// 登录设置// 获取、设置当前的登录状态,并存储
	private void setLogging(boolean logging) throws MissingResourceException {

		Logger.getLogger("com.google.cloud.client").setLevel(
				logging ? Level.CONFIG : Level.OFF);
		SharedPreferences settings = getSharedPreferences(PREF, 0);

		boolean currentSetting = settings.getBoolean("logging", false);
		if (currentSetting != logging) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("logging", logging);
			editor.commit();
		}
	}

	/*把图片转成二进制字节流*/
	public byte[] imgToBinary(String filePath) {
		// Bitmap img = Bitmap.createBitmap(src);
		// BitmapFactory.decodeByteArray(data, 0,data.length);

		FileInputStream in = null;
		try {
			in = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int n = 512;
		StringBuffer bs = new StringBuffer(20480);
		byte buffer[] = new byte[n];
		try {
			while ((in.read(buffer, 0, n) != -1) && (n > 0)) {
				bs.append(new String(buffer));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bs.toString().getBytes();
	}

	// 处理异常
	private void handleException(Exception e) {
		e.printStackTrace();

		if (e instanceof HttpResponseException) {
			int statusCode = ((HttpResponseException) e).response.statusCode;
			if (statusCode == 401 || statusCode == 403) {
				gotAccount(true);
			}
			return;
		}
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		if (settings.getBoolean("logging", false)) {
			if (e instanceof HttpResponseException) {
				try {
					Log.e(TAG, ((HttpResponseException) e).response
							.parseAsString());
				} catch (IOException parseException) {
					parseException.printStackTrace();
				}
			}
			Log.e(TAG, e.getMessage(), e);
		}

		/*
		 * SharedPreferences settings = getSharedPreferences(PREF, 0); boolean
		 * log = settings.getBoolean("logging", false); if (e instanceof
		 * HttpResponseException) { HttpResponse response =
		 * ((HttpResponseException) e).response; int statusCode =
		 * response.statusCode; try { response.ignore(); } catch (IOException
		 * e1) { e1.printStackTrace(); } if (statusCode == 401 || statusCode ==
		 * 403) { gotAccount(true); return; } if (log) { try { Log.e(TAG,
		 * response.parseAsString()); } catch (IOException parseException) {
		 * parseException.printStackTrace(); } } } if (log) { Log.e(TAG,
		 * e.getMessage(), e); }
		 */
	}

	/////////////////////////////////////////////////////////可不要???
	private void authenticated() {
		// /post消息,并处理request
		if (sendData != null) {
			try {
				if (sendData.fileName != null) {
					boolean success = false;
					try {
						HttpRequest request = transport.buildPostRequest();
						// 建立url
						request.url = PicasaUrl
								.relativeToRoot("feed/api/user/default/albumid/default");
						((GoogleHeaders) request.headers)
								.setSlugFromFileName(sendData.fileName);

						InputStreamContent content = new InputStreamContent();

						content.inputStream = this.getContentResolver()
								.openInputStream(sendData.uri);
						content.type = sendData.contentType;
						content.length = sendData.contentLength;

						request.content = content;
						request.execute().ignore();

						success = true;
					} catch (IOException e) {
						handleException(e);
					}
					/* 显示content的条目内容 */
					setListAdapter(new ArrayAdapter<String>(this,
							android.R.layout.simple_list_item_1,
							new String[] { success ? "OK" : "ERROR" }));// /??
				}
			} finally {
				sendData = null;
			}
		} else {
			executeRefreshAlbums();
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings = getSharedPreferences(PREF, 0);
		setLogging(settings.getBoolean("logging", false));// /将是否登陆设为否

		/* 是否允许使用Text 过滤,在Listview前面显示一透明的Text，用于输入快速查找 */
		this.getListView().setTextFilterEnabled(true);

		registerForContextMenu(getListView());// /注册上下文菜单

		Intent intent = getIntent();

		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			sendData = new SendData(intent, getContentResolver()); // //？？
		} else if (Intent.ACTION_MAIN.equals(getIntent().getAction())) {
			sendData = null;// /////////////////////////////执行此步
		}
		gotAccount(false);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ACCOUNTS:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select a Google account");
			final AccountManager manager = AccountManager.get(this);
			final Account[] accounts = manager.getAccountsByType("com.google");
			final int size = accounts.length;
			String[] names = new String[size];

			for (int i = 0; i < size; i++) {
				names[i] = accounts[i].name;
			}
			// ///账户列表
			builder.setItems(names, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					getAccount(manager, accounts[which]);
				}
			});
			return builder.create();
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ADD, 0, "New album");
		menu.add(0, MENU_ACCOUNTS, 0, "Switch Account");
		return true;

	}

	/* 照片上传 */
	public void upload(String path) {

		// /sdcard/avatar.jpg
		byte[] bytes1 = imgToBinary("/sdcard/avatar.jpg");
		/* 转base64 */
		String imgbase64 = new String(Base64.encode(bytes1));
		/*
		 * 1... POST Content-Type: multipart/related; boundary="END_OF_PART"
		 * Content-Length: 423478347 MIME-version: 1.0
		 * 
		 * Media multipart posting --END_OF_PART Content-Type:
		 * application/atom+xml
		 * 
		 * <entry xmlns='http://www.w3.org/2005/Atom'>
		 * <title>plz-to-love-realcat.jpg</title> <summary>Real cat wants
		 * attention too.</summary> <category
		 * scheme="http://schemas.google.com/g/2005#kind"
		 * term="http://schemas.google.com/photos/2007#photo"/> </entry>
		 * --END_OF_PART Content-Type: image/jpeg
		 * 
		 * ...binary image data... --END_OF_PART--
		 * 
		 * 2..... 
		 * Content-Type: image/jpeg Content-Length: 47899 
		 * Slug: plz-to-love-realcat.jpg
		 * 
		 * ...binary image data goes here...
		 */
		// BrowseActivity.
		/*
		 * HttpRequest request = transport.buildPostRequest(); request.setUrl(
		 * "https://picasaweb.google.com/data/feed/api/user/default/albumid/" +
		 * albums.get(1).album_id); request.headers.contentLength =
		 * String.valueOf(bytes1.length); request.headers.contentType =
		 * "image/jpeg";
		 */

		HttpClient client = new DefaultHttpClient();
		
		HttpPost post = new HttpPost("https://picasaweb.google.com/data/feed/api/user/lp88513/albumid/"
						+ albums.get(0).album_id);
		String ttt = albums.get(0).album_id;
		
		FileEntity tmp = null;
		String ret = null;

		client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.RFC_2109);

		tmp = new FileEntity(new File("/sdcard/avatar.jpg"), "utf-8");
		post.setEntity(tmp);
		post.setHeader("Content-Type","image/jpeg");
//		post.setHeader("Content-Length", String.valueOf(bytes1.length));
//		post.setHeader((Header) headers);
		post.setHeader("Authorization","GoogleLogin auth="+authToken);

		try {
			 response = client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		// try {
		// File imgFile =
		// InputStream is = new FileInputStream(imgFile);
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

	}

	/* 菜单的选择响应 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD: // ///////增加相册
			/*
			 * AlbumEntry album = new AlbumEntry(); album.access = "private";
			 * album.entry_title = "New Album" + new DateTime(new Date()); try {
			 * AlbumEntry.executeInsert(transport, album, this.postLink); }
			 * catch (IOException e) { handleException(e); }
			 */
			/*
			 * POST
			 * https://picasaweb.google.com/data/feed/api/user/userID/albumid
			 * /albumID Content-Type: image/jpeg Content-Length: 47899 Slug:
			 * plz-to-love-realcat.jpg Binary img
			 */

			/* 新建照片 */

			upload("/sdcard/avatar.jpg");

			executeRefreshAlbums();
			return true;
		case MENU_ACCOUNTS:// ///换账户
			showDialog(DIALOG_ACCOUNTS);
			return true;
		}

		return false;

	}

	/* 显示某个相册中的照片 */
	public void displayPhotos(long id) {

		String[] photoNames;
		List<PhotoEntry> photos = this.photos;
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
			photoNames[i] = photos.get(i).entry_title;
		}

		// 把相册信息赋给photos

		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, photoNames));
		setTitle(String.valueOf(id));
	}

	/* 响应单击相册的方法 */
	@Override
	public void onListItemClick(ListView listview, View v, int position, long id) {
		super.onListItemClick(listview, v, position, id);

		/* 发送request，请求浏览相册内容 */
		/*
		 * String[] albumNames; List<AlbumEntry> albums = this.albums;
		 * albums.clear(); GET
		 * http://picasaweb.com/data/feed/api/user/userID/albumid/albumID
		 */

		// 写个函数刷新每个相册

		displayPhotos(id);

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

	/* 上下文菜单 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXT_EDIT, 0, "Update Title");
		menu.add(0, CONTEXT_DELETE, 0, "Delete");
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		boolean logging = settings.getBoolean("logging", false);
		menu.add(0, CONTEXT_LOGGING, 0, "Logging").setCheckable(true)
				.setChecked(logging);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		AlbumEntry album = albums.get((int) info.id);
		try {
			switch (item.getItemId()) {
			case CONTEXT_EDIT:
				AlbumEntry patchedAlbum = album.clone();
				patchedAlbum.entry_title = album.entry_title + " UPDATED "
						+ new DateTime(new Date());
				patchedAlbum.executePatchRelativeToOriginal(transport, album);
				executeRefreshAlbums();
				return true;
			case CONTEXT_DELETE:
				try {
					album.executeDelete(transport);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				executeRefreshAlbums();
				return true;
			case CONTEXT_LOGGING:
				SharedPreferences settings = getSharedPreferences(PREF, 0);
				boolean logging = settings.getBoolean("logging", false);
				setLogging(!logging);
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		} catch (IOException e) {
			handleException(e);
		}
		return false;
	}

	/*
	 * POSt
	 * https://picasaweb.google.com/data/feed/api/user/userID/albumid/albumID
	 * Content-Type: image/jpeg Content-Length: 47899
	 * Slug:plz-to-love-realcat.jpg
	 * 
	 * ...binary image data goes here... 图片转二进制
	 */
	public static void uploadPicBin() {
		HttpRequest request = transport.buildPostRequest();
		// HttpContent content = new HttpContent();
		// request.content = content;

	}

	public byte[] getAttachmentContent(String path) {
		try {
			// 创建文件输入流对象
			FileInputStream is = new FileInputStream(path); // 设定读取的字节数
			int n = 512;
			StringBuffer bs = new StringBuffer(20480);
			byte buffer[] = new byte[n]; // 读取输入流
			while ((is.read(buffer, 0, n) != -1) && (n > 0)) {
				bs.append(new String(buffer));
			} // 关闭输入流 is.close(); return bs.toString().getBytes();
		} catch (IOException ioe) {

		} catch (Exception e) {

		}
		return null;
	}

	/*
	 * 解码
	 * 
	 * /* 讲byte[]进行二进制转换成base64再转换成String
	 * 
	 * public String byteTobase64ToString(byte[] att) { return new
	 * String(Base64.encodeBase64(att)); }
	 * 
	 * //下面是直接把二进制流进行展示 private Bitmap Bytes2Bimap(byte[] b){ if(b.length!=0){
	 * return BitmapFactory.decodeByteArray(b, 0, b.length); } else { return
	 * null; } }
	 */

}
