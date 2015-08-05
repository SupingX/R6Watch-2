package com.mycj.healthy.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.litesuits.bluetooth.conn.ConnectState;
import com.mycj.healthy.BaseSettingActivity;
import com.mycj.healthy.R;
import com.mycj.healthy.service.LiteBlueService;
import com.mycj.healthy.util.Constant;
import com.mycj.healthy.util.DataUtil;
import com.mycj.healthy.util.ProtoclData;
import com.mycj.healthy.util.SharedPreferenceUtil;
import com.mycj.healthy.view.ClickOnOffButton;
import com.mycj.healthy.view.ColorSeekBar;
import com.mycj.healthy.view.OnOffButton;
import com.mycj.healthy.view.StepSeekBar;

public class SettingStepGoalActivity extends BaseSettingActivity implements OnClickListener {
	// private OnOffButton btnOnoff;
	// private ClickOnOffButton btnClickOnoff;

	private CheckBox cbStep;
	private TextView tvInfo;
	private EditText etStep;
	private SeekBar sbStep;
	private int MAX = 99990;
	private int MIN = 500;
	private ColorSeekBar colorSeekBarStepGoal;
	private int goal;
	private LiteBlueService mLiteBlueService;
//	private TextWatcher mTextWatcher = new TextWatcher() {
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//		}
//
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//			int value = Integer.valueOf(etStep.getText().toString().trim());
//			if (value < MIN) {
//				toast("最低目标步数设置:500");
////				etStep.setText(String.valueOf(MIN));
//			} else if (value > MAX) {
//				etStep.setText(String.valueOf(MAX));
//				toast("最高目标步数设置:9990");
//			}
//		}
//	};
	private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				Message msg = mhandler.obtainMessage();
				msg.what = 1;
				mhandler.sendMessage(msg);
			}
			return false;
		}
	};

	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (progress <= MIN) {
				progress = MIN;
			}
			// 处理seekbar变化时的对应进度改变 EditText变化 以及运动评估
			etStep.setText("" + progress);
			colorSeekBarStepGoal.setProgress(progress);
			goal = progress;
			updateStepInfo(progress);
		}
	};

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String value = etStep.getText().toString().trim();
				int progress = Integer.valueOf(value);
				
				if (progress < MIN) {
					toast("最低目标步数设置:500");
					progress = MIN;
					etStep.setText(String.valueOf(MIN));
				} else if (progress > MAX) {
					etStep.setText(String.valueOf(MAX));
					progress = MAX;
					etStep.setText(String.valueOf(MAX));
					toast("最高目标步数设置:9990");
				}
				colorSeekBarStepGoal.setProgress(progress);
				sbStep.setProgress(progress);
				
				

				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};
	private boolean isChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_step_goal);
		mLiteBlueService = getLiteBlueService();
		initViews();
		setListener();
		initTitle();
		Log.e("", "version : " + getAppVersion());
	}

	public void initViews() {
		// btnOnoff = (OnOffButton) findViewById(R.id.btn_onoff);
		cbStep = (CheckBox) findViewById(R.id.cb_step);
		tvInfo = (TextView) findViewById(R.id.tv_info_step);
		etStep = (EditText) findViewById(R.id.ed_step);
		sbStep = (SeekBar) findViewById(R.id.sb_step);
		colorSeekBarStepGoal = (ColorSeekBar) findViewById(R.id.sk_step);
		colorSeekBarStepGoal.setMax(MAX);
		colorSeekBarStepGoal.setEnabled(false);
		sbStep.setMax(MAX);
		goal = (int) SharedPreferenceUtil.get(this, Constant.SHARE_STEP_GOAL, MIN);
		Log.e("", "取goal : " + goal);
		updateCheckBox();
		updateSeekBar();
		updateProgress();
		updateStepInfo(goal);

	}

	public void setListener() {
		sbStep.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		etStep.setOnEditorActionListener(mOnEditorActionListener);
//		etStep.addTextChangedListener(mTextWatcher);
		cbStep.setOnClickListener(this);
		cbStep.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cb_step:
			isChecked = !isChecked;
			cbStep.setChecked(isChecked);
			updateSeekBar();
			updateProgress();

			break;
		default:
			break;
		}
	}

	@Override
	public void imgConfirm() {
		Log.e("", "存goal : " + goal);
		SharedPreferenceUtil.put(SettingStepGoalActivity.this, Constant.SHARE_STEP_ON_OFF, isChecked);
		SharedPreferenceUtil.put(this, Constant.SHARE_STEP_GOAL, goal);
		if(goal<MIN){
			toast("最低目标步数设置:500");
			return;
		}if(goal>MAX){
			toast("最高目标步数设置:9990");
		}
		if (isConnected(mLiteBlueService)) {
			// if (mLiteBlueService.getCurrentState() != null &&
			// mLiteBlueService.getCurrentState() == ConnectState.Connected) {
			// mLiteBlueService.writeCharactics(ProtoclData.toByteForStepProtocl(sbStep.getProgress(),
			// isChecked ? 1 : 0));
			mLiteBlueService.writeCharacticsUseConnectListener(ProtoclData.toByteForStepProtocl(sbStep.getProgress(), isChecked ? 1 : 0));
		} else {
			toast("未连接手环...");
		}
		finish();
	}

	@Override
	public void imgBack() {
		finish();
	}

	@Override
	public void setTitle() {
		tvTitle.setText(getResources().getString(R.string.target_step_title));
	}

	/**
	 * 开关。
	 */
	private void updateCheckBox() {
		isChecked = (boolean) SharedPreferenceUtil.get(this, Constant.SHARE_STEP_ON_OFF, false);
		cbStep.setChecked(isChecked);
	}

	/**
	 * checkbox 关闭时 seekbar无效
	 */
	private void updateSeekBar() {
		if (!cbStep.isChecked()) {
			sbStep.setEnabled(false);
		} else {
			sbStep.setEnabled(true);
		}
	}

	/**
	 * checkbox 关闭时 没有进度
	 */
	private void updateProgress() {
		colorSeekBarStepGoal.setProgress(goal);
		sbStep.setProgress(goal);
		etStep.setText((goal <= MIN) ? "500" : (goal + ""));
	
	}

	private void updateStepInfo(int progress) {
		if (progress >= 0 && progress < 33330) {
			tvInfo.setText("运动评估：偏少");
		} else if (progress >= 33330 && progress <= 66660) {
			tvInfo.setText("运动评估：适中");
		} else if (progress > 66660) {
			tvInfo.setText("运动评估：过量");
		}
	}

}
