package com.mycj.healthy.ui;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.ActionSheetDialog;
import com.mycj.healthy.view.ActionSheetDialog.OnCancelClickListener;
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

	private ProgressBar mProgress;
	private ActionSheetDialog dialog;
	private ImageView imgDisconnectDevice;
	private LinearLayout llBindedDevice;
	private static Handler mHandler = new Handler() {
	
	};
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_DEVICE_FOUND)) {
				BluetoothDevice device = intent.getExtras().getParcelable(LiteBlueService.EXTRA_DEVICE);
				if (!devices.contains(device) 
						&& !device.getName().equals(SharedPreferenceUtil.get(SettingBindedDeviceActivity.this, Constant.SHARE_BINDING_DEVICE_NAME, ""))
						&& !device.getAddress().equals(SharedPreferenceUtil.get(SettingBindedDeviceActivity.this, Constant.SHARE_BINDING_DEVICE_ADRESS, ""))
						) {
					devices.add(device);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
			} else if (action.equals(LiteBlueService.LITE_GATT_CONNECTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateBlueConnectState(1);
						imgDisconnectDevice.setImageResource(R.drawable.ic_action_about);
					}
				});

			} else if (action.endsWith(LiteBlueService.LITE_GATT_DISCONNECTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateBlueConnectState(2);
						imgDisconnectDevice.setImageResource(R.drawable.ic_action_about);
					}
				});
			} else if (action.equals(LiteBlueService.LITE_SERVICE_DISCOVERED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setBindedDevice();
						updateBlueConnectState(1);
						imgDisconnectDevice.setImageResource(R.drawable.ic_action_about_on);
					}
				});
			} else if (action.equals(LiteBlueService.LITE_GATT_CONNECTTING)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateBlueConnectState(3);
						imgDisconnectDevice.setImageResource(R.drawable.ic_action_about);
					}
				});
			} else if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
				byte[] value = intent.getExtras().getByteArray(LiteBlueService.EXTRA_VALUE);
				Log.v("SettingBindedDeviceActivity", "______________________________byte[] value = : " + value);
			}
		}
	};
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
		IntentFilter filter = LiteBlueService.getIntentFilter();
		registerReceiver(mReceiver, filter);
		checkBlue();
		
		setBindedDevice();
		if (mLiteBlueService.isConnetted()) {
			updateBlueConnectState(1);
		} else {
			updateBlueConnectState(2);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (dialog != null) {
			dialog.dismiss();
		}
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
		// setCurrentDevice(mLiteBlueService.getCurrentBluetoothDevice());

		imgDisconnectDevice = (ImageView) findViewById(R.id.img_disconnect_device);
		tvConnectInfo = (TextView) findViewById(R.id.tv_connect_info);
		mProgress = (ProgressBar) findViewById(R.id.progress_blue);

		llBindedDevice = (LinearLayout) findViewById(R.id.ll_binded_device);
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
				final BluetoothDevice device = devices.get(location);
				mLiteBlueService.closeAll();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						stopScan();
						isScanning = false;
						updateScanning();
						mLiteBlueService.connnect(device);
						
					}
				},3000);
//				stopScan();
//				isScanning = false;
//				updateScanning();
				
