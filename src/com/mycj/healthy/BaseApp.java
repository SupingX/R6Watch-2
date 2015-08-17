package com.mycj.healthy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.log.BleLog;
import com.mycj.healthy.service.IncomingService;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.service.LiteBlueService.MyBinder;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.MessageUtil;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BaseApp extends Application {

	private LiteBlueService mLiteBlueService;
	private IncomingService mIncomingService;
	private static ArrayList<Activity> activities = new ArrayList<Activity>();

	private ServiceConnection liteConection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mLiteBlueService = ((MyBinder) service).getService();
			Log.e("BaseApp", "mLiteBlueService : " + mLiteBlueService);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mLiteBlueService = null;
		}
	};

//	private ServiceConnection incomingConection = new ServiceConnection() {
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			mIncomingService = ((IncomingService.MyBinder) service).getIncomingService();
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			mIncomingService = null;
//		}
//	};

	/**
	 * 短信监听
	 */
	private ContentObserver newMmsContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			int mNewSmsCount = MessageUtil.getNewSmsCount(getApplicationContext())+MessageUtil.getNewMmsCount(getApplicationContext());
			Log.e("mMissCallCount", "mNewSmsCount_______________________________" + mNewSmsCount);
			int phone = MessageUtil.readMissCall(getApplicationContext());
			doWriteToWatch(phone,mNewSmsCount);
			super.onChange(selfChange, uri);
			mHandle.removeCallbacks(task);
			mHandle.post(task);
			
		}

	};
	/**
	 * 电话监听
	 */
	private ContentObserver newCallContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			int mNewCallCount = MessageUtil.readMissCall(getApplicationContext());
			Log.e("mMissCallCount", "mNewCallCount_______________________________" + mNewCallCount);
			int msg = MessageUtil.getNewSmsCount(getApplicationContext())+MessageUtil.getNewMmsCount(getApplicationContext());
			doWriteToWatch(mNewCallCount,msg);
			super.onChange(selfChange, uri);
			mHandle.removeCallbacks(task);
			mHandle.post(task);
		}
		
	};
	

	/**
	 *
	 */
	private BroadcastReceiver mPhoneReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.i("BaseApp", "CallReceiver Start...");
			telephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			
		

		};
	};

	
	/**
	 * 电话状态监听
	 */
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			Log.v("BaseApp", "CallListener call state changed ---- [incomingNumber : " + incomingNumber + ",state : "+ state+"]");
//			String m = null;
//			// 如果当前状态为空闲,上次状态为响铃中的话,则认为是未接来电
//			if (lastetState == TelephonyManager.CALL_STATE_RINGING && state == TelephonyManager.CALL_STATE_IDLE) {
//				sendSmgWhenMissedCall(incomingNumber);
//			}
//			// 最后改变当前值
//			lastetState = state;
			
			if (state == TelephonyManager.CALL_STATE_RINGING) {
//				sendSmgWhenMissedCall(incomingNumber);
				int phone = MessageUtil.readMissCall(getApplicationContext())+1;
				int sms = MessageUtil.getNewMmsCount(getApplicationContext())+MessageUtil.getNewSmsCount(getApplicationContext());
				doWriteToWatch(phone, sms);
				mHandle.removeCallbacks(task);
				mHandle.post(task);
			}
			
		};
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Intent bindIntentForLiteBlue = new Intent(this, LiteBlueService.class);
		bindService(bindIntentForLiteBlue, liteConection, Context.BIND_AUTO_CREATE);

		registerReceiver(mPhoneReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
		 registerObserver();
	}
	
	
	private TelephonyManager telephony;
	private final static int DIFF = 4 * 1000; // 4秒运行一次
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			int mmsCount = MessageUtil.getNewMmsCount(getApplicationContext());
			int msmCount = MessageUtil.getNewSmsCount(getApplicationContext());
			int phoneCount = MessageUtil.readMissCall(getApplicationContext());
			
			Log.e("", "========================================phoneCount : " +phoneCount);
			if (mmsCount == 0 && msmCount == 0 && phoneCount == 0) {
				task.cancel();
				mHandle.removeCallbacks(task);
				doWriteToWatch(0, 0);
			} else {
					mHandle.sendEmptyMessage(1);
			}
		}
	};
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mHandle.postDelayed(task, DIFF);
				break;
			default:
				break;
			}
		};

	};
	
	protected void doWriteToWatch(int phoneCount, int msgCount) {
		boolean isOpen = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_INCOMING_ON_OFF, false);
		if (isOpen && mLiteBlueService.isConnetted()) {
			mLiteBlueService.writeValueToDevice(ProtoclData.toByteForPhoneMessageIncomingProtocl(phoneCount, msgCount));
		}
	}
	
	
	/**
	 * 注册监听短信
	 */
	private void registerObserver() {
		unregisterObserver();
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, newMmsContentObserver);
		 // 在服务创建的时候注册ContentObserver，之后就会一直存在  
        this.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, false, newCallContentObserver);  
	}

	private  void unregisterObserver() {
		try {
			if (newMmsContentObserver != null) {
				getContentResolver().unregisterContentObserver(newMmsContentObserver);
			}
			if (newCallContentObserver != null) {
				getContentResolver().unregisterContentObserver(newCallContentObserver);
			}
		} catch (Exception e) {
			Log.e("", "unregisterObserver fail");
		}
	}

	public IncomingService getIncomingService() {
		return this.mIncomingService;
	}

	/**
	 * 监听来电（不可用）
	 */
	private void register() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction("com.android.phone.NotificationMgr.MissedCall_intent");
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action != null && "com.android.phone.NotificationMgr.MissedCall_intent".equals(action)) {
					int mMissCallCount = intent.getExtras().getInt("MissedCallNumber");
					Log.e("mMissCallCount", "mMissCallCount__________________________" + mMissCallCount);

				}
			}
		}, filter);
	}




	public LiteBlueService getLiteBlueService() {
		return mLiteBlueService;
	}

	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	public static void finishActivity() {
		for (Activity activity : activities) {
			if (activity != null) {
				activity.finish();
			}
		}
//		Process.killProcess(Process.myPid());
//		System.exit(0);
	}

	@Override
	public void onTerminate() {
		mLiteBlueService.closeAll();
		mLiteBlueService.setCurrentState(null);
		unbindService(liteConection);
		mHandle.removeCallbacks(task);
		finishActivity();
		super.onTerminate();
	}

}
