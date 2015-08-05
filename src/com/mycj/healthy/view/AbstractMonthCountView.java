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
import android.view.View;

import com.mycj.healthy.R;
import com.mycj.healthy.view.AbstractWeekdayCountView.OnDateChangeListener;

public abstract class AbstractMonthCountView extends View {
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
	public int MAX = 310;//一个月最高步数
	private int color;
	private Date currentDate;
	public  void setColor(int color){
		this.color = color;
	}
	public abstract void initColor();
	
	public Date getCurrentDate(){
		return this.currentDate;
	}
	public AbstractMonthCountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public AbstractMonthCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AbstractMonthCountView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		initColor();
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
		mPaintMax.setColor(color);
		mPaintMax.setStrokeCap(Paint.Cap.ROUND);// 设置圆角
		mPaintRect = new Paint();
		mPaintRect.setAntiAlias(true);
		mPaintRect.setStrokeWidth(2);
		mPaintRect.setStyle(Paint.Style.FILL);
		mPaintRect.setColor(color);
		mPaintRect.setStrokeCap(Paint.Cap.ROUND);// 设置圆角

		mPaintText = new Paint();
		mPaintText.setAntiAlias(true);
		mPaintText.setStrokeWidth(1);
//		mPaintText.setStyle(Paint.Style.STROKE);
		mPaintText.setColor(Color.BLACK);
//		mPaintText.setStrokeCap(Paint.Cap.ROUND);// 设置圆角
		
		currentDate = new Date(System.currentTimeMillis());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mWidth = getWidth();
		mHeight = getHeight();
		countHeight = mHeight - 2*space;
		perX = mWidth / (3 * 12 + 1);
		drawLines(canvas);
		drawRects(canvas);
		super.onDraw(canvas);
	}
	
	/**
	 * 根据当前月，获取上diff月的计步总数 diff为零时，即为当前月
	 * @param month
	 * @param diff
	 * @return
	 */
	private int getStep(int month,int diff){
		return new Random().nextInt(MAX);
	}
	
	public abstract int getData(int month,int diff);
	
	/**
	 * 根据一个月内总计的步数/最大步数  * 统计图的高度
	 * @param step 单位：万步
	 * @return
	 */
	private float dataToHeight(int data){
		return countHeight*data/MAX;
	}
	
	private void drawRects(Canvas canvas) {

		int month = getCurrentMonth();
		for (int i = 0; i < 12; i++) {
			
			int data = getData(month, i);
			float left = mWidth-(3*perX * (i+1));
			float top = countHeight-dataToHeight(data)+space;
			float right=left +2*perX;
			float bottom = countHeight+space;
			RectF rect = new RectF(left, top, right, bottom);
			canvas.drawRect(rect, mPaintRect);
			//画数据字
			String text = data+"";
			Rect rectText = new Rect();
			mPaintText.getTextBounds(text, 0, text.length(), rectText);
			canvas.drawText(text, left-rectText.width()/2+perX,top-rectText.height()/2, mPaintText);
			//画日期
			Rect rectMonthText = new Rect();
			String monthStr = getMonth(month);
			month--;
			if(month==0){
				month=12;
			}
			mPaintText.getTextBounds(monthStr, 0, monthStr.length(), rectMonthText);
			canvas.drawText(monthStr, left-rectText.width()/2+perX, bottom+rectMonthText.height(), mPaintText);
		}
	}

	private String getMonth(int month){
		String str =null;
		if(month<10){
			str = "0" + month;
		}else{
			str = "" + month;
		}
		return str;
	}
	
	private int getCurrentMonth() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		int month = c.get(Calendar.MARCH) + 1;

		return month;
	}

	private void drawLines(Canvas canvas) {
		float perHeight = countHeight / 7;
		canvas.drawLine(0, space, mWidth, space, mPaintMax);
		for (int i = 0; i < 6; i++) {
			canvas.drawLine(0, space+perHeight * (i + 1), mWidth, space+perHeight * (i + 1), mPaintLine);
		}
		canvas.drawLine(0, space+countHeight, mWidth, space+countHeight, mPaintX);
	}

	
	public interface OnDateChangeListener{
		public void onPrevious(Date date);
		public void onNext(Date date);
	}
	private OnDateChangeListener mOnDateChangeListener;
	public void setOnDateChangeListener(OnDateChangeListener l){
		this.mOnDateChangeListener = l;
	}
	public void setCurrentDate(Date date){
		this.currentDate = date;
		invalidate();
	}
	
	public void previous(){
		Calendar c = Calendar.getInstance();
		c.setTime(getCurrentDate());
		c.add(Calendar.MONTH, -1);
		setCurrentDate(c.getTime());
		if(mOnDateChangeListener!=null){
			mOnDateChangeListener.onPrevious(currentDate);
		}
	}
	public void next(){
		Calendar c = Calendar.getInstance();
		c.setTime(getCurrentDate());
		c.add(Calendar.MONTH, 1);
		setCurrentDate(c.getTime());
		if(mOnDateChangeListener!=null){
			mOnDateChangeListener.onNext(currentDate);
		}
	}
	
	
	
}
