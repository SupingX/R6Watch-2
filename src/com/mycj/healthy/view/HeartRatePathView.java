package com.mycj.healthy.view;

import java.util.ArrayList;
import java.util.List;

import com.mycj.healthy.R;
import com.mycj.healthy.entity.HeartRateData;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.util.TimeUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 心率统计 之  数据路径
 * @author Administrator
 *
 */
public class HeartRatePathView extends View {
	private Paint mPaintPath;
	/**
	 * 左右的空白
	 */
	private float widthSpace = 50;
	/**
	 * 上下的空白
	 */
	private float heightSpace = 30;
	/**
	 * 原点Y值
	 */
	private float axisY;
	/**
	 * 原点X值
	 */
	private float axisX;

	/**
	 * 当前位置
	 */
	private float position;
	/**
	 * 数据
	 */
	private List<Point> points = new ArrayList<>();
	private List<HeartRateData> heartRates = new ArrayList<HeartRateData>();

	/**
	 * 颜色XY轴
	 */
	private int colorXY = Color.rgb(232, 0, 88);// #E80058
	/**
	 * 数据的路径
	 */
	private Path path;
	/**
	 * Y轴每个区间的长度
	 */
	private float perY;
	/**
	 * X轴每个区间的长度
	 */
	private float perX;
	/**
	 * 整个数据的宽度
	 */
	private float widthX;
	/**
	 * 整个数据的高度
	 */
	private float heightY;
	private GestureDetector detector;
	private Paint mPaintText;
	private Paint mPaintLine;
	private Paint mPaintPoint;
	private Paint mPaintPointData;

	public HeartRatePathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mPaintPath = new Paint();
		mPaintPath.setStyle(Paint.Style.STROKE);
		mPaintPath.setColor(getResources().getColor(R.color.count_bg_selected));
		mPaintPath.setAntiAlias(true);
		mPaintPath.setStrokeWidth(2);
		mPaintPath.setStrokeCap(Paint.Cap.ROUND); //设置圆角

		mPaintText = new Paint();
		mPaintText.setColor(Color.BLACK);
		mPaintText.setAntiAlias(true);
		mPaintText.setStrokeWidth(5);
		
		mPaintPointData = new Paint();
		mPaintPointData.setColor(getResources().getColor(R.color.count_bg_selected));
		mPaintPointData.setAntiAlias(true);
		mPaintPointData.setStrokeWidth(10);
		mPaintPointData.setStrokeCap(Paint.Cap.ROUND); //设置圆角

		mPaintLine = new Paint();
		mPaintLine.setColor(Color.GRAY);
		mPaintLine.setAntiAlias(true);
		mPaintLine.setStrokeWidth(1);

		mPaintPoint = new Paint();
		mPaintPoint.setColor(colorXY);
		mPaintPoint.setAntiAlias(true);
		mPaintPoint.setStrokeWidth(10);

		detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Log.e("", "-------------滑动-------distanceX----" + distanceX);

