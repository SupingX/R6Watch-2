package com.mycj.healthy.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mycj.healthy.R;

public class SleepWeekdayCountView extends View {
	private Paint mPaintX;
	private Paint mPaintLine;
	private Paint mPaintMax;
	private Paint mPaintRect;
	private Paint mPaintText;
	private float mWidth;
	private float mHeight;
	private float space = 40;
	private float countHeight;
	private float perX;
	private int MAX = 168;// 一周最高睡眠时间
	private Paint mPaintTextX;

	public SleepWeekdayCountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SleepWeekdayCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SleepWeekdayCountView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mPaintX = new Paint();
		mPaintX.setAntiAlias(true);
		mPaintX.setStrokeWidth(4);
		mPaintX.setStyle(Paint.Style.STROKE);
		mPaintX.setColor(getResources().getColor(R.color.blue));
		mPaintX.setStrokeCap(Paint.Cap.ROUND);// 设置圆角
		mPaintLine = new Paint();
		mPaintLine.setAntiAlias(true);
		mPaintLine.setStrokeWidth(1);
		mPaintLine.setStyle(Paint.Style.STROKE);
		mPaintLine.setColor(getResources().getColor(R.color.grey_light));
		mPaintLine.setStrokeCap(Paint.Cap.ROUND);// 设置圆角
		mPaintMax = new Paint();
		mPaintMax.setAntiAlias(true);
		mPaintMax.setStrokeWidth(1);
		mPaintMax.setStyle(Paint.Style.STROKE);
		mPaintMax.setColor(getResources().getColor(R.color.count_bg_selected));
		mPaintMax.setStrokeCap(Paint.Cap.ROUND);// 设置圆角
		mPaintRect = new Paint();
		mPaintRect.setAntiAlias(true);
		mPaintRect.setStrokeWidth(2);
		mPaintRect.setStyle(Paint.Style.FILL);
		mPaintRect.setColor(getResources().getColor(R.color.count_bg_selected));
		mPaintRect.setStrokeCap(Paint.Cap.ROUND);// 设置圆角

		mPaintText = new Paint();
		mPaintText.setAntiAlias(true);
		mPaintText.setStrokeWidth(1);
		// mPaintText.setStyle(Paint.Style.STROKE);
		mPaintText.setColor(Color.BLACK);
		// mPaintText.setStrokeCap(Paint.Cap.ROUND);// 设置圆角

		mPaintTextX = new Paint();
		mPaintTextX.setAntiAlias(true);
		mPaintTextX.setStrokeWidth(1);
		// mPaintText.setStyle(Paint.Style.STROKE);
		mPaintTextX.setColor(Color.BLACK);
		// mPaintText.setStrokeCap(Paint.Cap.ROUND);// 设置圆角
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mWidth = getWidth();
		mHeight = getHeight();
		countHeight = mHeight - 2 * space;
		perX = mWidth / (3 * 12 + 1);

		drawLines(canvas);
		drawRects(canvas);
		super.onDraw(canvas);
	}

	/**
	 * 根据一个月内总计的步数/最大步数 * 统计图的高度
	 * 
	 * @param step
	 *            单位：万步
	 * @return
	 */
	private float hoursToHeight(int hours) {
		return countHeight * hours / MAX;
	}

	/**
	 * 当前月的上一个月的总天数
	 * 
	 * @param month
	 * @return
	 */
	private int getMaxDayOfLastMonth(int month) {
		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		// 下面可以设置月份，注：月份设置要减1，所以设置1月就是1-1，设置2月就是2-1，如此类推
		cal.set(Calendar.MONTH, month - 1);
		// 调到上个月
		cal.add(Calendar.MONTH, -1);
		// 得到一个月最最后一天日期(31/30/29/28)
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return maxDay;
	}

	private void drawRects(Canvas canvas) {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		Calendar c = Calendar.getInstance();
		c.setTime(curDate);

		for (int i = 0; i < 12; i++) {
			// Log.e("", "-----当前日期-----" + c.get(Calendar.DAY_OF_MONTH));
			int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);// 一个月的第几天
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);// 星期几 开始星期天
			int month = c.get(Calendar.MONTH) - 1;// 星期几 开始星期天

			int hours; // 一周内的总睡眠时间
			String weekday;// 一周的范围（坐标轴）
			int diff;// 向前推移的天数

			if (dayOfWeek == 7) {// 如果今天是星期6 一个星期的最后一天
				int start;// 开始的日期
				if (dayOfMonth <= 6) {// 当今天天数小于6时，直接减会出现负数。应该求上一个月的日期
					// 求shang一个月的总天数
					int maxDay = getMaxDayOfLastMonth(month);
					//
					start = maxDay - (7 - dayOfMonth) + 1;
				} else {
					start = dayOfMonth - 6;
				}
				weekday = start + "-" + dayOfMonth;
				hours = getHours(dayOfMonth);
				diff = 7;
			} else if (dayOfWeek == 1) {// 今天是星期ri 一个星期的di一天
				weekday = dayOfMonth + "";
				hours = getHours(dayOfMonth + 6);
				diff = 0;
			} else {
				int start;
				if (dayOfMonth <= dayOfWeek) {
					// 求上一个月的总天数
					int maxDay = getMaxDayOfLastMonth(month);
					int offset = dayOfWeek - dayOfMonth;
					start = maxDay - offset;
				} else {
					start = dayOfMonth - dayOfWeek;
				}

				weekday = start + "-" + (dayOfMonth + 6 - dayOfWeek);
				hours = getHours(dayOfMonth + 6 - dayOfWeek);
				diff = dayOfWeek + 1;
			}

			float left = mWidth - (3 * perX * (i + 1));
			float top = countHeight - hoursToHeight(hours) + space;
			float right = left + 2 * perX;
			float bottom = countHeight + space;
			RectF rect = new RectF(left, top, right, bottom);
			canvas.drawRect(rect, mPaintRect);
			// 画数据字
			String text = hours + "";
			Rect rectText = new Rect();
			mPaintText.getTextBounds(text, 0, text.length(), rectText);
			canvas.drawText(text, left - rectText.width() / 2 + perX, top - rectText.height() / 2, mPaintText);

			// 画日期
			Rect rectMonthText = new Rect();
			mPaintTextX.getTextBounds(weekday, 0, weekday.length(), rectMonthText);
			float xText =left;
			float yText = bottom + rectMonthText.height();
//			if (rectText.width() > 2 * perX) {
//				xText = left - (rectText.width() / 2 - perX);
//			} else {
//				xText = left + perX - rectText.width() / 2;
//			}
			canvas.drawText(weekday, xText, yText, mPaintText);
			// 向前返回diff天
			c.add(Calendar.DATE, -diff);
		}
	}

	/**
	 * 获取day及day之前6天的时间总和
	 * 
	 * @param day
	 * @return
	 */
	private int getHours(int day) {
		return new Random().nextInt(168);
	}

	private String getMonth(int month) {
		String str = null;
		if (month < 10) {
			str = "0" + month;
		} else {
			str = "" + month;
		}
		return str;
	}

	private void drawLines(Canvas canvas) {
		float perHeight = countHeight / 7;
		canvas.drawLine(0, space, mWidth, space, mPaintMax);
		for (int i = 0; i < 6; i++) {
			canvas.drawLine(0, space + perHeight * (i + 1), mWidth, space + perHeight * (i + 1), mPaintLine);
		}
		canvas.drawLine(0, space + countHeight, mWidth, space + countHeight, mPaintX);
	}

}
