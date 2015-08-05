package com.mycj.healthy.service;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.ConnectError;
import com.litesuits.bluetooth.conn.ConnectListener;
import com.litesuits.bluetooth.conn.ConnectState;
import com.litesuits.bluetooth.conn.TimeoutCallback;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;

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
	public static final String LITE_BLUETOOTH_NOT_OPEN = "com.liteblue.characteristic_bluetooth_not_open";

	public static final String LITE_READ_RSSI = "com.liteblue.read_rssi";
	public static final String LITE_SCANNING = "com.liteblue.scanning";
	public static final String LITE_STATE_CHANGE = "com.liteblue.state_change";
	public static final String LITE_GATT_CONNECTTING = "com.liteblue.gatt_connectting";
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
	public static final String EXTRA_DATA_SLEEP = "extra_dada_sleep";
	public static final String EXTRA_DATA_SLEEP_MIDDLE = "extra_dada_sleep_middle";
	public static final String EXTRA_DATA_SLEEP_REST = "extra_dada_sleep_rest";
	public static final String EXTRA_DATA_SPORT = "extra_dada_sport";
	public static final String EXTRA_DATA_HEART_RATE = "extra_dada_heart_rate";

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

	private static final String BluetoothManager = null;
	private final MyBinder mBinder = new MyBinder();
	private LiteBluetooth mLiteBluetooth;
	private MyCallbackLeScan scanCallback;
	private MyLiteBlueConnectListener mConnLisener;
	private ConnectState currentState;
	private BluetoothDevice currentBluetoothDevice;
	private BluetoothGatt mBluetoothGatt;
	private Handler mHandler  = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:	
				mHandler.postDelayed(timer, 20*1000);
				break;

			default:
				break;
			}
		};
	};
	private TimerTask timer = new TimerTask() {
		
		@Override
		public void run() {
			if (!isEnable()) {
				bleIsNotEnable();
			}
			mHandler.sendEmptyMessage(1);
		}
		
	};
	
	
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
		return intentFilter;
	}

	protected void bleIsNotEnable() {
		Intent intent = new Intent(LITE_BLUETOOTH_NOT_OPEN);
		sendBroadcast(intent);
	}

	/**
	 * 开始搜索（旧的方法，弃用）
	 */
	public void startScan() {
		Log.e("LiteBlueService", "开始搜索！");
		mLiteBluetooth.stopScan(scanCallback);

		mLiteBluetooth.startScan(scanCallback);
	}

	public void startScanUsePeriodScanCallback() {
		Log.e("LiteBlueService", "开始搜索！");
		mLiteBluetooth.startScan(mPeriodScanCallback);
	}

	public void stopScanUsePeriodScanCallback() {
		Log.e("LiteBlueService", "结束搜索！");
		mLiteBluetooth.stopScan(mPeriodScanCallback);
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
		try {
			if (currentCharacteristicWrite == null) {
				Log.e("BleManager", "writeCharactics()--没有找到write属性");
				return;
			}
			mConnLisener.characteristicWrite(mBluetoothGatt, currentCharacteristicWrite, value, new TimeoutCallback() {
				@Override
				public void onTimeout(BluetoothGatt gatt) {
					Log.e("BleManager", "writeCharactics()超时...");
				}
			});
			Log.v("BleManager", "writeCharactics()--写入write属性");
		} catch (Exception e) {
			Log.e("BleManager", "writeCharactics()--写入数据失败...");
			e.printStackTrace();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e("LiteBlueService", "==onBind==");
		return mBinder;
	}

	@Override
	public void onCreate() {
		mHandler.post(timer);
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
		currentBluetoothDevice = null;
		mHandler.removeCallbacks(timer);
		super.onDestroy();
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
		Log.e("LiteBlueService", " 已连接。。。");
		sendBroadcast(intent);
	}

	protected void bleGattDisConnected() {
		Intent intent = new Intent(LITE_GATT_DISCONNECTED);
		Log.e("LiteBlueService", " 已断开。。。");
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

	protected void bleCharacteristicChangedFromStep(HashMap<String, Integer> stepData) {
		Log.v("", "计步数通知发送");

		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_STEP);
		intent.putExtra(EXTRA_DATA_STEP, stepData.get("step"));
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChangedFromHeartRate(HashMap<String, Integer> heartData) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_HEART_RATE);
		intent.putExtra(EXTRA_DATA_HEART_RATE, heartData.get("heartRate"));
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChangedFromSport(HashMap<String, Integer> sportData) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_SPORT);
		intent.putExtra(EXTRA_DATA_SPORT, sportData);
		sendBroadcast(intent);

	}

	protected void bleCharacteristicChangedFromSleep(HashMap<String, Integer> sleepData) {
		Intent intent = new Intent(LITE_CHARACTERISTIC_CHANGED_SLEEP);
		intent.putExtra(EXTRA_DATA_SLEEP, new int[] { sleepData.get("sleep"), sleepData.get("middle"), sleepData.get("rest") });
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

	public void save(BluetoothDevice device) {
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

			String name = (String) SharedPreferenceUtil.get(getApplicationContext(), Constant.SHARE_BINDING_DEVICE_NAME, "无连接");
			String address = (String) SharedPreferenceUtil.get(getApplicationContext(), Constant.SHARE_BINDING_DEVICE_ADRESS, "无连接");
			Log.v("LiteBlueService", "name : address " + name + " : " + address);
			if (name.equals(device.getName()) && address.equals(device.getAddress())) {
				connnect(device);
			}

			bleDeviceFound(device, rssi, scanRecord);
		}

		@Override
		public void onScanTimeout() {
			Log.e("LiteBlueService", "<<<< onScanTimeout() >>>>");
			// 搜索设备超时
		}

	}

	private synchronized void parseData(byte[] data) {
		HashMap<String, Integer> sleepData;
		HashMap<String, Integer> sportData;
		HashMap<String, Integer> heartData;
		HashMap<String, Integer> stepData;
		String dataStr = DataUtil.getStringByBytes(data);
		int tag = data[0];
		Log.v("LiteBlueService", "tag : " + tag);
		Log.i("LiteBlueService", "####################数据解析中##################" + dataStr);
		// int protocl
		// =Integer.valueOf(Integer.toHexString((dataStr.substring(0,
		// 2)),16));// 协议头
		// int protocl = Integer.parseInt(dataStr.substring(0, 2), 16); //
		// int protocl = Integer.parseInt(dataStr.substring(0, 2)); //
		// Log.v("LiteBlueService", "protocl : " + protocl);
		switch (tag) {
		case Constant.PROTOCL_STEP_NOTIFY:
			Log.v("LiteBlueService", "tag 111: " + tag);
			stepData = ProtoclData.parserDataForStep(data);
			if (stepData != null) {
				bleCharacteristicChangedFromStep(stepData);
			}
			break;
		case Constant.PROTOCL_SLEEP_NOTIFY:
			sleepData = ProtoclData.parserDataForSleep(data);
			if (sleepData != null) {
				bleCharacteristicChangedFromSleep(sleepData);
			}
			break;

		case Constant.PROTOCL_HEART_RATE_NOTIFY:
			heartData = ProtoclData.parserDataForHeartRate(data);
			if (heartData != null) {
				bleCharacteristicChangedFromHeartRate(heartData);
			}
			break;
		case Constant.PROTOCL_TIME_SYNC_NOTIFY:
			break;
		case Constant.PROTOCL_CLOCK_NOTIFY:
			break;
		case Constant.PROTOCL_STEP_GOAL_NOTIFY:
			break;
		case Constant.PROTOCL_HEART_RATE_MAX_NOTIFY:
			break;

		default:
			break;
		}
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
			Log.v("LiteBlueService", "<<<< onCharacteristicWrite() >>>>");
			bleCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onFailed(ConnectError error) {
			Log.v("LiteBlueService", "<<<< onFailed()--ConnectError : [" + error.getCode() + "," + error.getMessage() + "] >>>>");
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt) {
			mBluetoothGatt = gatt;
			Log.v("LiteBlueService", "<<<< onServicesDiscovered()--BluetoothGatt.services : [" + gatt.getServices() + "] >>>>");
			setCurrentBluetoothDevice(gatt.getDevice());
			save(currentBluetoothDevice);
			List<BluetoothGattService> services = gatt.getServices();
			for (BluetoothGattService mService : services) {
				Log.e("LiteBlueService", "_______________________________");
				Log.e("LiteBlueService", "service :" + mService.getUuid());
				if (mService.getUuid().equals(UUID.fromString(BLE_SERVICE))) {
					currentService = mService;
					Log.e("LiteBlueService", "_______service__________________找到了指定的service！！");
				}
				List<BluetoothGattCharacteristic> characteristics = mService.getCharacteristics();
				for (BluetoothGattCharacteristic mChar : characteristics) {
					Log.e("LiteBlueService", "characteristic :" + mChar.getUuid());
					if (mChar.getUuid().equals(UUID.fromString(BLE_CHARACTERISTIC_WRITE))) {
						Log.e("LiteBlueService", "__________write__________________找到了指定的characteristic！！");
						currentCharacteristicWrite = mChar;
					}
					if (mChar.getUuid().equals(UUID.fromString(BLE_CHARACTERISTIC_NOTIFY))) {
						Log.e("LiteBlueService", "___________notify_________________找到了指定的characteristic！！");
						currentCharacteristicNotifaication = mChar;
						this.enableCharacteristicNotification(gatt, mChar, DESC_CCC);
					}
				}
			}

			bleServiceDiscovered(gatt);
		}

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
				currentBluetoothDevice = null;
				bleGattDisConnected();

				// if (mBluetoothGatt!=null) {
				// mConnLisener.closeBluetoothGatt(mBluetoothGatt);
				// }
				// if (currentBluetoothDevice!=null) {
				// connnect(currentBluetoothDevice);
				// }
				break;

			default:
				break;
			}
		}

	}
}
