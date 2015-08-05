package com.mycj.healthy.view;

import java.util.ArrayList;
import java.util.List;

import com.mycj.healthy.util.DisplayUtil;

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
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Space;

public class SimpleHeartRateView extends View {

	private Paint mPaintXY;
	private Paint mPaintLine;
	private Paint mPaintPoint;
	private Paint mPaintPath;
	private Paint mPaintText;

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
	private float position = 0;
	/**
	 * 数据
	 */
	private List<Point> points = new ArrayList<>();
	/**
	 * 显示数据个数 默认为10
	 */
	private int xPoints = 11;

	/**
	 * 颜色XY轴
	 */
	private int colorXY = Color.rgb(232, 0, 88);// #E80058
	/**
	 * 数据的路径
	 */
	private Path path;
	/**
	 * 心率值等级
	 */
	private String levles[] = new String[] { "200", "160", "120", "80", "40" };
	/**
	 * Y轴每个区间的长度
	 */
	private float perY;
	/**
	 * X轴每个区间的长度
	 */
	private float perX;
	/**
	 * 按下的点X值
	 */
	private float downX;
	/**
	 * 整个数据的宽度
	 */
	private float widthX;
	/**
	 * 整个数据的高度
	 */
	private float heightY;

	private GestureDetector detector;

	public SimpleHeartRateView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SimpleHeartRateView(Context context) {
		super(context);
		init(context);
	}

