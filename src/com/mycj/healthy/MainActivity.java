package com.mycj.healthy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.entity.HeartRateData;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.fragment.CountFragment;
import com.mycj.healthy.fragment.InformationFragment;
import com.mycj.healthy.fragment.SettingFragment;
import com.mycj.healthy.service.IncomingService;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.ui.SettingBindedDeviceActivity;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.ActionSheetDialog;
import com.mycj.healthy.view.ActionSheetDialog.OnCancelClickListener;
import com.mycj.healthy.view.ActionSheetDialog.OnSheetItemClickListener;
import com.mycj.healthy.view.ActionSheetDialog.SheetItemColor;
import com.mycj.healthy.view.MyAlertDialog;

public class MainActivity extends FragmentActivity implements OnClickListener {
//	private ViewPager mViewPager;
	private List<Fragment> listFrags;
	private RadioGroup mRadioGroup;
	private RelativeLayout mRelativeLayoutInformation;
	private RelativeLayout mRelativeLayoutCount;
	private RelativeLayout mRelativeLayoutSetting;
	private TextView tvBottomInformation, tvBottomCount, tvBottomSetting;
	private ImageView imgBottomInformation, imgBottomCount, imgBottomSetting;
	private int currentId;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LiteBlueService.LITE_BLUETOOTH_NOT_OPEN)) {
			} else if (action.equals(LiteBlueService.LITE_SERVICE_DISCOVERED)) {
				runOnUiThread( new Runnable() {
					public void run() {
						showDialogSyncSetting();
//						mHander.sendEmptyMessage(9);
						if (ProtoclData.syncSetting(mLiteBlueService, MainActivity.this)) {
							dialogSyncSetting.dismiss();
						}
					}
				});
			}
		}
	};

	private InformationFragment informationFrag;
	private CountFragment countFrag;
	private SettingFragment settingFrag;
	private LiteBlueService mLiteBlueService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLiteBlueService = ((BaseApp) getApplication()).getLiteBlueService();
		// if(mLiteBlueService.getCurrentState()!=null&&mLiteBlueService.getCurrentState()==ConnectState.Connected){
		// syncSetting();
		// }
		firstEnter();
		initViews();
		setListener();
		setCurrentItem(0);
		IntentFilter filter = LiteBlueService.getIntentFilter();
		registerReceiver(mReceiver, filter);

	}


	private void firstEnter() {
		if (mLiteBlueService != null) {
			if (!mLiteBlueService.isEnable()) {
				showDialog();
			} else {
				mLiteBlueService.startScanUsePeriodScanCallback();
			}
		}
	}

	private Handler mHander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 9:
//				if (ProtoclData.syncSetting(mLiteBlueService, MainActivity.this)) {
//					dialogSyncSetting.dismiss();
//				}
				break;

			default:
				break;
			}

		};
	};
	
	
	
	private void showDialog() {
		dialog = new ActionSheetDialog(this).builder();
		dialog.setTitle("打开蓝牙？");
		dialog.addSheetItem("打开", SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {

				mHander.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mLiteBlueService.isEnable()) {
							// 蓝牙打开
							mLiteBlueService.startScanUsePeriodScanCallback();
						} else {
							// 未打开
						}
					}
				}, 5000);
				mLiteBlueService.enable();
			}
		}).show();
	}

	private void showDialogSyncSetting() {
		dialogSyncSetting = new com.mycj.healthy.view.MyAlertDialog(this).builder();
		dialogSyncSetting.setMsg("正在同步手机设置...");
		dialogSyncSetting.show();
	}

	@Override
	protected void onResume() {
		// firstEnter();

		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialogSyncSetting!=null) {
			dialogSyncSetting.dismiss();
		}
		if (dialogSyncSetting!=null) {
			dialogSyncSetting.dismiss();
		}
		unregisterReceiver(mReceiver);

	}
	
