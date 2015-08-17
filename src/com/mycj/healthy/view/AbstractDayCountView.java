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
import android.view.MotionEvent;
import android.view.View;

import com.mycj.healthy.R;
import com.mycj.healthy.view.AbstractWeekdayCountView.OnDateChangeListener;

public abstract class AbstractDayCountView extends View {
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
	public int MAX =200;//一天最高步数 
	private int color;
	private Date currentDate;
	public  void setColor(int color){
		this.color = color;
	}
	public abstract void initColor();
	
	public Date getCurrentDate(){
		return this.currentDate;
	}
	public AbstractDayCountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public AbstractDayCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AbstractDayCountView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		initColor();
		setClickable(true);
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
		
		mPaintRectBottom = new Paint();
		mPaintRectBottom.setAntiAlias(true);
		mPaintRectBottom.setStrokeWidth(1);
		mPaintRectBottom.setStyle(Paint.Style.STROKE);
		mPaintRectBottom.setColor(color);
		

		mPaintText = new Paint();
		mPaintText.setAntiAlias(true);
		mPaintText.setStrokeWidth(1);
//		mPaintText.setStyle(Paint.Style.STROKE);
		mPaintText.setColor(Color.BLACK);
//		mPaintText.setStrokeCap(Paint.Cap.ROUND);// 设置圆角
		
		mPaintTextDate = new Paint();
		mPaintTextDate.setAntiAlias(true);
		mPaintTextDate.setStrokeWidth(1);
//		mPaintText.setStyle(Paint.Style.STROKE);
		mPaintTextDate.setColor(Color.BLACK);
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
//	private int getStep(int month,int diff){
////		return new Random().nextInt(MAX);
//	}
	
	public abstract int getData(int diff);
	
