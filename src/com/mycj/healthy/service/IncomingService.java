package com.mycj.healthy.service;

import java.util.TimerTask;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.text.format.Time;

/**
 * 监听短信/电话 未读个数
 * 
 * @author Administrator
 *
 */
public class IncomingService extends Service {
	public final static String ACTION_INCOMING_TRUE = "action_incoming_true";
	public final static String ACTION_INCOMING_FALSE = "action_incoming_false";
	public final static String EXTRA_IS_INCOMING = "extra_is_incoming";
	public final static String EXTRA_MSG_COUNT = "extra_msg_count";
	public final static String EXTRA_PHONE_COUNT = "extra_phone_count";

	private final static int DIFF = 4 * 1000; // 60秒运行一次

	private int lastPhoneCount;
	private int lastMsgCount;

	private boolean isHasIncoming = false;
	private boolean isOn = false;

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

	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			int mmsCount = getNewMmsCount();
			int msmCount = getNewSmsCount();
			int phoneCount = readMissCall();
			if (mmsCount == 0 && msmCount == 0 && phoneCount == 0) {
				isHasIncoming = false;

			} else {
				isHasIncoming = true;
				if (!(lastPhoneCount == phoneCount && lastMsgCount == mmsCount + msmCount)) {
					lastPhoneCount = phoneCount;
					lastMsgCount = mmsCount + msmCount;
					sendbroastTrue();
				}
			}

			mHandle.sendEmptyMessage(1);
		}
	};

	public static IntentFilter getIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_INCOMING_TRUE);
		filter.addAction(ACTION_INCOMING_FALSE);
		return filter;
	}

	private void sendbroastTrue() {
		Intent intent = new Intent(ACTION_INCOMING_TRUE);
		intent.putExtra(EXTRA_IS_INCOMING, isHasIncoming);
		intent.putExtra(EXTRA_MSG_COUNT, lastMsgCount);
		intent.putExtra(EXTRA_PHONE_COUNT, lastPhoneCount);
		sendBroadcast(intent);
	}

	private MyBinder myBinder = new MyBinder();

	public class MyBinder extends Binder {
		public IncomingService getIncomingService() {
			return IncomingService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return myBinder;
	}
	@Override
	public void onDestroy() {
		mHandle.removeCallbacks(task);
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mHandle.post(task);
	}

	public void doWriteToWatch(LiteBlueService mLiteBlueService) {
		boolean isOpen = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_INCOMING_ON_OFF, false);
		if (isOpen && mLiteBlueService.getCurrentState() != null && mLiteBlueService.getCurrentState() == ConnectState.Connected) {
			mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForPhoneMessageIncomingProtocl(readMissCall(), getNewSmsCount() + getNewMmsCount()));
		}
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
	
	
	
}