	public SimpleHeartRateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 设置心率
	 * 
	 * @param data
	 */
	public void setData(float data) {
		if (isScroll) {
			isScroll = false;
		}

		Log.e("HeartRateView", "points : " + points.size());
		Point p = new Point();
		p.y = getPointY(data);
		p.x = points.size();
		points.add(p);
		if (points.size() > 10) {
			scrollBy((int) perX, 0);
			position += perX;
		}
		invalidate();
		if (mOnDataChangeListener != null) {
			mOnDataChangeListener.onDataChange(points);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// downX = event.getX();
		//
		// break;
		// case MotionEvent.ACTION_MOVE:
		// float moveX = event.getX();
		// float diff = moveX - downX;
		// int distance = (int) Math.abs(diff) / 10; //每次滑动的间隔
		// if (diff > 0) {
		// Log.e("", "右滑〉〉〉〉〉〉〉〉〉");
		// position += distance;
		// if (position >= widthX) { //滑倒边界时
		// position = widthX;
		// scrollTo((int) widthX, 0);
		// break;
		// }
		// if (position < widthX) {
		// scrollBy(distance, 0);
		// }
		// }
		// if (diff <= 0) {
		// Log.e("", "《〈〈〈〈〈〈〈〈〈左滑");
		// position -= distance;
		// if (position <= 0) { //滑倒边界时
		// position = 0;
		// scrollTo((int) 0, 0);
		// break;
		// }
		// if (position > 0) {
		// scrollBy(-distance, 0);
		// }
		// }
		//
		//
		// if(mOnScrollChangeListener!=null){
		// mOnScrollChangeListener.onScrollChange();
		// }
		// break;
		//
		// default:
		// break;
		// }
		// super.onTouchEvent(event);
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// isScroll =false;
		// }
		return detector.onTouchEvent(event);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		axisY = getHeight() - heightSpace;
		axisX = widthSpace;
		perY = (getHeight() - 2 * heightSpace) / 10;
		perX = (perY * 2f);
		if (points.size() > 10) {
			xPoints = points.size();
		}
		widthX = perX * (xPoints + 1);
		heightY = getHeight() - 2 * heightSpace;

		drawLines(canvas);
		drawXY(canvas);
		drawPath(canvas);

	}

	/**
	 * 初始一些参数
	 * 
	 * @param context
	 */
	private void init(Context context) {
		mPaintXY = new Paint();
		mPaintXY.setColor(colorXY);

		mPaintXY.setAntiAlias(true);
		mPaintXY.setStrokeWidth(3);

		mPaintLine = new Paint();
		mPaintLine.setColor(Color.GRAY);
		mPaintLine.setAntiAlias(true);
		mPaintLine.setStrokeWidth(1);

		mPaintPoint = new Paint();
		mPaintPoint.setColor(colorXY);
		mPaintPoint.setAntiAlias(true);
		mPaintPoint.setStrokeWidth(10);

		mPaintText = new Paint();
		mPaintText.setColor(Color.RED);
		mPaintText.setAntiAlias(true);
		mPaintText.setStrokeWidth(5);

		mPaintPath = new Paint();
		mPaintPath.setStyle(Paint.Style.STROKE);
		mPaintPath.setColor(Color.GREEN);
		mPaintPath.setAntiAlias(true);
		mPaintPath.setStrokeWidth(1);

		setClickable(true);
		setLongClickable(true);
		setPressed(true);

		detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Log.e("", "-------------滑动-------distanceX----" + distanceX);

				isScroll = true;

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

				if (mOnScrollChangeListener != null) {
					mOnScrollChangeListener.onScrollChange();
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

	/**
	 * 画路径
	 * 
	 * @param canvas
	 */
	private void drawPath(Canvas canvas) {
		path = new Path();
		path.moveTo(axisX, axisY);// 起点
		for (int j = 0; j < points.size(); j++) {
			path.lineTo((points.get(j).x + 1) * perX + widthSpace, points.get(j).y);
		}
		canvas.drawPath(path, mPaintPath);
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

	/**
	 * 画X Y轴
	 * 
	 * @param canvas
	 */
	private void drawXY(Canvas canvas) {
		canvas.drawLine(0, axisY, widthX, axisY, mPaintXY);// X
		if (isScroll) {
			canvas.drawLine(axisX, 0, axisX, widthX, mPaintXY);// y
		} else {
			canvas.drawLine(axisX + position, 0, axisX + position, widthX, mPaintXY);// y
		}

	}

	private boolean isScroll;

	/**
	 * 画线 字 点
	 * 
	 * @param canvas
	 */
	private void drawLines(Canvas canvas) {
		for (int i = 0; i < 10; i++) {
			canvas.drawLine(axisX, heightSpace + i * perY, widthX, heightSpace + i * perY, mPaintLine);// 横线
			if (i % 2 == 0) {
				if (isScroll) {
					canvas.drawCircle(axisX, heightSpace + i * perY, 5, mPaintPoint);// Y轴点
				} else {
					canvas.drawCircle(axisX + position, heightSpace + i * perY, 5, mPaintPoint);// Y轴点
				}
				Rect rectText = new Rect();
				mPaintText.getTextBounds(levles[i / 2], 0, levles[i / 2].length(), rectText);

				if (isScroll) {
					canvas.drawText(levles[i / 2], widthSpace - rectText.width() - 10, heightSpace + perY * i, mPaintText);// Yzhou
																															// de
																															// zi
				} else {
					canvas.drawText(levles[i / 2], widthSpace - rectText.width() - 10 + position, heightSpace + perY * i, mPaintText);// Yzhou
																																		// de
																																		// zi
				}
			}
		}

		for (int i = 0; i < xPoints + 1; i++) {
			if (i > 0) {
				canvas.drawLine(widthSpace + perX * (i), axisY, widthSpace + perX * (i), heightSpace, mPaintLine);// 竖线
				canvas.drawCircle(widthSpace + (i) * perX, axisY, 5, mPaintPoint);// X轴点
			}

			Rect rectText = new Rect();
			String str = i + "";
			mPaintText.getTextBounds(str, 0, str.length(), rectText);

			if (i > 0) {
				canvas.drawText(str, widthSpace + perX * i - rectText.width() / 2, getHeight() - rectText.height(), mPaintText);
			}
		}
	}

	public interface OnScrollChangeListener {
		public void onScrollChange();
	}

	public interface OnDataChangeListener {
		public void onDataChange(List<Point> points);
	}

	private OnScrollChangeListener mOnScrollChangeListener;

	public void setOnScrollChangeListener(OnScrollChangeListener mOnScrollChangeListener) {
		this.mOnScrollChangeListener = mOnScrollChangeListener;
	}

	private OnDataChangeListener mOnDataChangeListener;

	public void setOnDataChangeListener(OnDataChangeListener mOnDataChangeListener) {
		this.mOnDataChangeListener = mOnDataChangeListener;
	}

}