//				mLiteBlueService.connnect(device);
			
			
			}
		});

		imgDisconnectDevice.setOnClickListener(this);
		llBindedDevice.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_disconnect_device:
			if (isBinded()) {
				final ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
				dialog.setTitle(getResources().getString(R.string.contact_binding));
				dialog.addSheetItem(getResources().getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						runOnUiThread(new Runnable() {
							public void run() {
								mLiteBlueService.closeAll();
								mLiteBlueService.setCurrentBluetoothDevice(null);
								SharedPreferenceUtil.put(SettingBindedDeviceActivity.this, Constant.SHARE_BINDING_DEVICE_NAME, "");
								SharedPreferenceUtil.put(SettingBindedDeviceActivity.this, Constant.SHARE_BINDING_DEVICE_ADRESS, "");
								setBindedDevice();
								startScan();
								isScanning = true;
								updateScanning();
								tvConnectInfo.setText(getResources().getString(R.string.disconnect));
							}
						});

					}
				}).show();
			} else {
				toast(getResources().getString(R.string.no_device_binding));
			}
			// mLiteBlueService.closeAll();
			// startScan();
			// isScanning = true;
			// updateScanning();
			break;
		/*
		 * case R.id.ll_binded_device: String mac =
		 * (String)SharedPreferenceUtil.get(this,
		 * Constant.SHARE_BINDING_DEVICE_ADRESS, ""); if
		 * (mac!=null&&!mac.equals("")) { if (mLiteBlueService.isEnable()) { if
		 * (mLiteBlueService.isConnetted()) { if
		 * (mLiteBlueService.getCurrentBluetoothDevice()!=null) { if
		 * (!mLiteBlueService
		 * .getCurrentBluetoothDevice().getAddress().equals(mac)) {
		 * mLiteBlueService.connnect(mac); } } } } } break;
		 */
		default:
			break;
		}
	}

	
	/**
	 * 检查蓝牙是否打开
	 */
	private void checkBlue() {
		if (mLiteBlueService != null) {
			if (!mLiteBlueService.isEnable()) {
				showDialog();
			} else {
				mLiteBlueService.startScanUsePeriodScanCallback();
			}
		}
	}
	
	/**
	 * 搜索状态更新
	 */
	private void updateScanning() {
		if (!isScanning) {
			mProgress.setVisibility(View.INVISIBLE);
		} else {
			mProgress.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 设置当前绑定的设备
	 */
	private void setBindedDevice() {
		String name = (String) SharedPreferenceUtil.get(this, Constant.SHARE_BINDING_DEVICE_NAME, "");
		String address = (String) SharedPreferenceUtil.get(this, Constant.SHARE_BINDING_DEVICE_ADRESS, "");
		String noBinding = getResources().getString(R.string.no_binded);
		tvCurrentDeviceName.setText(name.equals("") ? noBinding: name);
		tvCurrentDeviceAdress.setText(address.equals("") ? noBinding: address);
	}
	
	/**
	 * 是否绑定
	 * @return
	 */
	public boolean isBinded() {
		String name = (String) SharedPreferenceUtil.get(this, Constant.SHARE_BINDING_DEVICE_NAME, "");
		String address = (String) SharedPreferenceUtil.get(this, Constant.SHARE_BINDING_DEVICE_ADRESS, "");
		if (!name.equals("") && !address.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 更新设备连接状态
	 * @param state
	 */
	private void updateBlueConnectState(int state) {
		switch (state) {
		case 1:
			if (isBinded()) {
				tvConnectInfo.setText(getResources().getString(R.string.binded));
				imgDisconnectDevice.setImageResource(R.drawable.ic_action_about_on);
			} else {
				tvConnectInfo.setText(getResources().getString(R.string.device_not_match));
				startScan();
				isScanning = true;
				updateScanning();
			}
			break;
		case 2:
			tvConnectInfo.setText(getResources().getString(R.string.connect_disconnect));
			startScan();
			isScanning = true;
			updateScanning();
			break;
		case 3:
			tvConnectInfo.setText(getResources().getString(R.string.being_connected));
			break;

		default:
			break;
		}
	}

	private void stopScan() {
		if (mLiteBlueService != null) {
			mLiteBlueService.stopScanUsePeriodScanCallback();
			devices.clear();
			mAdapter.notifyDataSetChanged();
		}
	}

	private void startScan() {
		if (mLiteBlueService != null) {
			mLiteBlueService.stopScanUsePeriodScanCallback();
			mLiteBlueService.startScanUsePeriodScanCallback();
		}
	}

	// private void openBluetooth() {
	// if (!mLiteBlueService.isEnable()) {
	// Toast.makeText(SettingBindedDeviceActivity.this, "请打开蓝牙",
	// Toast.LENGTH_SHORT).show();
	// // mLiteBlueService.enable(MainActivity.this);
	// } else {
	// if (mLiteBlueService != null) {
	// mLiteBlueService.stopScanUsePeriodScanCallback();
	// mLiteBlueService.startScanUsePeriodScanCallback();
	// isScanning = true;
	// updateScanning();
	// }
	// }
	// }
	
	private void showDialog() {
		dialog = new ActionSheetDialog(this).builder();
		dialog.setTitle(getResources().getString(R.string.open_blue));
		dialog.setOnCancelClickListener(new OnCancelClickListener() {
	
			@Override
			public void onClick() {
				finish();
			}
		});
		dialog.addSheetItem(getResources().getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mLiteBlueService.isEnable()) {
							// 蓝牙打开
							mLiteBlueService.startScanUsePeriodScanCallback();
						} else {
							// 未打开
							finish();
						}
					}
				}, 5000);
				mLiteBlueService.enable();
			}
		}).show();
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
