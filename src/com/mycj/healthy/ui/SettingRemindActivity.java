package com.mycj.healthy.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.R.drawable;
import com.mycj.healthy.R.id;
import com.mycj.healthy.R.layout;
import com.mycj.healthy.view.OnOffButton;

/**
 * 弃用
 */
public class SettingRemindActivity extends BaseSettingActivity implements OnClickListener {
	private NumberPicker npReminderHour, npReminderMin;
	private OnOffButton btnRemind;
	private ImageView imgRemindType;
	private TextView tvRemind;
	private TextView tvSun, tvMon, tvTue, tvWen, tvThu, tvFri, tvSat;
	private int currentWeekday = 0b1111111;

	private enum Weekday {
		SUN(0b1000000), MON(0b0100000), TUE(0b0010000), WEN(0b0001000), THU(0b0000100), FRI(0b0000010), SAT(0b0000001);

		private int value;

		private Weekday(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_remind);
		initTitle();
		initViews();
		setListener();
	}

	public void setListener() {
		btnRemind.setOnClickListener(this);
		imgRemindType.setOnClickListener(this);
		tvSun.setOnClickListener(this);
		tvMon.setOnClickListener(this);
		tvTue.setOnClickListener(this);
		tvWen.setOnClickListener(this);
		tvThu.setOnClickListener(this);
		tvFri.setOnClickListener(this);
		tvSat.setOnClickListener(this);
	}

	public void initViews() {
		btnRemind = (OnOffButton) findViewById(R.id.btn_remind);
		imgRemindType = (ImageView) findViewById(R.id.img_remind_type);
		tvRemind = (TextView) findViewById(R.id.tv_remind);
		tvSun = (TextView) findViewById(R.id.tv_sun);
		tvMon = (TextView) findViewById(R.id.tv_mon);
		tvTue = (TextView) findViewById(R.id.tv_tue);
		tvWen = (TextView) findViewById(R.id.tv_wen);
		tvThu = (TextView) findViewById(R.id.tv_thu);
		tvFri = (TextView) findViewById(R.id.tv_fri);
		tvSat = (TextView) findViewById(R.id.tv_sat);

		// numberPicker
		npReminderHour = (NumberPicker) findViewById(R.id.np_remind_hour);
		npReminderMin = (NumberPicker) findViewById(R.id.np_remind_min);

		npReminderHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npReminderHour.setMaxValue(23);
		npReminderHour.setMinimumHeight(0);

		npReminderMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npReminderMin.setMaxValue(59);
		npReminderMin.setMinimumHeight(0);
	}

	@Override
	public void imgConfirm() {
		finish();
	}

	@Override
	public void imgBack() {
		finish();
	}

	@Override
	public void setTitle() {
		tvTitle.setText("事件提醒");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_sun:
			currentWeekday ^= Weekday.SUN.getValue();
			Log.e("", "currentWeekday : " + Integer.toBinaryString(currentWeekday));
			if ((currentWeekday & Weekday.SUN.getValue()) == Weekday.SUN.getValue()) {
				tvSun.setBackground(getResources().getDrawable(R.drawable.bg_weekday_start_unselected));
			}
			break;
		case R.id.tv_mon:

			break;
		case R.id.tv_tue:

			break;
		case R.id.tv_wen:

			break;
		case R.id.tv_thu:

			break;
		case R.id.tv_fri:

			break;
		case R.id.tv_sat:

			break;

		default:
			break;
		}
	}

}
