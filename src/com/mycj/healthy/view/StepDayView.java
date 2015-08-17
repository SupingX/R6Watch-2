package com.mycj.healthy.view;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import com.mycj.healthy.R;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.util.TimeUtil;

import android.content.Context;
import android.util.AttributeSet;

public class StepDayView extends AbstractDayCountView {

	public StepDayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public StepDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StepDayView(Context context) {
		super(context);
	}

	@Override
	public void initColor() {
		setColor(getResources().getColor(R.color.count_step));
	}

	@Override
	public int getData(int diff) {
		Date date = TimeUtil.getDateOfDiffDay(getCurrentDate(), -diff);
		String dateStr=  TimeUtil.dateToString(date);
		List<HistoryData> historyDatas = DataSupport.where("historyDate=?",dateStr).find(HistoryData.class);
		if(historyDatas!=null&&historyDatas.size()!=0){
			return historyDatas.get(0).getStep();
		}
	return 0;
//		return new Random().nextInt(100); //模拟测试数据
	}

}
