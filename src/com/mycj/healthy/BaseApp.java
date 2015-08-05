package com.mycj.healthy;

import java.util.ArrayList;
import java.util.HashMap;

import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.log.BleLog;
import com.mycj.healthy.service.IncomingService;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.service.LiteBlueService.MyBinder;
import com.mycj.healthy.util.Constant;
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
import android.os.Process;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BaseApp extends Application {

	private LiteBluetooth mLiteBluetooth;

	private LiteBlueService mLiteBlueService;
	private IncomingService mIncomingService;

	public IncomingService getIncomingService() {
		return this.mIncomingService;
	}

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

	private ServiceConnection incomingConection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIncomingService = ((IncomingService.MyBinder) service).getIncomingService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIncomingService = null;
		}
	};

	/**
	 * 短信监听
	 */
	private ContentObserver newMmsContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			int mNewSmsCount = getNewSmsCount() + getNewMmsCount();
			Log.e("mMissCallCount", "mNewSmsCount_______________________________" + mNewSmsCount);
			doWriteToWatch();
			super.onChange(selfChange, uri);
		}

	};

	/**
	 *
	 */
	private BroadcastReceiver mPhoneReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.i("sms", "CallReceiver Start...");
			TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			telephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		};
	};

	/**
	 * 电话状态监听
	 */
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.v("BaseApp", "CallListener call state changed : " + incomingNumber);
			String m = null;
			// 如果当前状态为空闲,上次状态为响铃中的话,则认为是未接来电
			if (lastetState == TelephonyManager.CALL_STATE_RINGING && state == TelephonyManager.CALL_STATE_IDLE) {
				sendSmgWhenMissedCall(incomingNumber);
			}
			// 最后改变当前值
			lastetState = state;
		};
	};

	private static int lastetState = TelephonyManager.CALL_STATE_IDLE; // 最后的状态

	@Override
	public void onCreate() {
		super.onCreate();

		Intent bindIntentForLiteBlue = new Intent(this, LiteBlueService.class);
		bindService(bindIntentForLiteBlue, liteConection, Context.BIND_AUTO_CREATE);

		Intent bindIntentForIncoming = new Intent(this, IncomingService.class);
		bindService(bindIntentForIncoming, incomingConection, Context.BIND_AUTO_CREATE);

		// register();
		// registerObserver();
		// registerReceiver(mPhoneReceiver, new
		// IntentFilter("android.intent.action.PHONE_STATE"));

		registerReceiver(mIncomingReveiver, IncomingService.getIntentFilter());
//		registerReceiver(mBlueReveiver, LiteBlueService.getIntentFilter());
	}
	
	private BroadcastReceiver mIncomingReveiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(IncomingService.ACTION_INCOMING_TRUE)) {
				Log.e("BaseApp", "=======incoming true========");
				int phoneCount = intent.getExtras().getInt(IncomingService.EXTRA_PHONE_COUNT);
				int msgCount = intent.getExtras().getInt(IncomingService.EXTRA_MSG_COUNT);
				doWriteToWatch(phoneCount, msgCount);
			} else if (action.equals(IncomingService.ACTION_INCOMING_FALSE)) {
				Log.e("BaseApp", "=======incoming false========");
			}
		}
	};
//	private BroadcastReceiver mBlueReveiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
//			} else if (action.equals(LiteBlueService.LITE_GATT_DISCONNECTED)) {
//			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_HEART_RATE)) {
//				final int hr = intent.getExtras().getInt(LiteBlueService.EXTRA_DATA_HEART_RATE);
//				if (mOnHeartRateChangListener!=null) {
//					mOnHeartRateChangListener.onHeartRateChange(hr);
//				}
//			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_SLEEP)) {
//
//				final int[] data = intent.getExtras().getIntArray(LiteBlueService.EXTRA_DATA_SLEEP);
//			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED_STEP)) {
//				Log.e("BaseApp", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//				final int step = intent.getExtras().getInt(LiteBlueService.EXTRA_DATA_STEP);
//				if(mOnStepChangListener!=null){
//					mOnStepChangListener.onChange(step);
//				}
//			}
//		
//		}
//	};
	
	protected void doWriteToWatch(int phoneCount, int msgCount) {
		boolean isOpen = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_INCOMING_ON_OFF, false);
		if (isOpen && mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
			mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForPhoneMessageIncomingProtocl(phoneCount, msgCount));
		}
	}
	
//	public interface OnStepChangListener{
//		public void onChange(int step);
//	}
//	private OnStepChangListener mOnStepChangListener;
//	public void setOnStepChangListener(OnStepChangListener mOnStepChangListener){
//		this.mOnStepChangListener = mOnStepChangListener;
//	}
//	
//	public interface OnHeartRateChangListener{
//		public void onHeartRateChange(int heartRate);
//
//	}
//	private OnHeartRateChangListener mOnHeartRateChangListener;
//	public void setOnHeartRateChangListener(OnHeartRateChangListener mOnHeartRateChangListener){
//		this.mOnHeartRateChangListener = mOnHeartRateChangListener;
//	}
	
	/**
	 * 注册监听短信
	 */
	private void registerObserver() {
		unregisterObserver();
		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, newMmsContentObserver);
		// getContentResolver().registerContentObserver(MmsSms.CONTENT_URI,
		// true, newMmsContentObserver);
	}

	private synchronized void unregisterObserver() {
		try {
			if (newMmsContentObserver != null) {
				getContentResolver().unregisterContentObserver(newMmsContentObserver);
			}
			if (newMmsContentObserver != null) {
				getContentResolver().unregisterContentObserver(newMmsContentObserver);
			}
		} catch (Exception e) {
			Log.e("", "unregisterObserver fail");
		}
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

	/**
	 * 得到未读短信数量：
	 * 
	 * @return
	 */
	private int getNewSmsCount() {
		int result = 0;
		Cursor csr = getContentResolver().query(Uri.parse("content://sms"), null, "type = 1 and read = 0", null, null);
		if (csr != null) {
			result = csr.getCount();
			csr.close();
		}
		return result;
	}

	/**
	 * 获取未读彩信数量：
	 * 
	 * @return
	 */
	private int getNewMmsCount() {
		int result = 0;
		Cursor csr = getContentResolver().query(Uri.parse("content://mms/inbox"), null, "read = 0", null, null);
		if (csr != null) {
			result = csr.getCount();
			csr.close();
		}
		return result;
	}

	/**
	 * 获取本地未接电话数量
	 * 
	 * @return
	 */
	private int readMissCall() {
		int result = 0;
		Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { Calls.TYPE }, " type=? and new=?", new String[] { Calls.MISSED_TYPE + "", "1" }, "date desc");

		if (cursor != null) {
			result = cursor.getCount();
			cursor.close();
		}
		return result;
	}

	private void sendSmgWhenMissedCall(String incomingNumber) {
		// 未接来电处理(发短信,发email等)
		Log.e("BaseApp", "未接来电：" + incomingNumber);
		Toast.makeText(this, "未接来电", Toast.LENGTH_LONG).show();
		doWriteToWatch();

	}

	/**
	 * 
	 */
	public void doWriteToWatch() {
		boolean isOpen = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_INCOMING_ON_OFF, false);
		if (isOpen && mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
			mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForPhoneMessageIncomingProtocl(readMissCall(), getNewSmsCount() + getNewMmsCount()));
		}
	}

	public LiteBluetooth getLiteBlueTooth() {
		return this.mLiteBluetooth;
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
		finishActivity();
//		System.exit(0);
		super.onTerminate();
	}

}