	/**
	 * 根据一个月内总计的步数/最大步数  * 统计图的高度
	 * @param step 单位：万步
	 * @return
	 */
	private float dataToHeight(int data){
		float h = countHeight*data/MAX;
//		if(h<=countHeight/1000f ){//当数据为很小时，设置最小高度为1；
//			h=1f;
//		}
		return h;
	}
	private int getCurrentMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MARCH) + 1;

		return month;
	}
	private int getCurrentYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	private int getCurrentDateOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_YEAR);
	}
	private void drawRects(Canvas canvas) {

		int day = getCurrentDay(currentDate);
		int month = getCurrentMonth(currentDate);
		int year = getCurrentYear(currentDate);
		int dayOfYear = getCurrentDateOfYear(currentDate);
		
		for (int i = 0; i < 12; i++) {
			//画数据方块
//			相应的日期到数据库查询、没有则0
			int data = getData(i);
			MAX = Math.max(MAX, data);//当哪天高于这个 ，就设置这个为最高步数
			float left = mWidth-(3*perX * (i+1));
			float top = countHeight-dataToHeight(data)+space;
			float right=left +2*perX;
			float bottom = countHeight+space+4;
			RectF rect = new RectF(left, top, right, bottom);
			RectF bottomRect = new RectF(left, mHeight-space, right, bottom);
//			canvas.drawRect(bottomRect, mPaintRectBottom);
			canvas.drawLine(left, mHeight-space, left+2*perX, mHeight-space, mPaintRectBottom);
			canvas.drawRect(rect, mPaintRect);
			//画数据字
			String text = data+"";
			Rect rectText = new Rect();
			mPaintText.getTextBounds(text, 0, text.length(), rectText);
			canvas.drawText(text, left-rectText.width()/2+perX,top-rectText.height()/2, mPaintText);
			//画日期
			Rect rectMonthText = new Rect();
			String monthStr = formatDayStr(day)+"日";//不足2位补0
			mPaintTextDate.getTextBounds(monthStr, 0, monthStr.length(), rectMonthText);
			if(isSameDay(day,month,year)){//今天的为红色 年月日都要相等
				mPaintTextDate.setColor(Color.RED);
				if (rectMonthText.width()/2-perX>0) {
					canvas.drawText(monthStr, left-(rectMonthText.width()/2-perX), bottom+rectMonthText.height(), mPaintTextDate);
				}else{
					canvas.drawText(monthStr, left+(perX-rectMonthText.width()/2), bottom+rectMonthText.height(), mPaintTextDate);
				}
				mPaintTextDate.setColor(Color.BLACK);
			}else{
				if (rectMonthText.width()/2-perX>0) {
					canvas.drawText(monthStr, left-(rectMonthText.width()/2-perX), bottom+rectMonthText.height(), mPaintTextDate);
				}else{
					canvas.drawText(monthStr, left+(perX-rectMonthText.width()/2), bottom+rectMonthText.height(), mPaintTextDate);
				}
			}
			
			//画完后日期-1
			day--;
			dayOfYear--;
			if(day==0){
				day = getMaxDayOfLastMonth(month);//日期为零时，为上一个月的最大天数
				month--;
			}
			if(dayOfYear==0){
				dayOfYear = getMaxDayOfLastYear(year);
						year--;
			}
		}
	}
	
	/**
	 * 获取year前一年的总天数
	 * @param year
	 * @return
	 */
	private int getMaxDayOfLastYear(int year) {
		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		// 下面可以设置月份，注：月份设置要减1，所以设置1月就是1-1，设置2月就是2-1，如此类推
		cal.set(Calendar.YEAR, year);
		// 调到上个月
		cal.add(Calendar.YEAR, -1);
		// 得到一个月最最后一天日期(31/30/29/28)
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
		return maxDay;
		
	}
	private boolean isSameDay(int day,int month,int year)	{
		Calendar c=  Calendar.getInstance();
		c.setTime(getCurrentDate());
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH)+1;
		
		Calendar cNow=  Calendar.getInstance();
		cNow.setTime(new Date());
		int yearNow = cNow.get(Calendar.YEAR);
		int monthNow = cNow.get(Calendar.MONTH)+1;
		int dayNow = cNow.get(Calendar.DAY_OF_MONTH);
		
		return year==yearNow&&month==monthNow&&day == dayNow;
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
	
	/**
	 * 当前月的总天数
	 * 
	 * @param month
	 * @return
	 */
	private int getMaxDayOfMonth(int month) {
		// 获取当前时间
		Calendar cal = Calendar.getInstance();
		// 下面可以设置月份，注：月份设置要减1，所以设置1月就是1-1，设置2月就是2-1，如此类推
		cal.set(Calendar.MONTH, month - 1);
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return maxDay;
	}
	private String formatDayStr(int day){
		String str =null;
		if(day<10){
			str = "0" + day;
		}else{
			str = "" + day;
		}
		return str;
	}
	
	
	/**
	 * 获得制定日期的天
	 * @param date
	 * @return
	 */
	private int getCurrentDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DAY_OF_MONTH) ;
		return day;
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
		public void onNow(Date date);
	}
	private OnDateChangeListener mOnDateChangeListener;
	private Paint mPaintRectBottom;
	private float downX;
	private Paint mPaintTextDate;
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
		c.add(Calendar.DATE, -1);
		setCurrentDate(c.getTime());
		if(mOnDateChangeListener!=null){
			mOnDateChangeListener.onPrevious(currentDate);
		}
	}
	public void next(){
		Calendar c = Calendar.getInstance();
		c.setTime(getCurrentDate());
		c.add(Calendar.DATE, 1);
		setCurrentDate(c.getTime());
		if(mOnDateChangeListener!=null){
			mOnDateChangeListener.onNext(currentDate);
		}
	}
	
	public void now(){
		setCurrentDate(new Date());
		if(mOnDateChangeListener!=null){
			mOnDateChangeListener.onNow(currentDate);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			float upX= event.getX();
			float diff = upX- downX;
			if (Math.abs(diff)>10) {
				if (diff>0) {
					//右滑
					next();
				}else{
				previous();
				}
			}
			
			break;
		default:
			break;
		}
		
		return super.onTouchEvent(event);
	}
	
	
}
