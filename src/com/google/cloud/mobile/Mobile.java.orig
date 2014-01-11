package com.google.cloud.mobile;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.cloud.frame.BrowseActivity;
import com.google.cloud.frame.R;
import com.google.cloud.picasa.PicasaControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class Mobile extends ListActivity  {

	private List<FileOop> filesOop = new ArrayList<FileOop>();
	private File currentDir = new File("/");

	private PicasaControl picasaControl = new PicasaControl();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getListView().setTextFilterEnabled(true);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String choice = bundle.getString("choice");
		
		if (choice.equals("Native"))
			browseRoot();
		else
			browseFile(new File("/sdcard"));
	}

	/* 浏览系统根目录 */
	protected void browseRoot() {
		browseFile(new File("/"));
	}

	/* 返回上一级 */
	protected void upLevel() {
		if (this.currentDir.getParent() != null) {
			this.browseFile(this.currentDir.getParentFile());
		}
	}

	/* 浏览指定文件夹,如果为文件则打开 */
	protected void browseFile(final File file) {
		if (file.isDirectory()) {
			this.currentDir = file; // 在此改变了currentDir，使之总是 为当前的目录.
			fillList(file.listFiles());
		} else {
			/* 打开文件 */
//			openFile(file);
			
			/*打开选择界面，选择上传到的文件目录*/
//			Intent in = new Intent();
//			in.setClass(this,PicasaControl.class);
////			startActivity(in);
			
			
			
		}
	}
	
	/*
	 * 把图片的二进制代码转为图片 public void createFile(String name, String type, String
	 * body) { byte[] bodyBity = MessageFromServer.base64Tobyte(body); String
	 * path = null; try {
	 * 
	 * if (type.equalsIgnoreCase("JPG") || type.equalsIgnoreCase("PNG") ||
	 * type.equalsIgnoreCase("gpeg") || type.equalsIgnoreCase("gif") ||
	 * type.equalsIgnoreCase("bmp")) { path = "/sdcard/picture/"; }else { path =
	 * "/sdcard/other/"; } FileOutputStream fos = new FileOutputStream(path +
	 * name + "." + type); BufferedOutputStream bufOutputStream = new
	 * BufferedOutputStream(fos,20480); bufOutputStream.write(bodyBity);
	 * bufOutputStream.flush(); bufOutputStream.close(); } catch
	 * (FileNotFoundException e1) { Log.i("archermind",
	 * "--------------------------------------------------------" + e1); } catch
	 * (IOException e) { Log.i("archermind",
	 * "--------------------------------------------------------" + e); }
	 * 
	 * }
	 */
	public byte[] imgToBinary(String filePath){
//		Bitmap img = Bitmap.createBitmap(src);
//		BitmapFactory.decodeByteArray(data, 0,data.length);
		
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
			while ((in.read(buffer,0,n) != -1) && (n>0)) {
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

	/**/
	public void uploadPicBin() {
//		HttpRequest request = transport.buildPostRequest();
//		HttpContent content = new HttpContent();
//		request.content = content;
		
		
	}

	/* 浏览根目录 */
	protected void broeseRoot() {
		browseFile(currentDir);
	}

	/* 用文件（夹）数组填充列表 */
	@SuppressWarnings("unchecked")
	protected void fillList(File[] files) {
		/* 将该Activity的files列表清空 */
		this.filesOop.clear();

		Drawable currentIcon = null;

		/* 添加刷新当前目录的选项 */

		this.filesOop.add(new FileOop(getString(R.string.refresh),
				getResources().getDrawable(R.drawable.refresh)));

		/* 如果不是根目录则添加上一级目录项 */
		if (this.currentDir.getParent() != null)

			this.filesOop.add(new FileOop(getString(R.string.up_one_level),
					getResources().getDrawable(R.drawable.uponelevel)));

		/* Icon的设置 */
		for (File currentFile : files) {

			/* 如果是文件夹，设置文件夹图标 */
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.folder);
			} else {
				/* 如果是文件，判断文件类型，设置相应图标 */
				String fileName = currentFile.getName();
				/* 依据不同的文件后缀名来判断文件类型，设置不同图标 */

				if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingImage))) {/* 图片 */
					currentIcon = getResources().getDrawable(R.drawable.image);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingAudio))) {/* 音频 */
					currentIcon = getResources().getDrawable(R.drawable.audio);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPackage))) {/* 压缩包 */
					currentIcon = getResources()
							.getDrawable(R.drawable.archive);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingText))) {/* 文本 */
					currentIcon = getResources().getDrawable(R.drawable.text);
				}else{
					currentIcon = getResources().getDrawable(android.R.drawable.stat_notify_error);
				}

			}
			/* 只显示文件或文件夹的名字 */
			int currentPathStringLenght = this.currentDir.getAbsolutePath()
					.length();
			this.filesOop.add(new FileOop(currentFile.getAbsolutePath()
					.substring(currentPathStringLenght), currentIcon));

		}
		/* 把数据和ListView邦定 */
		// this.getListView().setListAdapter();
		Collections.sort(this.filesOop);
		DataAdapter data = new DataAdapter(this);
		data.setListItems(filesOop);
		this.setListAdapter(data);

	}

	/* 当单击某个item时 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String selected_name = this.filesOop.get(position).getFileName();
		
		/* 当单击的是刷新或返回上一级 */
		if (selected_name.equals(getResources().getString(R.string.refresh))) {
			browseFile(currentDir);
		} else if (selected_name.equals(getResources().getString(
				R.string.up_one_level))) {
			upLevel();
		} else {
			/* 浏览该文件夹下的内容 */
			File clickedFile = new File(this.currentDir.getAbsolutePath()
					+ selected_name);
//			browseFile(clickedFile);
			////////////////////////////////////////////////////////////////暂改为上传/////////////////////////////
			
			String path = this.currentDir.getAbsolutePath() + selected_name;/////////图片的绝对路径
			byte[] bytes = imgToBinary(path);
			
			//上传////
			/*POST
			 * https://picasaweb.google.com/data/feed/api/user/userID/albumid/albumID
			 * Content-Type: image/jpeg Content-Length: 47899 Slug:
			 * plz-to-love-realcat.jpg
			 * Binary img
			*/
//			transport 
//			HttpTransport transport = new HttpTransport();
			
//			GoogleHeaders headers =  (GoogleHeaders) transport.defaultHeaders; 
			
//			request.setUrl("https://picasaweb.google.com/data/feed/api/user/default/albumid/default");
//			HttpRequest request = transport.buildPostRequest();
			
			HttpTransport transport = PicasaControl.transport;
			
			HttpRequest request = transport.buildPostRequest();
			/*获取默认相册的ID*/
			
			PicasaControl.uploadPicBin();
//			BrowseActivity.
			
			
			request.setUrl("https://picasaweb.google.com/data/feed/api/user/default/albumid/default");
			
			request.headers.set("Content-Type", "image/jpeg");
			
//			String type = request.content.getType();
			
			
			request.headers.set("Slug", "dog");
			request.headers.set("Content",bytes);
//			request.headers.set("Content-Length",bytes.length);
		
			
//			HttpContent hc = new HttpContent();
			
//            request.content = ;
			try {
				request.execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}

	}

	/* 单击时打开文件 */
	protected void openFile(File aFile) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(aFile.getAbsolutePath());
		String fileName = file.getName();

		/**/
		if (checkEndsWithInStringArray(fileName,
				getResources().getStringArray(R.array.fileEndingImage))) {
			intent.setDataAndType(Uri.fromFile(file), "image/*");
		} else if (checkEndsWithInStringArray(fileName, getResources()
				.getStringArray(R.array.fileEndingAudio))) {
			intent.setDataAndType(Uri.fromFile(file), "audio/*");
		} else if (checkEndsWithInStringArray(fileName, getResources()
				.getStringArray(R.array.fileEndingVideo))) {
			intent.setDataAndType(Uri.fromFile(file), "video/*");
		}
		startActivity(intent);

	}

	/* 判断文件类型 */
	public boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	/* 创建菜单 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, 0, 0, "New Folder");
		menu.add(0, 1, 0, "Delete");
		menu.add(0, 2, 0, "Paste");
		menu.add(0, 3, 0, "Root");
		menu.add(0, 4, 0, "Back");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case 0:
			NewFolder();
			break;
		case 1:
			Delete();
			break;
		case 2:
			Paste();
			break;
		case 3:
			browseRoot();
			break;
		case 4:
			upLevel();
			break;
		default:
			break;

		}
		return false;
	}

	

	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	/* 新建文件夹 */
	public void NewFolder() {

	}

	/* 删除文件夹 */
	public void Delete() {

	}

	/* 粘贴 */
	public void Paste() {

	}
	/**/

}
