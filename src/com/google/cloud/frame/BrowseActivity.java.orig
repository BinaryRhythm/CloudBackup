package com.google.cloud.frame;

<<<<<<< HEAD
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
=======
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.cloud.docs.DocsControl;
import com.google.cloud.mobile.Mobile;
import com.google.cloud.picasa.PicasaControl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
>>>>>>> dd48d6e52d812ee38945a788183d037f88d3054b
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
<<<<<<< HEAD
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.cloud.docs.DocsControl;
import com.google.cloud.mobile.Mobile;
import com.google.cloud.picasa.PicasaControl;

public class BrowseActivity extends TabActivity {

	private final static String TOKEN_MSG = ""; // 用于存放所获取的Token.
	private final static String Str_Token = ""; // token字符串
	private final static String ACCOUNTS = "AccountMsg";

	public String getToken() {
		return this.Str_Token;
	}

=======
import android.widget.ListAdapter;
import android.widget.TabHost;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BrowseActivity extends TabActivity {
>>>>>>> dd48d6e52d812ee38945a788183d037f88d3054b
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.browsetab);
		// this.requestWindowFeature(getWindow().FEATURE_NO_TITLE);

		TabHost choiceTab = getTabHost();
		choiceTab.setBackgroundColor(Color.BLUE);
		choiceTab.bringToFront();

		TabHost.TabSpec spec;
		Intent intent;
		Bundle bundle;

		/* 添加标签和标签的活动对象 */

		/* 本机 */
		intent = new Intent().setClass(this, Mobile.class);
		bundle = new Bundle();
		bundle.putString("choice", "Native");
		intent.putExtras(bundle);
		spec = choiceTab
				.newTabSpec("Native")
				.setIndicator("Native",
						this.getResources().getDrawable(R.drawable.mobile))
				.setContent(intent);
		choiceTab.addTab(spec);

		/* SD Card */
		intent = new Intent().setClass(this, Mobile.class);
		bundle = new Bundle();
		bundle.putString("choice", "SDCard");
		intent.putExtras(bundle);

		spec = choiceTab
				.newTabSpec("SD Card")
				.setIndicator("SD Card",
						this.getResources().getDrawable(R.drawable.card))
				.setContent(intent);
		choiceTab.addTab(spec);

		/* Picasa */
		intent = new Intent().setClass(this, PicasaControl.class);
		spec = choiceTab
				.newTabSpec("Picasa")
<<<<<<< HEAD
				.setIndicator("Picasa",
						this.getResources().getDrawable(R.drawable.picasa_icon))
=======
				.setIndicator(
						"Picasa",
						this.getResources().getDrawable(
								R.drawable.picasa_icon))
>>>>>>> dd48d6e52d812ee38945a788183d037f88d3054b
				.setContent(intent);
		choiceTab.addTab(spec);

		/* Docs */
		intent = new Intent().setClass(this, DocsControl.class);
		spec = choiceTab
				.newTabSpec("Docs")
<<<<<<< HEAD
				.setIndicator("Docs",
						this.getResources().getDrawable(R.drawable.docs_icon))
				.setContent(intent);
		choiceTab.addTab(spec);

		/* 显示对话框 */