				if (distanceX > 0) {
					Log.e("", "右滑〉〉〉〉〉〉〉〉〉");
					position += distanceX;
					if (position + getWidth() >= widthX) { // 滑倒边界时
						position = widthX - getWidth();
						scrollTo((int) position, 0);
						return false;
					}
					if (position < widthX) {
						scrollBy((int) distanceX, 0);
					}
				}
				if (distanceX <= 0) {
					Log.e("", "《〈〈〈〈〈〈〈〈〈左滑");
					position += distanceX;
					if (position <= 0) { // 滑倒边界时
						position = 0;
						scrollTo((int) position, 0);
						return false;
					}
					if (position > 0) {
						scrollBy((int) distanceX, 0);
					}
				}

				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// isScroll = false;
				return super.onFling(e1, e2, velocityX, velocityY);
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return detector.onTouchEvent(event);
	}

	public HeartRatePathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public HeartRatePathView(Context context) {
		super(context);
		init(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		axisY = getHeight() - heightSpace;
		axisX = widthSpace;
		perY = (getHeight() - 2 * heightSpace) / 10;
		perX = (perY * 2f);
		// 当点的数小于10时
		if (points.size() <= 10) {
			widthX = perX * 11 + widthSpace;
		}
		heightY = getHeight() - 2 * heightSpace;
		Log.e("", "points.size() : " + points.size());
		drawText(canvas);
		drawPath(canvas);
		super.onDraw(canvas);
	}

	/**
	 * 画路径
	 * 
	 * @param canvas
	 */
	private void drawPath(Canvas canvas) {
		path = new Path();
		path.moveTo(axisX, axisY);// 起点
		for (int j = 0; j < points.size(); j++) {
			float pathX = (points.get(j).x + 1) * perX+widthSpace;
			float pathY = points.get(j).y;
			//画点
			canvas.drawPoint(pathX, pathY, mPaintPointData);
//			canvas.drawCircle(pathX, pathY, 5, mPaintPointData);
			//画字
//			String str = TimeUtil.dateToHourStr(heartRates.get(j).getDate());
			String str = heartRates.get(j).getHeartRate()+"";
			canvas.drawText(str, pathX, pathY, mPaintText);
			//画路径
			path.lineTo(pathX ,pathY );
		}
		canvas.drawPath(path, mPaintPath);

	}

//	public interface OnHeartRateChangeListener {
//		public void onChange(List<Float> heartRates);
//	}
//	private OnHeartRateChangeListener mOnHeartRateChangeListener;
//	public void setOnHeartRateChangeListener(OnHeartRateChangeListener l){
//		this.mOnHeartRateChangeListener = l;
//	}
	
	private void drawText(Canvas canvas) {
		if (points.size() <= 10) {
			for (int j = 0; j <= 10; j++) {
				String str = j + "";
				Rect rectText = new Rect();
				mPaintText.getTextBounds(str, 0, str.length(), rectText);
				canvas.drawText(str, widthSpace + perX * j - rectText.width() / 2, getHeight() - rectText.height(), mPaintText);
				canvas.drawLine(widthSpace + perX * (j), axisY, widthSpace + perX * (j), heightSpace, mPaintLine);// 竖线
				canvas.drawCircle(widthSpace + (j) * perX, axisY, 5, mPaintPoint);// X轴点
			}
		} else {
			for (int j = 0; j <= points.size(); j++) {
				String str = j + "";
				Rect rectText = new Rect();
				mPaintText.getTextBounds(str, 0, str.length(), rectText);
				if (j > 0) {
					canvas.drawText(str, widthSpace + perX * j - rectText.width() / 2, getHeight() - rectText.height(), mPaintText);
				}
				if (j > 0) {
					canvas.drawLine(widthSpace + perX * (j), axisY, widthSpace + perX * (j), heightSpace, mPaintLine);// 竖线
					canvas.drawCircle(widthSpace + (j) * perX, axisY, 5, mPaintPoint);// X轴点
				}
			}
		}
	}

	/**
	 * 设置心率
	 * 
	 * @param data
	 */
	public void setData(HeartRateData data) {

		Log.e("HeartRateView", "points : " + points.size());
		heartRates.add(data);
		Point p = new Point();
		p.y = getPointY(data.getHeartRate());
		p.x = points.size();
		points.add(p);
		if (points.size() > 9) {// 可换成当大于屏幕宽度-diff时 增加
			scrollBy((int) perX, 0);
			position += perX;
		}
		widthX += perX;
		invalidate();
//		if (mOnHeartRateChangeListener!=null) {
//			mOnHeartRateChangeListener.onChange(heartRates);
//		}
	}

	/**
	 * 得到每个数据的Y坐标
	 * 
	 * @param data
	 * @return
	 */
	private int getPointY(float data) {
		return (int) (axisY - (heightY * data / 200));
	}
	
	public void reset (){
		position = 0;
		points.clear();
		widthX = perX * 11 + widthSpace;
		scrollTo(0, 0);
		heartRates.clear();
		invalidate();
	}

}
