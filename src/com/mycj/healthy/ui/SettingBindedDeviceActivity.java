package com.mycj.healthy.ui;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.BaseApp;
import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.MainActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.service.IncomingService;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.ActionSheetDialog;
import com.mycj.healthy.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.healthy.view.ActionSheetDialog.SheetItemColor;

public class SettingBindedDeviceActivity extends BaseSettingActivity implements OnClickListener {

	private List<BluetoothDevice> devices;
	private MyAdapter mAdapter;
	private ListView lvDevices;
	private TextView tvCurrentDeviceName;
	private TextView tvCurrentDeviceAdress;
	private TextView tvConnectInfo;
	private LiteBlueService mLiteBlueService;
	private boolean isScanning;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_DEVICE_FOUND)) {
				BluetoothDevice device = intent.getExtras().getParcelable(LiteBlueService.EXTRA_DEVICE);
				if (!devices.contains(device)) {
					devices.add(device);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
			} else if (action.equals(LiteBlueService.LITE_GATT_CONNECTED)) {
				updateBlueConnectState();
				// setCurrentDevice(mLiteBlueService.getCurrentBluetoothDevice());

			} else if (action.endsWith(LiteBlueService.LITE_GATT_DISCONNECTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setCurrentDevice(null);
						Log.e("SettingBindedDeviceActivity", "_________LITE_GATT_DISCONNECTED____________");
					}
				});
				updateBlueConnectState();
			} else if (action.equals(LiteBlueService.LITE_SERVICE_DISCOVERED)) {
				// final BluetoothDevice device =
				// intent.getExtras().getParcelable(LiteBlueService.EXTRA_DEVICE);
				final BluetoothDevice device = mLiteBlueService.getCurrentBluetoothDevice();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						SharedPreferenceUtil.put(SettingBindedDeviceActivity.this, Constant.SHARE_CURRENT_DEVICE_NAME, device.getName());
						SharedPreferenceUtil.put(SettingBindedDeviceActivity.this, Constant.SHARE_CURRENT_DEVICE_ADDRESS, device.getAddress());
						setCurrentDevice(device);

						writeIncoomingData();
					}
				});
				updateBlueConnectState();
			} else if (action.equals(LiteBlueService.LITE_GATT_CONNECTTING)) {
				updateBlueConnectState();
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
				byte[] value = intent.getExtras().getByteArray(LiteBlueService.EXTRA_VALUE);
				Log.v("SettingBindedDeviceActivity", "______________________________byte[] value = : " + value);
			}
		}
	};
	private ProgressBar mProgress;
	private ImageView imgDisconnectDevice;
	private Handler mHandler = new Handler() {

	};

	private void openBluetooth() {
		if (!mLiteBlueService.isEnable()) {
			Toast.makeText(SettingBindedDeviceActivity.this, "请打开蓝牙", Toast.LENGTH_SHORT).show();
			// mLiteBlueService.enable(MainActivity.this);
		} else {
			if (mLiteBlueService != null) {
				mLiteBlueService.stopScanUsePeriodScanCallback();
				mLiteBlueService.startScanUsePeriodScanCallback();
				isScanning = true;
				updateScanning();
			}
		}
	}

	private void writeIncoomingData() {
		BaseApp app = (BaseApp) getApplication();
		IncomingService incomingService = app.getIncomingService();
		incomingService.doWriteToWatch(mLiteBlueService);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_binded_device);
		mLiteBlueService = getLiteBlueService();
		initTitle();
		initViews();
		setListener();

	}

	@Override
	protected void onResume() {

		openBluetooth();
		// lite
		IntentFilter filter = LiteBlueService.getIntentFilter();
		registerReceiver(mReceiver, filter);
		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// startScan();
		// isScanning = true;
		// updateScanning();
		// }
		// }, 500);

		BluetoothDevice device = mLiteBlueService.getCurrentBluetoothDevice();
		if (mLiteBlueService.getCurrentBluetoothDevice() != null) {
			setCurrentDevice(device);
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		stopScan();
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void imgConfirm() {
		updateScanning();
		if (isScanning) {
			stopScan();
		} else {
			startScan();
		}
		isScanning = !isScanning;
		updateScanning();
	}

	@Override
	public void imgBack() {
		finish();
	}

	@Override
	public void setTitle() {
		tvTitle.setText(getResources().getString(R.string.binding_equipment));
		imgConfirm.setImageResource(R.drawable.selector_state_bluetooth);
	}

	@Override
	public void initViews() {
		lvDevices = (ListView) findViewById(R.id.lv_devices);
		tvCurrentDeviceName = (TextView) findViewById(R.id.tv_current_device_name);
		tvCurrentDeviceAdress = (TextView) findViewById(R.id.tv_current_device_adress);
		Log.d("SettingBindedDeviceActivity", "_________________________________mLiteBlueService : " + mLiteBlueService);
		setCurrentDevice(mLiteBlueService.getCurrentBluetoothDevice());

		imgDisconnectDevice = (ImageView) findViewById(R.id.img_disconnect_device);
		tvConnectInfo = (TextView) findViewById(R.id.tv_connect_info);
		mProgress = (ProgressBar) findViewById(R.id.progress_blue);

		// 标题背景颜色
		findViewById(R.id.top_title).setBackgroundColor(getResources().getColor(R.color.blue));
		devices = new ArrayList<BluetoothDevice>();
		mAdapter = new MyAdapter();
		lvDevices.setAdapter(mAdapter);

	}

	@Override
	public void setListener() {
		lvDevices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int location, long arg3) {
				BluetoothDevice device = devices.get(location);
				mLiteBlueService.connnect(device);
				stopScan();
				isScanning = false;
				updateScanning();
			}
		});

		imgDisconnectDevice.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_disconnect_device:
			if (mLiteBlueService.getCurrentBluetoothDevice() != null) {
				final ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
				dialog.setTitle("解除绑定？");
				dialog.addSheetItem("解除绑定", SheetItemColor.Red, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						mLiteBlueService.closeAll();
						mLiteBlueService.setCurrentBluetoothDevice(null);

						SharedPreferenceUtil.put(SettingBindedDeviceActivity.this, Constant.SHARE_BINDING_DEVICE_NAME, null);
						SharedPreferenceUtil.put(SettingBindedDeviceActivity.this, Constant.SHARE_BINDING_DEVICE_ADRESS, null);

						setCurrentDevice(null);
						startScan();
						isScanning = true;
						updateScanning();
					}
				}).show();
			} else {
				toast("没有设备绑定...");
			}
			// mLiteBlueService.closeAll();
			// startScan();
			// isScanning = true;
			// updateScanning();
			break;
		default:
			break;
		}
	}

	private void updateScanning() {
		if (!isScanning) {
			mProgress.setVisibility(View.INVISIBLE);
		} else {
			mProgress.setVisibility(View.VISIBLE);
		}
	}

	private void setCurrentDevice(BluetoothDevice device) {
		if (device == null) {
			tvCurrentDeviceName.setText("无连接");
			tvCurrentDeviceAdress.setText("无连接");
		} else {
			tvCurrentDeviceName.setText("[" + device.getName() + "]");
			tvCurrentDeviceAdress.setText("[" + device.getAddress() + "]");
		}
	}

	private void updateBlueConnectState() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvConnectInfo.setText(mLiteBlueService.getCurrentState().getMessage());
			}
		});
	}

	private void stopScan() {
		if (mLiteBlueService != null) {
			// mLiteBlueService.stopScan();
			mLiteBlueService.stopScanUsePeriodScanCallback();
			devices.clear();
			mAdapter.notifyDataSetChanged();
		}
	}

	private void startScan() {
		if (mLiteBlueService != null) {
			// mLiteBlueService.stopScan();
			// mLiteBlueService.startScan();
			mLiteBlueService.stopScanUsePeriodScanCallback();
			mLiteBlueService.startScanUsePeriodScanCallback();
		}
	}

	/**
	 * Devices Adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return devices.size();
		}

		@Override
		public Object getItem(int position) {
			return devices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.e("", "--------------------------------");
			ViewHolder vh;
			if (convertView == null) {
				convertView = LayoutInflater.from(SettingBindedDeviceActivity.this).inflate(R.layout.item_bluetooth, parent, false);
				vh = new ViewHolder();
				vh.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				vh.tvAdress = (TextView) convertView.findViewById(R.id.tv_adress);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.tvName.setText(devices.get(position).getName());
			vh.tvAdress.setText(devices.get(position).getAddress());
			return convertView;
		}

		class ViewHolder {
			TextView tvName;
			TextView tvAdress;
		}
	}

}
