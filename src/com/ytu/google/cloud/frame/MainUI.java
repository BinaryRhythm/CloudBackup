package com.ytu.google.cloud.frame;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.ytu.google.cloud.docs.DocsControl;
import com.ytu.google.cloud.mobile.Mobile;
import com.ytu.google.cloud.onekey.OneKey;
import com.ytu.google.cloud.picasa.PicasaControl;
import android.widget.EditText;

public class MainUI extends TabActivity {

	private static final String ACCOUNT_TYPE = "com.google";
	private static final int DLG_ACCOUNTS = 1;
	private static final int DLG_LOGIN = 2;

	private static final String PICASA_ACCOUNT_TYPE = "lh2";
	private static final String DOCS_ACCOUNT_TYPE = "writely";

	public static final String PICASA_TOKEN = "ptoken";
	public static final String DOCS_TOKEN = "dtoken";

	public static SharedPreferences pref;
	private String Dauth = null;
	private String Pauth = null;

	private ProgressDialog dlg;

	private Account[] accounts = null;
	private int REQUEST_CODE = 3;

	TabHost choiceTab;

	// private final Handler DlgHandler = new Handler() {
	//
	// @Override
	// public void handleMessage(final Message msg) {
	// dlg.dismiss();
	//
	// }
	//
	// };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);

		choiceTab = getTabHost();
//		choiceTab.setBackgroundColor(Color.GRAY);
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
				.newTabSpec("SDCard")
				.setIndicator(
						"SDCard",
						this.getResources()
								.getDrawable(R.drawable.ico_tab_card))
				.setContent(intent);
		choiceTab.addTab(spec);
		/* 快捷方式 */
		intent = new Intent().setClass(this, OneKey.class);
		spec = choiceTab
				.newTabSpec("OneKey")
				.setIndicator(
						"One Key",
						this.getResources().getDrawable(
								R.drawable.ico_tab_onekey)).setContent(intent);
		choiceTab.addTab(spec);
		/* Picasa */
		intent = new Intent().setClass(this, PicasaControl.class);
		spec = choiceTab
				.newTabSpec("Picasa")
				.setIndicator(
						"Picasa",
						this.getResources().getDrawable(
								R.drawable.ico_tab_picasa)).setContent(intent);
		choiceTab.addTab(spec);
		/* Docs */
		intent = new Intent().setClass(this, DocsControl.class);
		spec = choiceTab
				.newTabSpec("Docs")
				.setIndicator(
						"Docs",
						this.getResources()
								.getDrawable(R.drawable.ico_tab_docs))
				.setContent(intent);
		choiceTab.addTab(spec);

		accounts = getBindAccounts(ACCOUNT_TYPE);

		accounts = null;
		if (accounts != null) {
			showDialog(DLG_ACCOUNTS);
		} else {
			showDialog(DLG_LOGIN);
		}
	}

	/* 获取Auth，使用多线程 */
	public String getPToken(final Account account)
			throws OperationCanceledException, AuthenticatorException {
	    AccountManager managerP = AccountManager.get(this);
//		final String name = account.name;

		try {
			Pauth = managerP
					.getAuthToken(account, PICASA_ACCOUNT_TYPE, null,
							MainUI.this, null, null).getResult()
					.getString(AccountManager.KEY_AUTHTOKEN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Pauth;
	}

	public String getDToken(final Account account)
			throws OperationCanceledException, AuthenticatorException {
		AccountManager managerD = AccountManager.get(this);
//        final String name = account.name;
		/* 新开个线程获取auth */
		try {
			Dauth = managerD.getAuthToken(account,DOCS_ACCOUNT_TYPE,null ,MainUI.this, null,null)
			.getResult().getString(AccountManager.KEY_AUTHTOKEN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Dauth;
	}

	/* 获取系统绑定账户 */
	public Account[] getBindAccounts(String type) {

		AccountManager manager = AccountManager.get(this);
		Account[] accounts = manager.getAccountsByType(type);
		return accounts;

	}

	/* 存储获取到得Token */
	public boolean storeToken(String keys, String auth) {
		pref = getSharedPreferences("AUTHMSG", MODE_PRIVATE);
	    pref.edit().remove(keys);
		if (auth != null) {
			pref.edit().putString(keys, auth).commit();
			return true;
		}
		return false;
	}

	/* 账户选择对话框的创建 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {

		switch (id) {
		case DLG_ACCOUNTS: {
			int len = accounts.length;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String[] names = new String[len];
			for (int i = 0; i < len; i++) {
				names[i] = accounts[i].name;
			}
			builder.setTitle("Please Select a Account")
					.setIcon(android.R.drawable.arrow_down_float)
					.setItems(names, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 获取选择的Token存入XML
							// Toast.makeText(MainUI.this,accounts[which].name,Toast.LENGTH_SHORT).show();
							try {
								// ///doc
								if (storeToken(MainUI.PICASA_TOKEN,
										getPToken(accounts[which]))) {
									Toast.makeText(MainUI.this,
											"Login Picasa success!",
											Toast.LENGTH_SHORT).show();

								} else {
									Toast.makeText(MainUI.this,
											"Login Picasa failed!",
											Toast.LENGTH_SHORT).show();
								}
								// ////////picasa
								if (storeToken(MainUI.DOCS_TOKEN,
										getDToken(accounts[which]))) {
									Toast.makeText(MainUI.this,
											"Login Docs success!",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(MainUI.this,
											"Login Docs failed!",
											Toast.LENGTH_SHORT).show();
								}

							} catch (OperationCanceledException e) {
								// TODO Auto-generated catch
								// block
								e.printStackTrace();
							} catch (AuthenticatorException e) {
								// TODO Auto-generated catch
								// block
								e.printStackTrace();
							}
						}
					});
			return builder.create();
		}
		case DLG_LOGIN: {
			LayoutInflater inflater = LayoutInflater.from(this);
			final View textEntryView = inflater.inflate(R.layout.login, null);

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.alert_dark_frame);
			builder.setTitle("Google账户登陆");
			builder.setView(textEntryView);

			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							final ProgressDialog dlg_wait;
							dlg_wait = ProgressDialog.show(MainUI.this,
									"请等待...", "登录中...", true);
							new Thread() {
								public void run() {
									try {
										// 获取Token，并存储
										String user = ((EditText) textEntryView
												.findViewById(R.id.user))
												.getText().toString().trim();
										String psd = ((EditText) textEntryView
												.findViewById(R.id.psd))
												.getText().toString().trim();

										storeToken(
												MainUI.PICASA_TOKEN,
												Login(user, psd,
														PICASA_ACCOUNT_TYPE));

										storeToken(
												MainUI.DOCS_TOKEN,
												Login(user, psd,
														DOCS_ACCOUNT_TYPE));

									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										dlg_wait.dismiss();
									}
								}
							}.start();
						}
					});
			builder.create().show();
		}

		default:
			return null;
		}
	}

	/* 登陆获取Token */
	public String Login(String user, String psd, String type) {

		ClientLogin clientLogin;
		ClientLogin.Response response;

		clientLogin = new ClientLogin();
		response = new ClientLogin.Response();

		clientLogin.accountType = "GOOGLE";
		clientLogin.applicationName = "CloudBackup-YTU-1.0";

		clientLogin.authTokenType = type;

		clientLogin.username = user;
		clientLogin.password = psd;

		try {
			response = clientLogin.authenticate();
			return response.auth;
		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}