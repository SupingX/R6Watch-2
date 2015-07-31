package com.mycj.healthy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import com.mycj.healthy.fragment.CountFragment;
import com.mycj.healthy.fragment.InformationFragment;
import com.mycj.healthy.fragment.SettingFragment;
import com.mycj.healthy.service.IncomingService;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private ViewPager mViewPager;
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
			if (action.equals(LiteBlueService.LITE_CHARACTERISTIC_CHANGED)) {
				byte[] value = intent.getExtras().getByteArray(LiteBlueService.EXTRA_VALUE);
				Log.v("MainActivity", "______________________________byte[] value = : " + DataUtil.getStringByBytes(value));
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
		initViews();
		setListener();
		mRadioGroup.check(R.id.rl_infomation);
		IntentFilter filter = LiteBlueService.getIntentFilter();
		registerReceiver(mReceiver, filter);
		mLiteBlueService = ((BaseApp) getApplication()).getLiteBlueService();

	}

	private void firstEnter() {
		if (!mLiteBlueService.isEnable()) {
			Toast.makeText(MainActivity.this, "请打开蓝牙", Toast.LENGTH_SHORT).show();
			// mLiteBlueService.enable(MainActivity.this);
		} else {
			if (mLiteBlueService != null) {
				mLiteBlueService.stopScanUsePeriodScanCallback();
				mLiteBlueService.startScanUsePeriodScanCallback();
			}
		}
	}

	@Override
	protected void onResume() {
		firstEnter();
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_infomation:
			mRadioGroup.check(R.id.rl_infomation);

			break;
		case R.id.rl_count:
			mRadioGroup.check(R.id.rl_count);
			break;
		case R.id.rl_setting:
			mRadioGroup.check(R.id.rl_setting);
			break;

		default:
			break;
		}
	}

	private void initViews() {
		// viewPager
		mViewPager = (ViewPager) findViewById(R.id.vp);
		informationFrag = new InformationFragment();
		countFrag = new CountFragment();
		settingFrag = new SettingFragment();
		// listFrags = new ArrayList<>();
		// listFrags.add(informationFrag);
		// // listFrags.add(informationFrag2);
		// listFrags.add(countFrag);
		// listFrags.add(settingFrag);

		mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				// return listFrags.size();
				return 3;
			}

			@Override
			public Fragment getItem(int position) {
				// return listFrags.get(position);
				switch (position) {
				case 0:
					if (informationFrag == null) {
						informationFrag = new InformationFragment();
					}
					return informationFrag;
				case 1:
					if (countFrag == null) {
						countFrag = new CountFragment();
					}
					return countFrag;
				case 2:
					if (settingFrag == null) {
						settingFrag = new SettingFragment();
					}
					return settingFrag;
				default:
					break;
				}
				return null;
			}
		});
		mViewPager.setCurrentItem(0);
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
		setRadioGroupListener();
		setViewpagerListener();
	}

	/**
	 * 底部radioRroup 选中事件
	 */
	private void setRadioGroupListener() {
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rl_infomation:
					Log.e("redioGroup", "我的状态 :" + (mRadioGroup.getCheckedRadioButtonId() == R.id.rl_infomation));
					mViewPager.setCurrentItem(0);
					break;
				case R.id.rl_count:
					Log.e("redioGroup", "统计" + mRadioGroup.getCheckedRadioButtonId());
					mViewPager.setCurrentItem(1);
					break;
				case R.id.rl_setting:
					Log.e("redioGroup", "设置" + mRadioGroup.getCheckedRadioButtonId());
					mViewPager.setCurrentItem(2);
					break;

				default:
					break;
				}
				updateRadioGroupChecked();
			}
		});
	}

	/**
	 * viewPager 切换页面事件
	 */
	private void setViewpagerListener() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					mRadioGroup.check(R.id.rl_infomation);
					break;
				case 1:
					mRadioGroup.check(R.id.rl_count);
					break;
				case 2:
					mRadioGroup.check(R.id.rl_setting);
					break;

				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	/**
	 * 底部radioGroup视图改变
	 */
	private void updateRadioGroupChecked() {
		int selected = getResources().getColor(R.color.blue);
		switch (mRadioGroup.getCheckedRadioButtonId()) {
		case R.id.rl_infomation:
			tvBottomInformation.setTextColor(selected);
			imgBottomInformation.setImageResource(R.drawable.my_state_button_on);
			updateCurrentItem();
			currentId = R.id.rl_infomation;
			break;
		case R.id.rl_count:
			tvBottomCount.setTextColor(selected);
			imgBottomCount.setImageResource(R.drawable.statistics_button_on);
			updateCurrentItem();
			currentId = R.id.rl_count;
			break;
		case R.id.rl_setting:
			tvBottomSetting.setTextColor(selected);
			imgBottomSetting.setImageResource(R.drawable.setting_button_on);
			updateCurrentItem();
			currentId = R.id.rl_setting;
			break;
		default:

			break;
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
