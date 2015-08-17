package com.mycj.healthy.view;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import com.mycj.healthy.R;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.util.TimeUtil;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;

public class SleepDayView extends AbstractDayCountView {

	public SleepDayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		MAX = 60;
	}

	public SleepDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		MAX = 60;
	}

	public SleepDayView(Context context) {
		super(context);
		MAX = 60;
	}

	@Override
	public void initColor() {
		setColor(getResources().getColor(R.color.count_bg_selected));
	}

	@Override
	public int getData( int diff) {
			Date date = TimeUtil.getDateOfDiffDay(getCurrentDate(), -diff);
			String dateStr=  TimeUtil.dateToString(date);
			List<HistoryData> historyDatas = DataSupport.where("historyDate=?",dateStr).find(HistoryData.class);
			if(historyDatas!=null&&historyDatas.size()!=0){
				return historyDatas.get(0).getSleep();
			}
		return 0;
	}

}
