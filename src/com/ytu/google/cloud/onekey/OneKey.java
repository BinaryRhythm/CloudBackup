package com.ytu.google.cloud.onekey;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Settings.System;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ytu.google.cloud.docs.DocsControl;
import com.ytu.google.cloud.frame.R;
import com.ytu.google.cloud.models.DocsEntry;

public class OneKey extends Activity {

	private Button btn_settings;
	private Button btn_sms;

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// get system settings
			switch (v.getId()) {
			case R.id.btn_settings:
				backupSystemSettings();// 备份配置信息
				break;
			case R.id.btn_sms:
				backupSMS();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onekey);
		findViews();
		BindListeners();
	}

	protected void findViews() {
		btn_settings = (Button) findViewById(R.id.btn_settings);
		btn_sms = (Button) findViewById(R.id.btn_sms);
	}

	// 控件绑定
	protected void BindListeners() {
		btn_settings.setOnClickListener(listener);
		btn_sms.setOnClickListener(listener);
	}

	// 弹出对话框选择上传位置

	// 备份所有的系统设置信息
	protected void backupSystemSettings() {

		final DocsControl dc = new DocsControl();
		List<DocsEntry> docs = dc.getDocsRoot(true);
		if (docs.size() > 0) {
			int numDocs = docs.size();
			String[] DocsNames = new String[numDocs];
			for (int i = 0; i < numDocs; i++) {
				DocsNames[i] = docs.get(i).title;
			}

			Dialog select_dlg = new AlertDialog.Builder(OneKey.this)
					.setTitle("Select Location")
					.setIcon(android.R.drawable.arrow_down_float)
					.setItems(DocsNames, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
//                             DocsControl.uploadFile();
						}
					}).create();
			select_dlg.show();
		} else {
			new AlertDialog.Builder(OneKey.this)
					.setTitle("Alert")
					.setIcon(android.R.drawable.alert_light_frame)
					.setMessage(
							"The Docs root have no filefolder.Do you want to upload to root directory?")
					.setNegativeButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							})
					.setPositiveButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).create().show();

		}

	}

	protected void backupSMS() {

		// ContentResolver content = getContentResolver();
		// Cursor cur = content.query(CallLog.CONTENT_URI , projection, null,
		// null, null);

	}

	// ContentResolver content = getContentResolver();
	//
	// Cursor crusor =
	// content.query(Settings.System.CONTENT_URI, null, null,null,null);
	//
	// DocsControl.uploadFile();

	// final String[] CALL_PRJ = new String[] {
	// Settings.System.VOLUME_ALARM,Settings.System.MODE_RINGER;
	// //CallLog.Calls.DATE,CallLog.Calls.NUMBER
	// };
	//
	// String[] CALL_PRJ1 = new String[] { CallLog.Calls.DATE,
	// CallLog.Calls.NUMBER };
	//
	// ContentResolver content = getContentResolver();
	//
	// Settings.System settings = new Settings.System();
	//
	// Configuration cf = new Configuration();
	// cf.setToDefaults();
	//
	// System.getConfiguration(content, cf);
	//
	// Locale local = cf.locale;
	// String countryString = local.getCountry();
	// String language = local.getLanguage();
	// String t = Settings.System.getString(content,
	// Settings.System.AIRPLANE_MODE_ON);
	// Cursor cursor = content.query(Settings.System.CONTENT_URI, , null,
	// null, null);
	// Toast.makeText(OneKey.this, t, Toast.LENGTH_SHORT).show();

	// Cursor cursor1 = content.query(CallLog.Calls.CONTENT_URI, CALL_PRJ1,
	// null, null, null);
	// Toast.makeText(OneKey.this, String.valueOf(cursor.getCount()),
	// Toast.LENGTH_LONG).show();

	// setTitle(String.valueOf(cursor.getColumnCount()));//

	/*
	 * // Settings.System sys_set = new Settings.System();
	 * 
	 * String a; ContentResolver cr = getContentResolver();// new
	 * ContentResolver(this); Configuration cf = new Configuration();
	 * cf.setToDefaults();
	 * 
	 * this.startManagingCursor(null);
	 * 
	 * //if(Environment.getExternalStorageState().equals(Environment.
	 * MEDIA_MOUNTED)){ File path = Environment.getExternalStorageDirectory();
	 * StatFs statfs = new StatFs(path.getPath()); Toast.makeText(OneKey.this,
	 * path.getPath(), Toast.LENGTH_LONG).show();
	 * 
	 * //获取block的SIZE long blocSize = statfs.getBlockSize();
	 * Toast.makeText(OneKey.this,String.valueOf(blocSize),
	 * Toast.LENGTH_LONG).show();
	 * 
	 * //获取BLOCK数量
	 * 
	 * long totalBlocks = statfs.getBlockCount(); Toast.makeText(OneKey.this,
	 * String.valueOf(totalBlocks), Toast.LENGTH_LONG).show();
	 * 
	 * //空闲的Block的数量
	 * 
	 * long availaBlock = statfs.getAvailableBlocks();
	 * Toast.makeText(OneKey.this, String.valueOf(availaBlock),
	 * Toast.LENGTH_LONG).show();
	 * 
	 * 
	 * // }
	 * 
	 * // Intent launchSettingsIntent = new //
	 * Intent(Settings.ACTION_APPLICATION_SETTINGS); //
	 * startActivity(launchSettingsIntent);
	 * 
	 * // Intent intent=new Intent(); // intent.setComponent(new
	 * ComponentName("com.android.settings", //
	 * "com.android.settings.Settings")); // startActivity(intent);
	 * 
	 * // a = cr.getType(android.provider.Contacts.Phones.CONTENT_URI ); //
	 * Settings.System.getConfiguration(cr, cf);
	 * 
	 * // int a=0;
	 * 
	 * // Settings.System system = new Settings.System(); // Configuration
	 * configuration = new Configuration(); // configuration.setToDefaults(); //
	 * Settings.System.getConfiguration(this.getContentResolver(), //
	 * configuration); //
	 */

}
