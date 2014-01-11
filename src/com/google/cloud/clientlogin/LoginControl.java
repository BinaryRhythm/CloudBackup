package com.google.cloud.clientlogin;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.view.Menu;


public interface LoginControl{


    static final String PREF = "MyPrefs";

	static final String AUTH_TOKEN_TYPE = "lh2";
	static final String TAG = "PicasaControl";

	static final int MENU_ADD = Menu.FIRST;
	static final int MENU_ACCOUNTS = MENU_ADD + 1;

	static final int CONTEXT_EDIT = 0;
	static final int CONTEXT_DELETE = 1;
	static final int CONTEXT_LOGGING = 2;

	static final int REQUEST_AUTHENTICATE = 0;
	
	static final int DIALOG_ACCOUNTS = 0;
	
	
/*
	private void getAccount(final AccountManager manager, final Account account) {
		// 多线程获取账号信息，并存入Pref中
		SharedPreferences settings = getSharedPreferences(TOKEN_MSG, 0);
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
	
	*/


//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.clientlogin);
//
//		findViews();
//		Init();
//		
//		btn_ok.setOnClickListener(ltn);
//
//		
//	
//		
//
//	}

}