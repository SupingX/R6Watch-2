package com.mycj.healthy.view;

import java.util.Date;
import java.util.Random;

import org.litepal.crud.DataSupport;

import com.mycj.healthy.R;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.util.TimeUtil;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class StepMonthView extends AbstractMonthCountView {

	public StepMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public StepMonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StepMonthView(Context context) {
		super(context);
	}

	@Override
	public void initColor() {
		setColor(getResources().getColor(R.color.count_step));
	}

	@Override
	public int getData(int diff) {
		int sum;
		Date date = TimeUtil.getDateOfDiffMonth(getCurrentDate(), -diff);//取得月份偏移的日期
		String dateStr=  TimeUtil.dateToString(date).substring(0,6);
		Log.v("", "******************************dateStr :" + dateStr);
//		sum = DataSupport.where("historyDate=?",dateStr).sum(HistoryData.class, "step", Integer.class);
		sum = DataSupport.where("substr(historyDate,1,6)=?",dateStr).sum(HistoryData.class, "step", Integer.class);
		return sum;
//		return new Random().nextInt(MAX);//测试数据
	}

}