//	private long lastTime;
	
	@Override
	public void onClick(View v) {
		//禁止多次点击
//		long time = System.currentTimeMillis();
//		long diff  = time - lastTime;
//		if (diff<250) {
//			return;
//		}
		switch (v.getId()) {
		case R.id.rl_infomation:
			setCurrentItem(0);
			break;
		case R.id.rl_count:

			setCurrentItem(1);
			List<HistoryData> list = HistoryData.findAll(HistoryData.class);
			if (list != null) {
				Log.v("", "==================list : " + list.size());
				for (HistoryData hd : list) {
					Log.v("", hd.getHistoryDate() + "<-------------->" + hd.getStep());
				}
			}

			// List<HistoryData> historyDatas =
			// DataSupport.where("strftime('%Y.%m.%d','historyDate')=?",sdf.format(new
			// Date())).find(HistoryData.class);
			List<HistoryData> historyDatas = DataSupport.where("historyDate=?", "20150621").find(HistoryData.class);
			Log.v("", ">>>>>>>>>>>historyDatas : " + historyDatas);
			if (historyDatas != null) {
				Log.v("", ">>>>>>>>>>>historyDatas : " + historyDatas.size());
				for (HistoryData hd : historyDatas) {
					Log.v("", hd.getHistoryDate() + "<-------------->" + hd.getStep());
				}
			}

			//
			int sum = DataSupport.where("substr(historyDate,1,6)=?", "201506").sum(HistoryData.class, "step", Integer.class);
			Log.v("", "<------sum-------->" + sum);
			break;
		case R.id.rl_setting:
			setCurrentItem(2);
	
			break;
		default:
			break;
		}
//		lastTime = time;
	}

	@Override
		public void onBackPressed() {
			ActionSheetDialog exitDialog = new ActionSheetDialog(this).builder();
			exitDialog.setTitle("退出程序？");
			exitDialog.addSheetItem("确定", SheetItemColor.Red, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					finish();
				}
			}).show();
	//		super.onBackPressed();
		}

	private MyAlertDialog dialogSyncSetting;
	private ActionSheetDialog dialog;
	private InformationFragment informationFragment;
	private FragmentTransaction beginTransaction;
	private SettingFragment settingFragment;
	private CountFragment countFragment;

	private void initViews() {
		// viewPager
//		mViewPager = (ViewPager) findViewById(R.id.vp);
//		informationFrag = new InformationFragment();
//		countFrag = new CountFragment();
//		settingFrag = new SettingFragment();
//		listFrags = new ArrayList<>();
//		listFrags.add(informationFrag);
//		listFrags.add(countFrag);
//		listFrags.add(settingFrag);
		
		
//
//		mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
//			@Override
//			public int getCount() {
//				// return listFrags.size();
//				return 3;
//			}
//
//			@Override
//			public Fragment getItem(int position) {
//
//				Fragment f = listFrags.get(position);
//				// Bundle b = new Bundle();
//				// b.putString("adress", adress);
//				// f.setArguments(b);
//				return f;
//				// return listFrags.get(position);
//				// switch (position) {
//				// case 0:
//				// if (informationFrag == null) {
//				// informationFrag = new InformationFragment();
//				// }
//				// return informationFrag;
//				// case 1:
//				// if (countFrag == null) {
//				// countFrag = new CountFragment();
//				// }
//				// return countFrag;
//				// case 2:
//				// if (settingFrag == null) {
//				// settingFrag = new SettingFragment();
//				// }
//				// return settingFrag;
//				// default:
//				// break;
//				// }
//				// return null;
//			}
//		});
//		mViewPager.setOffscreenPageLimit(3);
//		mViewPager.setCurrentItem(0);
////		mViewPager.setPageTransformer(true, new PageTransformer() {
////
////			@Override
////			public void transformPage(View view, float position) {
////				int pageWidth = view.getWidth();
////
////				if (position < -1) { // [-Infinity,-1)
////					// This page is way off-screen to the left.
////					view.setAlpha(0);
////
////				} else if (position <= 0) { // [-1,0]
////					// Use the default slide transition when moving to the left
////					// page
////					view.setAlpha(1);
////					view.setTranslationX(0);
////					view.setScaleX(1);
////					view.setScaleY(1);
////
////				} else if (position <= 1) { // (0,1]
////					// Fade the page out.
////					view.setAlpha(1 - position);
////
////					// Counteract the default slide transition
////					view.setTranslationX(pageWidth * -position);
////
////					// Scale the page down (between MIN_SCALE and 1)
////					float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
////					view.setScaleX(scaleFactor);
////					view.setScaleY(scaleFactor);
////
////				} else { // (1,+Infinity]
////					// This page is way off-screen to the right.
////					view.setAlpha(0);
////				}
////			}
////		});

		initBottom();

	}

	private void initBottom() {
		// 底部Bottom
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_bottom);
		mRelativeLayoutInformation = (RelativeLayout) findViewById(R.id.rl_infomation);
		mRelativeLayoutCount = (RelativeLayout) findViewById(R.id.rl_count);
		mRelativeLayoutSetting = (RelativeLayout) findViewById(R.id.rl_setting);
		tvBottomCount = (TextView) findViewById(R.id.tv_count);
		tvBottomSetting = (TextView) findViewById(R.id.tv_setting);
		tvBottomInformation = (TextView) findViewById(R.id.tv_infomation);
		imgBottomInformation = (ImageView) findViewById(R.id.img_infomation);
		imgBottomCount = (ImageView) findViewById(R.id.img_count);
		imgBottomSetting = (ImageView) findViewById(R.id.img_setting);
	}

	/**
	 * 为各个view添加listener
	 */
	private void setListener() {
		mRelativeLayoutInformation.setOnClickListener(this);
		mRelativeLayoutCount.setOnClickListener(this);
		mRelativeLayoutSetting.setOnClickListener(this);
//		setRadioGroupListener();
//		setViewpagerListener();
	}

	/**
	 * 底部radioRroup 选中事件
	 */