		onCreateDialog(1, null).show();

	}

	/* 弹出账户对话框 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		switch (id) {
		case 1:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			/* 配置对话框相关信息 */
			builder.setTitle("Select a Google account");
			final Account[] accounts = AccountManager.get(this)
					.getAccountsByType("com.google"); // 列出所有谷歌账号
			final int size = accounts.length;

			String[] names = new String[size + 1];

			names[0] = "New Account";

			for (int i = 1; i < (size + 1); i++) {
				names[i] = accounts[i - 1].name;
			}
			// 列出列表
			builder.setItems(names, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 响应单击的账户
					/* 单击New Account */
					if (which == 0) {
						createAccount();
					} else {
						/* 单击账户 ，存储信息 */
						storeAccounts(accounts[which].toString());
					}
					// getAccount(AccountManager.get(BrowseActivity.this),accounts[which]);
				}
			});
			return builder.create(); // 返回创建的Dialog

			/* 未绑定账户，转到账户绑定页面 */

		default:
			return null;
		}

	}

	/* 存账户信息到XML */
	protected boolean storeAccounts(String user, String psd) {
		/* 把用户名和密码存到本地的XML中 */
		SharedPreferences setting = getSharedPreferences(ACCOUNTS, MODE_PRIVATE);
		SharedPreferences.Editor editor = setting.edit();
		/* 把用户名和密码存入SharedPreferences中 */
		editor.putString("accountName", user);
		editor.putString("password", psd);
		editor.commit();
		return true;
	}

	protected boolean storeAccounts(String user) {
		/* 把用户名和密码存到本地的XML中 */
		SharedPreferences setting = getSharedPreferences(ACCOUNTS, MODE_PRIVATE);
		SharedPreferences.Editor editor = setting.edit();
		/* 把用户名和密码存入SharedPreferences中 */
		editor.putString("accountName", user);

		editor.commit();
		return true;
	}

	/* 转到新账户界面 */
	protected void createAccount() {
		LayoutInflater myLayout = LayoutInflater.from(BrowseActivity.this);
		final View dlgView = myLayout.inflate(R.layout.clientreg, null);

		final EditText edit_text_user = (EditText) dlgView
				.findViewById(R.id.userId);
		final EditText edit_text_psd = (EditText) dlgView
				.findViewById(R.id.psd);

		Builder dlg = new AlertDialog.Builder(BrowseActivity.this)
				// 注册Dialog
				.setTitle("New Account")
				.setView(dlgView)
				// ///////////////////////////////////////////////////修改

				.setPositiveButton("Commit",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								boolean flags;

								String user = edit_text_user.getText()
										.toString();
								String psd = edit_text_psd.getText().toString();

								if (user != null && psd != null) {
									/* 判断是否为Gmail账户 , 循环到输入有效的账户信息 */
									do {
										flags = true;
										user = edit_text_user.getText()
												.toString();
										psd = edit_text_psd.getText()
												.toString();
										/* 有效 */
										if (user.substring(
												user.indexOf("@") + 1,
												user.length()).equals(
												"gmail.com")) { // /////////////////////仅仅局限了账户是Gmail
											storeAccounts(user, psd);
											flags = false;
										} else {
											/* 无效,清空 */
											Toast.makeText(
													BrowseActivity.this,
													"The Google Account is not valid.",
													Toast.LENGTH_SHORT).show();
											edit_text_psd.setText("");
											edit_text_user.setText("");
										}

									} while (flags);

								} else {
									Toast.makeText(BrowseActivity.this,
											"The Google Account is not valid.",
											Toast.LENGTH_SHORT).show();
								}

							}

						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (edit_text_psd != null
										|| edit_text_user != null) {
									edit_text_psd.setText("");
									edit_text_user.setText("");
								} else {
									finish();
								}
							}
						});

		dlg.create().show();

	}

	/* 判断退出键 */
=======
				.setIndicator(
						"Docs",
						this.getResources().getDrawable(
								R.drawable.docs_icon))
				.setContent(intent);
		choiceTab.addTab(spec);

	}

	/**/
>>>>>>> dd48d6e52d812ee38945a788183d037f88d3054b
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		/* 弹出退出确认对话框 */
		case KeyEvent.KEYCODE_BACK: {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.alert)
					// /////////////////////////////////换图片
					.setTitle(R.string.exit_alert_title)
					.setMessage(R.string.exit_message)
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							})
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									finish();
								}
							}).show();
			return true;
		}
<<<<<<< HEAD
=======

>>>>>>> dd48d6e52d812ee38945a788183d037f88d3054b
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

<<<<<<< HEAD
	/* 销毁程序 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
		// 或改用android.os.Process.killProcess(android.os.Process.myPid());
=======
	/**/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 或改用System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
>>>>>>> dd48d6e52d812ee38945a788183d037f88d3054b
	}

}