package com.mycj.healthy.service;

import java.security.spec.MGF1ParameterSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectError;
import com.litesuits.bluetooth.conn.ConnectListener;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.conn.TimeoutCallback;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.mycj.healthy.BaseApp;
import com.mycj.healthy.MainActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.entity.HeartRateData;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.entity.SportData;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.MessageUtil;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.util.TimeUtil;

public class LiteBlueService extends Service {
	/** Intent for broadcast */
	public static final String LITE_DEVICE_FOUND = "com.liteblue.device_found";
	public static final String LITE_GATT_CONNECTED = "com.liteblue.gatt_connected";
	public static final String LITE_GATT_DISCONNECTED = "com.liteblue.gatt_disconnected";
	public static final String LITE_SERVICE_DISCOVERED = "com.liteblue.service_discovered";
	public static final String LITE_CHARACTERISTIC_READ = "com.liteblue.characteristic_read";
	public static final String LITE_CHARACTERISTIC_NOTIFICATION = "com.liteblue.characteristic_notification";
	public static final String LITE_CHARACTERISTIC_WRITE = "com.liteblue.characteristic_write";
	public static final String LITE_CHARACTERISTIC_CHANGED = "com.liteblue.characteristic_changed";
	public static final String LITE_CHARACTERISTIC_CHANGED_STEP = "com.liteblue.characteristic_changed_step";
	public static final String LITE_CHARACTERISTIC_CHANGED_SLEEP = "com.liteblue.characteristic_changed_sleep";
	public static final String LITE_CHARACTERISTIC_CHANGED_SPORT = "com.liteblue.characteristic_changed_sport";
	public static final String LITE_CHARACTERISTIC_CHANGED_HEART_RATE = "com.liteblue.characteristic_changed_sport_heart_rate";
	public static final String LITE_CHARACTERISTIC_CHANGED_GOAL_STEP = "com.liteblue.characteristic_changed_goal_step";
	public static final String LITE_CHARACTERISTIC_CHANGED_MAX_HEART_RATE = "com.liteblue.characteristic_changed_max_step";
	public static final String LITE_BLUETOOTH_NOT_OPEN = "com.liteblue.characteristic_bluetooth_not_open";
	public static final String LITE_SYNC_SUCCESS = "com.liteblue.sync.success";
	public static final String LITE_SYNC_FAIL = "com.liteblue.sunc.fail";
	public static final String LITE_READ_RSSI = "com.liteblue.read_rssi";
	public static final String LITE_SCANNING = "com.liteblue.scanning";
	public static final String LITE_STATE_CHANGE = "com.liteblue.state_change";
	public static final String LITE_GATT_CONNECTTING = "com.liteblue.gatt_connectting";
	public static final String LITE_ISSETTING_OK = "com.liteblue.issetting.ok";
	/** Intent extras */
	public static final String EXTRA_DEVICE = "DEVICE";
	public static final String EXTRA_RSSI = "RSSI";
	public static final String EXTRA_ADDR = "ADDRESS";
	public static final String EXTRA_CONNECTED = "CONNECTED";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_UUID = "UUID";
	public static final String EXTRA_VALUE = "VALUE";
	public static final String EXTRA_REQUEST = "REQUEST";
	public static final String EXTRA_REASON = "REASON";
	public static final String EXTRA_ERROR = "REASON";
	public static final String EXTRA_SCAN_RECORD = "SCAN_RECORD";
	public static final String EXTRA_STATE = "STATE";
	public static final String EXTRA_DATA_STEP = "extra_dada_step";
	public static final String EXTRA_DATA_GOAL_STEP = "extra_goal_dada_step";
	public static final String EXTRA_DATA_SPORT_MODEL = "extra_goal_dada_sport_model";
	public static final String EXTRA_DATA_MAX_HEART_RATE = "extra_goal_dada_max_heart_rate";
	public static final String EXTRA_DATA_SLEEP = "extra_dada_sleep";
	public static final String EXTRA_DATA_SLEEP_MIDDLE = "extra_dada_sleep_middle";
	public static final String EXTRA_DATA_SLEEP_REST = "extra_dada_sleep_rest";
	public static final String EXTRA_DATA_SPORT = "extra_dada_sport";
	public static final String EXTRA_DATA_HEART_RATE = "extra_dada_heart_rate";
	public static final String EXTRA_DATA_HEART_RATE_DATE = "extra_dada_heart_rate_date";