//	private void setRadioGroupListener() {
//		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				switch (checkedId) {
//				case R.id.rl_infomation:
//					Log.e("redioGroup", "我的状态 :" + (mRadioGroup.getCheckedRadioButtonId() == R.id.rl_infomation));
////					mViewPager.setCurrentItem(0);
//					break;
//				case R.id.rl_count:
//					Log.e("redioGroup", "统计" + mRadioGroup.getCheckedRadioButtonId());
////					mViewPager.setCurrentItem(1);
//					break;
//				case R.id.rl_setting:
//					Log.e("redioGroup", "设置" + mRadioGroup.getCheckedRadioButtonId());
////					mViewPager.setCurrentItem(2);
//					break;
//
//				default:
//					break;
//				}
//				updateRadioGroupChecked();
//			}
//		});
//	}

//	/**
//	 * viewPager 切换页面事件
//	 */
//	private void setViewpagerListener() {
//		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int arg0) {
//				switch (arg0) {
//				case 0:
//					mRadioGroup.check(R.id.rl_infomation);
//					break;
//				case 1:
//					mRadioGroup.check(R.id.rl_count);
//					break;
//				case 2:
//					mRadioGroup.check(R.id.rl_setting);
//					break;
//
//				default:
//					break;
//				}
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//			}
//		});
//	}

//	/**
//	 * 底部radioGroup视图改变
//	 */
//	private void updateRadioGroupChecked() 
//		int selected = getResources().getColor(R.color.blue);
//		switch (mRadioGroup.getCheckedRadioButtonId()) {
//		case R.id.rl_infomation:
//			setCurrentItem(0);
//		
//			break;
//		case R.id.rl_count:
//			setCurrentItem(1);
//			tvBottomCount.setTextColor(selected);
//			imgBottomCount.setImageResource(R.drawable.statistics_button_on);
//			break;
//		case R.id.rl_setting:
//			setCurrentItem(2);
//			tvBottomSetting.setTextColor(selected);
//			imgBottomSetting.setImageResource(R.drawable.setting_button_on);
//			break;
//		default:
//
//			break;
//		}
//	}
	
	private void  clearItem(){
		int unselected = getResources().getColor(R.color.grey_light);
		tvBottomInformation.setTextColor(unselected);
		imgBottomInformation.setImageResource(R.drawable.my_state_button);
		tvBottomCount.setTextColor(unselected);
		imgBottomCount.setImageResource(R.drawable.statistics_button);
		tvBottomSetting.setTextColor(unselected);
		imgBottomSetting.setImageResource(R.drawable.setting_button);
		
	}
	
	private void setCurrentItem(int id){
		try {
			clearItem();
			beginTransaction = getSupportFragmentManager().beginTransaction();
			int selected = getResources().getColor(R.color.blue);
			switch (id) {
			case 0:
				tvBottomInformation.setTextColor(selected);
				imgBottomInformation.setImageResource(R.drawable.my_state_button_on);
				if (informationFragment == null) {
					informationFragment = new InformationFragment();
				}
				beginTransaction.replace(R.id.frame_main, informationFragment);
				break;
			case 1:
				tvBottomCount.setTextColor(selected);
				imgBottomCount.setImageResource(R.drawable.statistics_button_on);
				if (countFragment == null) {
					countFragment = new CountFragment();
				}
				beginTransaction.replace(R.id.frame_main, countFragment);
				break;
			case 2:
				tvBottomSetting.setTextColor(selected);
				imgBottomSetting.setImageResource(R.drawable.setting_button_on);
				if (settingFragment == null) {
					settingFragment = new SettingFragment();
				}
				beginTransaction.replace(R.id.frame_main, settingFragment);
				break;

			default:
				break;
			}
			beginTransaction.addToBackStack(null);
			beginTransaction.commitAllowingStateLoss();
//			beginTransaction.commit();
		} catch (Exception e) {
		}
	}
	
	/**
	 * radioGroup中checked的元素 由【选中】 改变 成【未选中】状态
	 */
	private void updateCurrentItem() {
		int unselected = getResources().getColor(R.color.grey_light);
		switch (currentId) {
		case R.id.rl_infomation:
			tvBottomInformation.setTextColor(unselected);
			imgBottomInformation.setImageResource(R.drawable.my_state_button);
			break;
		case R.id.rl_count:
			tvBottomCount.setTextColor(unselected);
			imgBottomCount.setImageResource(R.drawable.statistics_button);
			break;
		case R.id.rl_setting:
			tvBottomSetting.setTextColor(unselected);
			imgBottomSetting.setImageResource(R.drawable.setting_button);
			break;

		default:
			break;
		}
	}
}
