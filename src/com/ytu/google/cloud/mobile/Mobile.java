package com.ytu.google.cloud.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.InputStreamContent;
import com.ytu.google.cloud.docs.DocsControl;
import com.ytu.google.cloud.frame.MainUI;
import com.ytu.google.cloud.frame.R;
import com.ytu.google.cloud.models.DocsUrl;
import com.ytu.google.cloud.models.PicasaFeed;
import com.ytu.google.cloud.models.PicasaUrl;

public class Mobile extends ListActivity {

	private List<FileOop> filesOop = new ArrayList<FileOop>();
	private File currentDir = new File("mnt/sdcard");

	private  ProgressDialog dlg_upload = null;

	// private Handler pHandler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getListView().setTextFilterEnabled(true);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String choice = bundle.getString("choice");

		if (choice.equals("Native"))
			browseSDCard();

		// Looper looper = Looper.getMainLooper();
		// looper.prepare();

	}

	// ///多线程模块-----------------------------------------------/////////////////

	public class DocsUploadThread extends Thread {

		String path;

		public DocsUploadThread(String path) {
			this.path = path;
		}

		Message msg = new Message();
		@Override
		public void run() {
			try{
			Looper.prepare();
			DocsControl.uploadFile(path);
			// DocsControl.uploadFile(file.getAbsoluteFile().toString());
			// pHandler.sendEmptyMessage(0);
			System.out.println("dddddddddddddddddddddddddddddddd");
			
			msg.what = 1;
			
			}catch (Exception e){
				e.printStackTrace();
			}
			pHandler.sendMessage(msg);
		}
	}

	  Handler pHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				dlg_upload.dismiss();
				break;
			}
		}
	};

	// ////////////End----------------------///////////

	/* 浏览存储卡目录 */
	protected void browseSDCard() {
		browseFile(new File("mnt/sdcard"));
	}

	/* 返回上一级 */
	protected void upLevel() {
		this.browseFile(this.currentDir.getParentFile());
	}

	/* 浏览指定文件夹 ,如果是文件则直接上传到默认文件夹 */
	@SuppressWarnings("static-access")
	protected void browseFile(final File file) {

		if (file.isDirectory()) {
			this.currentDir = file; // 在此改变了currentDir，使之总是 为当前的目录.
			fillList(file.listFiles());
		} else {
			/* 上传文件 */

			dlg_upload=ProgressDialog.show(this, "Please waiting...", "上传中...");

			// new Thread() {
			// @Override
			// public void run() {
			//
			// try {
			// Looper.prepare();
			// DocsControl.uploadFile(file.getAbsolutePath()
			// .toString());
			// Looper.loop();
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// dlg_upload.dismiss();
			// }
			// }
			// }.start();

			new DocsUploadThread(file.getAbsolutePath()).start();

			// new DocsControl.DocsUploadThread(file.getAbsolutePath()).start();
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

	/* 用文件（夹）数组填充列表 */
	@SuppressWarnings("unchecked")
	protected void fillList(File[] files) {
		/* 将该Activity的files列表清空 */
		this.filesOop.clear();

		Drawable currentIcon = null;

		/* 添加刷新当前目录的选项 */

		this.filesOop.add(new FileOop(getString(R.string.refresh),
				getResources().getDrawable(R.drawable.ico_listview_refresh)));

		/* 如果不是SDCard根目录则添加 返回上一级 目录项 */
		// String aa = this.currentDir.getParent();
		// String kk = this.currentDir.getPath();
		if (!this.currentDir.getPath().endsWith("sdcard")) {
			this.filesOop
					.add(new FileOop(getString(R.string.up_one_level),
							getResources().getDrawable(
									R.drawable.ico_listview_uplevel)));
		}
		/* Icon的设置 */
		for (File currentFile : files) {

			/* 如果是文件夹，设置文件夹图标 */
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(
						R.drawable.ico_listview_folder);
			} else {
				/* 如果是文件，判断文件类型，设置相应图标 */
				String fileName = currentFile.getName();
				/* 依据不同的文件后缀名来判断文件类型，设置不同图标 */

				if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingImage))) {/* 图片 */
					currentIcon = getResources().getDrawable(
							R.drawable.ico_listview_image);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingAudio))) {/* 音频 */
					currentIcon = getResources().getDrawable(
							R.drawable.ico_listview_music);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPackage))) {/* 压缩包 */
					currentIcon = getResources().getDrawable(
							R.drawable.ico_listview_archive);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingText))) {/* 文本 */
					currentIcon = getResources().getDrawable(
							R.drawable.ico_listview_text);
				} else {
					currentIcon = getResources().getDrawable(R.drawable.why);
				}

			}
			/* 只显示文件或文件夹的名字 */

			this.filesOop.add(new FileOop(currentFile.getName(), currentIcon));

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

			File clickedFile = new File(this.currentDir.getAbsolutePath() + "/"
					+ selected_name);
			browseFile(clickedFile);

		}

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
			browseSDCard();
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
		File root = this.getFilesDir();

		if (new File(root + "/hehe.txt").mkdir()) {
			Toast.makeText(Mobile.this, root.toString(), Toast.LENGTH_LONG)
					.show();
		}
	}

	/* 删除文件夹 */
	public void Delete() {
		browseFile(new File("/data/data/com.rss"));
	}

	/* 粘贴 */
	public void Paste() {

		File root = this.getFilesDir();
		Toast.makeText(Mobile.this, root.toString(), Toast.LENGTH_LONG).show();
		browseFile(root);
	}
	/**/

}