	public static final String DESC_CCC = "00002902-0000-1000-8000-00805f9b34fb";
	//
	public static final String BLE_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static final String BLE_CHARACTERISTIC_NOTIFY = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static final String BLE_CHARACTERISTIC_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
	// public static final String BLE_SERVICE =
	// "000056FF-0000-1000-8000-00805f9b34fb";
	// public static final String BLE_CHARACTERISTIC_NOTIFY =
	// "000033F4-0000-1000-8000-00805f9b34fb";
	// public static final String BLE_CHARACTERISTIC_WRITE =
	// "000033F3-0000-1000-8000-00805f9b34fb";

	private final MyBinder mBinder = new MyBinder();
	private LiteBluetooth mLiteBluetooth;
	private MyCallbackLeScan scanCallback;
	private MyLiteBlueConnectListener mConnLisener;
	private ConnectState currentState;
	private BluetoothDevice currentBluetoothDevice;
	private BluetoothGatt mBluetoothGatt;
	private boolean isConnetted;

	private int writeInterval = 200; // 100 ms
	private int writeCount = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				mHandler.postDelayed(timerCheckBle, 20 * 1000);
				break;
			case 2:
				// new SyncTaskSetting().execute(byteList.get(idx));
				writeCharactics(byteList.get(idx));
				idx++;
				mHandler.postDelayed(syncRunning, 2 * 1000);
				break;
			case 4:
				break;

