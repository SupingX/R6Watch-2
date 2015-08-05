package com.mycj.healthy;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.service.LiteBlueService;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

	public LiteBlueService getLiteBlueService() {
		return ((BaseApp) getApplication()).getLiteBlueService();
	}

	public void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	public void toastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	public void toastNotConnectted() {
		Toast.makeText(this, "未连接手环...", Toast.LENGTH_SHORT).show();
	}

	public abstract void initViews();

	public abstract void setListener();

	public int getAppVersion() {
		int version = 1;
		PackageManager manger = this.getPackageManager();
		try {
			PackageInfo info = manger.getPackageInfo(this.getPackageName(), 0);

			version = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	public boolean isConnected(LiteBlueService service) {
		return service.getCurrentState() != null && service.getCurrentState() == ConnectState.Connected;
	}
}