			default:
				break;
			}
		};
	};
	private TimerTask timerCheckBle = new TimerTask() {

		@Override
		public void run() {
			if (!isEnable()) {
				bleIsNotEnable();
			}
			mHandler.sendEmptyMessage(1);
		}

	};

	public boolean isConnetted() {
		if (currentState != null) {
			if (currentState == ConnectState.Connected) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LITE_DEVICE_FOUND);
		intentFilter.addAction(LITE_GATT_CONNECTED);
		intentFilter.addAction(LITE_GATT_DISCONNECTED);
		intentFilter.addAction(LITE_SERVICE_DISCOVERED);
		intentFilter.addAction(LITE_CHARACTERISTIC_READ);
		intentFilter.addAction(LITE_CHARACTERISTIC_NOTIFICATION);
		intentFilter.addAction(LITE_CHARACTERISTIC_WRITE);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED);
		intentFilter.addAction(LITE_READ_RSSI);
		intentFilter.addAction(LITE_BLUETOOTH_NOT_OPEN);
		intentFilter.addAction(LITE_SCANNING);
		intentFilter.addAction(LITE_STATE_CHANGE);
		intentFilter.addAction(LITE_GATT_CONNECTTING);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED_HEART_RATE);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED_SLEEP);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED_SPORT);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED_STEP);
		intentFilter.addAction(LITE_SYNC_FAIL);
		intentFilter.addAction(LITE_SYNC_SUCCESS);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED_GOAL_STEP);
		intentFilter.addAction(LITE_CHARACTERISTIC_CHANGED_MAX_HEART_RATE);
		intentFilter.addAction(LITE_ISSETTING_OK);
		return intentFilter;
	}

	/**
	 * 开始搜索（旧的方法，弃用）
	 */
	public void startScan() {
		Log.e("LiteBlueService", "开始搜索！");
		mLiteBluetooth.stopScan(scanCallback);

		mLiteBluetooth.startScan(scanCallback);
	}

	private boolean isScanning;

	public boolean isScanning() {
		return isScanning;
	}

	// public boolean isBinded() {
	// if (currentBluetoothDevice == null) {
	// return false;
	// } else {
	// return true;
	// }
	// }

	public void startScanUsePeriodScanCallback() {

		Log.e("LiteBlueService", "开始搜索！");
		mLiteBluetooth.startScan(mPeriodScanCallback);
		isScanning = true;
	}

	public void stopScanUsePeriodScanCallback() {
		Log.e("LiteBlueService", "结束搜索！");
		mLiteBluetooth.stopScan(mPeriodScanCallback);
		isScanning = false;
	}

	/**
	 * 结束搜索（旧的方法，弃用）
	 */
	public void stopScan() {
		Log.e("LiteBlueService", "结束搜索！");
		mLiteBluetooth.stopScan(scanCallback);

	}

	public void closeAll() {
		Log.e("LiteBlueService", "关闭所有链接！");
		mLiteBluetooth.closeAllConnects();
	}

	public void connnect(BluetoothDevice device) {
		// if (mBluetoothGatt != null) {
		// mConnLisener.closeBluetoothGatt(mBluetoothGatt);
		// }
		mLiteBluetooth.connect(device, true, mConnLisener);

	}

	public void connnect(String mac) {
		mLiteBluetooth.connect(mac, true, mConnLisener);

	}

	/**
	 * service
	 */
	private BluetoothGattService currentService;
	/**
	 * characteristic写入特性
	 */
	private BluetoothGattCharacteristic currentCharacteristicWrite;
	/**
	 * characteristic读取特性
	 */
	private BluetoothGattCharacteristic currentCharacteristicNotifaication;
	private BluetoothAdapter mBluetoothAdapter;
	private MyPeriodScanCallback mPeriodScanCallback;
	private Runnable syncRunning;
	private int idx;
	private List<byte[]> byteList;

	/**
	 * 写入数据（旧的方法，弃用）
	 * 
	 * @param address
	 * @param value
	 */
	public synchronized void writeCharactics(byte[] value) {
		try {
			if (currentCharacteristicWrite == null) {
				Log.e("BleManager", "writeCharactics()--没有找到write属性");
				return;
			}
			currentCharacteristicWrite.setValue(value);
			mBluetoothGatt.writeCharacteristic(currentCharacteristicWrite);
			Log.v("BleManager", "writeCharactics()--写入write属性");
		} catch (Exception e) {
			Log.e("BleManager", "writeCharactics()--写入数据失败...");
			e.printStackTrace();
		}

	}

	/**
	 * 写入数据
	 * 
	 * @param address
	 * @param value
	 */
	public synchronized void writeCharacticsUseConnectListener(byte[] value) {
		synchronized (currentCharacteristicWrite) {
			try {
				if (currentCharacteristicWrite == null) {
					Log.e("BleManager", "writeCharactics()--没有找到write属性");
					return;
				}
				mConnLisener.characteristicWrite(mBluetoothGatt, currentCharacteristicWrite, value, new TimeoutCallback() {
					@Override
					public void onTimeout(BluetoothGatt gatt) {
						// Log.e("BleManager", "writeCharactics()超时...");
					}
				});
				Log.v("LiteBlueService", "writeCharactics()--写入write属性");
			} catch (Exception e) {
				Log.e("LiteBlueService", "writeCharactics()--写入数据失败...");
				e.printStackTrace();
			}
		}
	}

	private int count = 0;
	private int interval = 200;

	public void writeCharacticsQueque(byte[] value) {
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e("LiteBlueService", "==onBind==");
		return mBinder;
	}

	@Override
	public void onCreate() {
		mHandler.post(timerCheckBle);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mPeriodScanCallback = new MyPeriodScanCallback(10000, mBluetoothAdapter);
		mLiteBluetooth = new LiteBluetooth(getApplicationContext());
		Log.e("LiteBlueService", "==onCreate==");
		Log.v("LiteBlueService", "mLiteBluetooth : " + mLiteBluetooth);
		scanCallback = new MyCallbackLeScan();
		mConnLisener = new MyLiteBlueConnectListener();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("LiteBlueService", "==onStartCommand==");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.e("LiteBlueService", "==onDestroy==");
		mHandler.removeCallbacks(timerCheckBle);
		mBluetoothGatt.close();
		mBluetoothGatt=null;
		super.onDestroy();
	}

	protected void bleIsNotEnable() {
		Intent intent = new Intent(LITE_BLUETOOTH_NOT_OPEN);
		sendBroadcast(intent);
	}

	protected void bleStateChanged(ConnectState state) {
		Intent intent = new Intent(LITE_STATE_CHANGE);
		intent.putExtra(EXTRA_STATE, state);
		sendBroadcast(intent);
	}

	protected void bleDeviceFound(BluetoothDevice device, int rssi, byte[] scanRecord) {
		Intent intent = new Intent(LITE_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		intent.putExtra(EXTRA_SCAN_RECORD, scanRecord);
		sendBroadcast(intent);
	}

	protected void bleGattConnected() {
		Intent intent = new Intent(LITE_GATT_CONNECTED);
		// intent.putExtra(EXTRA_DEVICE,mBluetoothGatt.getDevice());
		sendBroadcast(intent);
	}

	protected void bleGattDisConnected() {
		Intent intent = new Intent(LITE_GATT_DISCONNECTED);
		sendBroadcast(intent);
	}

	protected void bleCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

	}

	protected void bleServiceDiscovered(BluetoothGatt gatt) {
		Intent intent = new Intent(LITE_SERVICE_DISCOVERED);
		BluetoothDevice device = gatt.getDevice();
		intent.putExtra(EXTRA_DEVICE, device);
		Log.e("LiteBlueService", " 发现服务。。。");
		sendBroadcast(intent);
	}

	protected void bleConnectting() {
		Intent intent = new Intent(LITE_GATT_CONNECTTING);
		Log.e("LiteBlueService", " 连接中。。。");
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		byte[] value = characteristic.getValue();
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED);
		intent.putExtra(EXTRA_VALUE, value);
		sendBroadcast(intent);

	}

	protected void bleScanning() {
		Intent intent = new Intent(LITE_SCANNING);
		Log.e("LiteBlueService", " 正在搜索。。。");
		sendBroadcast(intent);
	}

	protected void bleCharacteristicChangedFromStep(SportData stepData) {
		Log.v("", "计步数通知发送");

		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_STEP);
		intent.putExtra(EXTRA_DATA_STEP, stepData.getStep());
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChangedFromHeartRate(HeartRateData heartData) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_HEART_RATE);
		// intent.putExtra(EXTRA_DATA_HEART_RATE, heartData.getHeartRate());
		// intent.putExtra(EXTRA_DATA_HEART_RATE_DATE, heartData.getDate());
		intent.putExtra(EXTRA_DATA_HEART_RATE, heartData);
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChangedFromSport(HashMap<String, Integer> sportData) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_SPORT);
		intent.putExtra(EXTRA_DATA_SPORT, sportData);
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChangedForStepGoal(int[] goalStepData) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_GOAL_STEP);
		intent.putExtra(EXTRA_DATA_GOAL_STEP, goalStepData[1]);
		intent.putExtra(EXTRA_DATA_SPORT_MODEL, goalStepData[0]);
		sendBroadcast(intent);
	}

	protected void bleCharacteristicChangedFromSleep(HashMap<String, Integer> sleepData) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_SLEEP);
		intent.putExtra(EXTRA_DATA_SLEEP, new int[] { sleepData.get("sleep"), sleepData.get("middle"), sleepData.get("rest") });
		sendBroadcast(intent);
	}

	protected void bleCharacteristicChangedForMaxHeartRate(int maxHeartRate) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_MAX_HEART_RATE);
		intent.putExtra(EXTRA_DATA_MAX_HEART_RATE, maxHeartRate);
		sendBroadcast(intent);
	}

	protected void bleSyncSetting() {
		Intent intent = new Intent(LITE_ISSETTING_OK);
		sendBroadcast(intent);
	}

	protected void bleSyncHistoryFail() {
		Intent intent = new Intent(LITE_SYNC_FAIL);
		sendBroadcast(intent);
	}

	protected void bleSyncHistorySuccess() {
		Intent intent = new Intent(LITE_SYNC_SUCCESS);
		sendBroadcast(intent);
	}

	public boolean isEnable() {
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Log.v("LiteBlueService", "蓝牙未打开");
			return false;
		} else {
			return true;
		}
	}

	public void enable(Activity activity) {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, 1);
	}

	public void enable() {
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.enable();
		}
	}

	/**
	 * @return the currentState
	 */
	public ConnectState getCurrentState() {
		return currentState;
	}

	/**
	 * @param currentState
	 *            the currentState to set
	 */
	public void setCurrentState(ConnectState currentState) {
		this.currentState = currentState;
	}

	public BluetoothDevice getCurrentBluetoothDevice() {
		return currentBluetoothDevice;
	}

	public void setCurrentBluetoothDevice(BluetoothDevice currentBluetoothDevice) {
		this.currentBluetoothDevice = currentBluetoothDevice;
	}

	public void bindedDevice(BluetoothDevice device) {
		Log.e("", "device.name" + device.getName() + ":" + device.getAddress());
		if (device != null) {
			SharedPreferenceUtil.put(this, Constant.SHARE_BINDING_DEVICE_NAME, device.getName());
			SharedPreferenceUtil.put(this, Constant.SHARE_BINDING_DEVICE_ADRESS, device.getAddress());
		}
	}

	public class MyBinder extends Binder {
		public LiteBlueService getService() {
			return LiteBlueService.this;
		}
	}

	/**
	 * call back for scan（旧的方法，弃用）
	 * 
	 * @author Administrator
	 *
	 */
	private class MyCallbackLeScan implements BluetoothAdapter.LeScanCallback {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

			bleDeviceFound(device, rssi, scanRecord);
		}

	}

	/**
	 * 搜索回调Callback
	 * 
	 * @author Administrator
	 *
	 */
	private class MyPeriodScanCallback extends PeriodScanCallback {

		protected MyPeriodScanCallback(long timeoutMillis, BluetoothAdapter adapter) {
			super(timeoutMillis, adapter);
		}

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

			String name = (String) SharedPreferenceUtil.get(getApplicationContext(), Constant.SHARE_BINDING_DEVICE_NAME, "");
			String address = (String) SharedPreferenceUtil.get(getApplicationContext(), Constant.SHARE_BINDING_DEVICE_ADRESS, "");

			Log.v("LiteBlueService", "《《     绑定设备信息[ name : address " + name + " : " + address + " ]    》》");
			if (name.equals(device.getName()) && address.equals(device.getAddress())) {
				connnect(device);
				// bindedDevice(device);
			}

			bleDeviceFound(device, rssi, scanRecord);
		}

		@Override
		public void onScanTimeout() {
			Log.e("LiteBlueService", "<<<< onScanTimeout() >>>>");
			// 搜索设备超时
			startScanUsePeriodScanCallback();
		}

	}

	private synchronized void parseData(byte[] data) {
		HeartRateData heartRateDate;
		SportData sportData;
		HistoryData historyData;

		String dataStr = DataUtil.getStringByBytes(data);
		int tag = data[0];
		Log.v("LiteBlueService", "tag : " + tag);
		Log.i("LiteBlueService", "####################数据解析中##################" + dataStr);
		switch (tag) {
		case Constant.PROTOCL_STEP_NOTIFY:
			sportData = ProtoclData.parserDataForStep(data);
			if (sportData != null) {
				bleCharacteristicChangedFromStep(sportData);
			}
			break;

		case Constant.PROTOCL_HEART_RATE_NOTIFY:
			heartRateDate = ProtoclData.parserDataForHeartRate(data);
			if (heartRateDate != null) {
				bleCharacteristicChangedFromHeartRate(heartRateDate);
				// 存贮
				saveHreartRateData(heartRateDate);
			}
			break;
		// 同步数据
		case Constant.PROTOCL_UPDATE_ALL_NOTIFY:
			historyData = ProtoclData.parserProtoclDataForHistory(data);
			if (historyData != null) {
				saveHistoryData(historyData);
			}
			break;
		case Constant.PROTOCL_CLOCK_NOTIFY:
			break;
		case Constant.PROTOCL_STEP_GOAL_NOTIFY:
			int[] goalStepData = ProtoclData.parserDataForGoalStep(data);
			if (goalStepData != null) {
				saveGoalStep(goalStepData);
				bleCharacteristicChangedForStepGoal(goalStepData);
			}
			break;
		case Constant.PROTOCL_HEART_RATE_MAX_NOTIFY_TO_PHONE:
			int maxHeartRate = ProtoclData.parserDataForMaxHeartRate(data);
			if (maxHeartRate != 0) {
				Log.v("", "maxHeartRate :" + maxHeartRate);
				saveHeartRate(maxHeartRate);
				bleCharacteristicChangedForMaxHeartRate(maxHeartRate);
			}
			break;

		default:
			break;
		}
	}

	private void saveHeartRate(int maxHeartRate) {
		SharedPreferenceUtil.put(getApplicationContext(), Constant.SHARE_HEART_RATE_MAX, maxHeartRate);
	}

	private void saveGoalStep(int[] goalStepData) {
		SharedPreferenceUtil.put(getApplicationContext(), Constant.SHARE_STEP_GOAL, goalStepData[1]);
		SharedPreferenceUtil.put(getApplicationContext(), Constant.SHARE_STEP_ON_OFF, goalStepData[0] == 1 ? true : false);
	}

	private class HeartRateTask extends AsyncTask<HeartRateData, Void, Boolean> {
		@Override
		protected Boolean doInBackground(HeartRateData... params) {
			if (params[0].getHeartRate()!=0) {
				return params[0].save();// Litepal 存储
			}else{
				return false;
			}
		}

	};

	private class SyncTaskSetting extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... params) {
			writeCharactics(params[0]);
			return null;
		}

	}

	private class HistoryDataTask extends AsyncTask<HistoryData, Void, Integer> {
		@Override
		protected Integer doInBackground(HistoryData... params) {

			// 查询某天的记录
			List<HistoryData> historyDatas = DataSupport.where("historyDate=?", params[0].getHistoryDate()).find(HistoryData.class);
		
			Log.i("LiteBlueService", "同步开始...");
			boolean isSaveOk;
			// 判断数据库中有没有这天的数据
			if (historyDatas != null && historyDatas.size() > 0) {
				Log.i("LiteBlueService", "已存在数据，更新操作");
				historyDatas.get(0).setStep(params[0].getStep());
				historyDatas.get(0).setSleep(params[0].getSleep());
				historyDatas.get(0).setMiddle(params[0].getMiddle());
				historyDatas.get(0).setRest(params[0].getRest());
				historyDatas.get(0).save();
				isSaveOk = true;
			} else {
				Log.i("LiteBlueService", "不存在数据，存贮操作");
				isSaveOk = params[0].save();
				isSaveOk = true;
			}

			// 先判断存贮是否OK，再判断存储的日期是否是今天
			if (isSaveOk) {
				boolean isOneDay = TimeUtil.isSameDay(params[0].getHistoryDate(), new Date());
				if (isOneDay) {
					Log.i("LiteBlueService", "同步完成...");
					return 1;
				} else {
					Log.i("LiteBlueService", "同步ing...");
					return 2;
				}
			} else {
				Log.i("LiteBlueService", "同步失败...");
				return 0;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case 0:
				// 同步失败
				bleSyncHistoryFail();
				break;
			case 1:
				// 同步完成
				bleSyncHistorySuccess();
				break;
			case 2:
				// 同步中
				break;
			default:
				break;
			}
			super.onPostExecute(result);
		}
	};

	/**
	 * 
	 * App同步设置到手表
	 */
	private void syncSetting() {
		// sendNotification("正在同步设置...");
		final long start = System.currentTimeMillis();
		Log.v("LiteBlueService", "----------------gatt连接成功，同步设置开始----------------");
		int step = (int) SharedPreferenceUtil.get(this, Constant.SHARE_STEP_GOAL, 500);
		int model = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_STEP_ON_OFF, false) ? 1 : 0;
		int hour = (int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_HOUR, 12);
		int minute = (int) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_MIN, 00);
		int open = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_CLOCK_ON_OFF, false) ? 0 : 1;
		int sleepStartHour = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_START_HOUR, 22);
		int sleepStartMin = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_START_MIN, 00);
		int sleepEndHour = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_END_HOUR, 8);
		int sleepEndMin = (int) SharedPreferenceUtil.get(this, Constant.SHARE_SLEEP_END_MIN, 00);
		int middleStartHour = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_START_HOUR, 12);
		int middleStartMin = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_START_MIN, 00);
		int middleEndHour = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_END_HOUR, 14);
		int middleEndMin = (int) SharedPreferenceUtil.get(this, Constant.SHARE_MIDDLE_SLEEP_END_MIN, 00);
		int[] sleep = new int[] { sleepStartHour, sleepStartMin, sleepEndHour, sleepEndMin, middleStartHour, middleStartMin, middleEndHour, middleEndMin };
		int max = (int) SharedPreferenceUtil.get(this, Constant.SHARE_HEART_RATE_MAX, 100);
		int openForAutoHr = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? 1 : 0;
		Log.v("LiteBlueService", "--最大目标步数 : " + step + "是否开启 : " + model);
		Log.v("LiteBlueService", "--闹钟 : " + hour + ":" + minute + ",是否开启 : " + open);
		Log.v("LiteBlueService", "--睡眠开始时间 : " + sleepStartHour + ":" + sleepStartMin + "睡眠结束时间 : " + sleepEndHour + ":" + sleepEndMin);
		Log.v("LiteBlueService", "--午睡开始时间 : " + middleStartHour + ":" + middleStartMin + "午睡结束时间 : " + middleEndHour + ":" + middleEndMin);
		Log.v("LiteBlueService", "--最大心率设置 : " + max);
		Log.v("LiteBlueService", "--自动心率设置 : " + openForAutoHr);
		byteList = new ArrayList<>();
		byteList.add(ProtoclData.toByteForDateProtocl(new Date(), getApplicationContext()));
		byteList.add(ProtoclData.toByteForStepProtocl(step, model));
		byteList.add(ProtoclData.toByteForClockProtocl(hour, minute, open));
		byteList.add(ProtoclData.toByteForSleepProtocl(sleep));
		byteList.add(ProtoclData.toByteForHeartRateMaxProtocl(max));
		byteList.add(ProtoclData.toByteForAutoHeartRateProtocl(openForAutoHr));
		byteList.add(ProtoclData.toByteForPhoneMessageIncomingProtocl(MessageUtil.readMissCall(getApplicationContext()),
				MessageUtil.getNewSmsCount(getApplicationContext()) + MessageUtil.getNewMmsCount(getApplicationContext())));

		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (int i = 0; i < byteList.size(); i++) {
			final int index = i;
			try {
				Thread.sleep(index * 1000);
			} catch (InterruptedException e) {
				Log.e("", "_____________________i_________________________" + i);
				e.printStackTrace();
			}
			singleThreadExecutor.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println(index);
					writeCharacticsUseConnectListener(byteList.get(index));
				}
			});
		}

		// syncRunning = new Runnable() {
		// @Override
		// public void run() {
		// if (idx < byteList.size()) {
		// mHandler.sendEmptyMessage(2);
		// } else {
		// long end = System.currentTimeMillis();
		// Log.v("LiteBlueService",
		// "----------------gatt连接成功，同步设置结束----------------用时 :" + (end - start)
		// + "ms");
		// idx = 0;
		// sendNotification("同步设置完成！");
		// bleSyncSetting();
		// }
		// }
		// };
		// mHandler.postDelayed(syncRunning, 8 * 1000);

		// //同步日期
		// writeCharacticsUseConnectListener(ProtoclData.toByteForDateProtocl(new
		// Date(), getApplicationContext()));
		// //同步未接电话/短信
		// ((BaseApp) getApplication()).doWriteToWatch();
		// //同步目标步数
		// writeCharacticsUseConnectListener(ProtoclData.toByteForStepProtocl(step,
		// model));
		// //同步闹钟
		// writeCharacticsUseConnectListener(ProtoclData.toByteForClockProtocl(hour,
		// minute, open));
		// //同步睡眠时间
		// writeCharacticsUseConnectListener(ProtoclData.toByteForSleepProtocl(sleep));
		// //同步最大心率
		// writeCharacticsUseConnectListener(ProtoclData.toByteForHeartRateMaxProtocl(max));
		// //同步自动心率
		// writeCharacticsUseConnectListener(ProtoclData.toByteForAutoHeartRateProtocl(openForAutoHr));

	}

	private void saveHreartRateData(HeartRateData data) {
		new HeartRateTask().execute(data);
	}

	private void saveHistoryData(HistoryData data) {
		new HistoryDataTask().execute(data);
	}

	/**
	 * LiteBlue ConnectListener
	 * 
	 * @author Administrator
	 *
	 */
	private class MyLiteBlueConnectListener extends ConnectListener {

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			Log.i("LiteBlueService", "<<<< onCharacteristicChanged() >>>>");
			System.out.println("onCharacteristicChanged");
			bleCharacteristicChanged(gatt, characteristic);
			// initData(characteristic.getValue());
			byte[] data = characteristic.getValue();
			parseData(data);

		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			Log.v("LiteBlueServiceTest", "<<<< onCharacteristicWrite() >>>>" + characteristic.getValue());

			bleCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onFailed(ConnectError error) {
			Log.v("LiteBlueService", "<<<< onFailed()--ConnectError : [" + error.getCode() + "," + error.getMessage() + "] >>>>");
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt) {
			Log.v("LiteBlueService", "<<<< onServicesDiscovered()--BluetoothGatt.services : [" + gatt.getServices() + "] >>>>");
			mBluetoothGatt = gatt;
			setCurrentBluetoothDevice(gatt.getDevice());

			// 方法一
			BluetoothGattService service = gatt.getService(UUID.fromString(BLE_SERVICE));
			if (service != null) {
				BluetoothGattCharacteristic characteristicNotify = service.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_NOTIFY));
				currentCharacteristicWrite = service.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_WRITE));
				Log.e("LiteBlueService", "_______service__________________找到了指定的service！！");
				if (characteristicNotify != null && currentCharacteristicWrite != null) {
					enableCharacteristicNotification(gatt, characteristicNotify, DESC_CCC);
					bleServiceDiscovered(gatt);
					bindedDevice(currentBluetoothDevice);// 绑定设备
					Log.e("LiteBlueService", "__________write__________________找到了指定的characteristic！！");
					Log.e("LiteBlueService", "___________notify_________________找到了指定的characteristic！！");
					stopScanUsePeriodScanCallback();
				}
			}
		}

		// 方法二
		// List<BluetoothGattService> services = gatt.getServices();
		// for (BluetoothGattService mService : services) {
		// Log.e("LiteBlueService", "_______________________________");
		// Log.e("LiteBlueService", "service :" + mService.getUuid());
		// if (mService.getUuid().equals(UUID.fromString(BLE_SERVICE))) {
		// currentService = mService;
		// Log.e("LiteBlueService",
		// "_______service__________________找到了指定的service！！");
		// List<BluetoothGattCharacteristic> characteristics =
		// mService.getCharacteristics();
		// for (BluetoothGattCharacteristic mChar : characteristics) {
		// Log.e("LiteBlueService", "characteristic :" + mChar.getUuid());
		// if
		// (mChar.getUuid().equals(UUID.fromString(BLE_CHARACTERISTIC_WRITE))) {
		// Log.e("LiteBlueService",
		// "__________write__________________找到了指定的characteristic！！");
		// currentCharacteristicWrite = mChar;
		//
		// }
		// if
		// (mChar.getUuid().equals(UUID.fromString(BLE_CHARACTERISTIC_NOTIFY)))
		// {
		// Log.e("LiteBlueService",
		// "___________notify_________________找到了指定的characteristic！！");
		// currentCharacteristicNotifaication = mChar;
		// this.enableCharacteristicNotification(gatt, mChar, DESC_CCC);
		// }
		// }
		// bindedDevice(currentBluetoothDevice);// 绑定设备
		// bleServiceDiscovered(gatt);
		// }
		// }
		// }

		/*
		 * 
		 * Initialed(0, "初始化状态：连接未建立"), Scanning(1, "扫描中..."), Connecting(2,
		 * "设备连接中..."), Connected(3, "设备已连接"), ServiceDiscovering(4,
		 * "服务发现中..."), ServiceDiscovered(5, "已发现服务"), DisConnected(6, "连接已断开");
		 */
		@Override
		public void onStateChanged(ConnectState state) {
			Log.v("LiteBlueService", "<<<< onStateChanged()--ConnectState : [" + state.getCode() + "," + state.getMessage() + "] >>>>");
			switch (state.getCode()) {
			case 0:
				currentState = ConnectState.Initialed;
				break;
			case 1:
				currentState = ConnectState.Scanning;
				bleScanning();
				break;
			case 2:
				currentState = ConnectState.Connecting;
				bleConnectting();
				break;
			case 3:
				currentState = ConnectState.Connected;
				bleGattConnected();
				break;
			case 4:

				break;
			case 5:

				break;
			case 6:
				currentState = ConnectState.DisConnected;
				bleGattDisConnected();
				
				mBluetoothGatt.close();
				mBluetoothGatt=null;
				// currentCharacteristicWrite = null;
				// currentCharacteristicNotifaication = null;
				// currentBluetoothDevice = null;
				// if (mBluetoothGatt!=null) {
				// mConnLisener.closeBluetoothGatt(mBluetoothGatt);
				// }
				// if (currentBluetoothDevice!=null) {
				// connnect(currentBluetoothDevice);
				// }
				startScanUsePeriodScanCallback();
				Log.e("", "_____________________duan xian  le ________________________________");
				break;

			default:
				break;
			}
		}
	}

	private void sendNotification(String msg) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0, new
		// Intent(this, Complete.class), 0);
		// 通过Notification.Builder来创建通知，注意API Level
		// API16之后才支持
		Notification notify3 = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher).setTicker(msg).setContentTitle("提醒").setContentText(msg)
		// .setContentIntent(pendingIntent3)
		// .setNumber(1)
				.build(); // 需要注意build()是在API
							// level16及之后增加的，API11可以使用getNotificatin()来替代
		notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
		manager.notify(1, notify3);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
	}

	public void writeValueToDevice(final byte[] value) {
		int actualTimeInterval = writeCount * writeInterval;
		Log.d("LiteBlueServiceTest", "" + actualTimeInterval);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mConnLisener.characteristicWrite(mBluetoothGatt, currentCharacteristicWrite, value, null);
				Log.d("LiteBlueServiceTest", "writeValueToDevice()" + value);
				writeCount--;
				if (writeCount < 0) {
					writeCount = 0;
				}
			}
		}, actualTimeInterval);
		writeCount++;
	}

}
