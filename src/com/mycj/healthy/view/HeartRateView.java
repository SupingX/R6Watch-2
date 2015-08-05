package com.mycj.healthy.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HeartRateView extends View {
	private int xLineWidth = 4;
	private int horizontalLineWidth = 1;
	private Paint mPaintXLine;
	private Paint mPaintHorizontalLine;
	private Paint mPaintText;
	private Paint mPaintPath;
	private Paint mPaintPoint;
	private Rect rectText;
	private String[] levles = new String[] { "200", "160", "120", "80", "40", "0" };
	//
	private LinkedList<Float> listHeartRates = new LinkedList<>();

	public void setData(final float data) {
		Log.e("HeartRateView", "----setData()-----");
		listHeartRates.add(data);
		size = listHeartRates.size();
		invalidate();

		// post(new Runnable() {
		// @Override
		// public void run() {
		// listHeartRates.add(data);
		// postInvalidate();
		// }
		// });
	}

	private int countPoint;
	private float mHeight;
	private float mWidth;
	private float otherWidth;
	private float otherHeight;
	private float perWith;
	private List<Point> points;
	private float xDown;
	private Path p;

	private OnHeartDataChangeListener mOnHeartDataChangeListener;
	private OnScrollListener mOnScrollListener;
	private LinkedList<Float> showList;

	public void setOnScrollListener(OnScrollListener mOnScrollListener) {
		this.mOnScrollListener = mOnScrollListener;
	}

	public void setOnHeartDataChangeListener(OnHeartDataChangeListener listener) {
		this.mOnHeartDataChangeListener = listener;
	}

	public HeartRateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public interface OnHeartDataChangeListener {
		public void dataChange(LinkedList<Float> data);
	}

	public interface OnScrollListener {
		public void onScroll();
	}

	private void init() {
		// Resources r = getResources();
		mPaintXLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintXLine.setColor(Color.WHITE);
		// xLineWidth = r.getDimensionPixelSize(R.dimen.x_line);
		// xLineWidth = (int) r.getDimension(R.dimen.x_line);
		mPaintXLine.setStrokeWidth(xLineWidth);

		mPaintHorizontalLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintHorizontalLine.setColor(Color.WHITE);
		mPaintHorizontalLine.setAlpha(100);
		// horizontalLineWidth =
		// r.getDimensionPixelSize(R.dimen.horizontal_line);
		// horizontalLineWidth = (int) r.getDimension(R.dimen.horizontal_line);
		mPaintHorizontalLine.setStrokeWidth(horizontalLineWidth);

		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);

		mPaintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintPath.setStyle(Paint.Style.STROKE);
		mPaintPath.setStrokeCap(Paint.Cap.ROUND);
		mPaintPath.setColor(Color.RED);
		mPaintPath.setStrokeWidth(5);
		mPaintPath.setAntiAlias(true);

		mPaintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintPoint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintPoint.setColor(Color.GREEN);
		mPaintPoint.setStrokeWidth(2);
		mPaintPoint.setAntiAlias(true);
		mPaintPoint.setStrokeCap(Paint.Cap.ROUND); // 设置圆角

		// { 213f, 25f, 56f, 77f, 34f, 72f, 213f, 25f, 56f, 77f, 34f, 72f, 213f,
		// 25f, 56f, 2f, 34f, 72f, 213f, 25f };
		listHeartRates.add(213f);
		listHeartRates.add(25f);
		listHeartRates.add(67f);
		listHeartRates.add(64f);
		listHeartRates.add(125f);
		size = listHeartRates.size();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("HeartRateView", "<-       onDraw       ->");

		initvalue();
		// 画线
		drawLines(canvas);
		// getPoints();
		// getPoints2();
		getPoints3();
		// 画路径
		drawPath(canvas);
		super.onDraw(canvas);
	}

	private void initvalue() {
		otherWidth = 50;
		otherHeight = 50;
		mHeight = getHeight() - otherHeight * 2;
		mWidth = getWidth();

	}

	/**
	 * 画线 　+　文字
	 * 
	 * @param canvas
	 */
	private void drawLines(Canvas canvas) {
		// 画X轴
		canvas.drawLine(0, getHeight() - otherHeight, mWidth, getHeight() - otherHeight, mPaintXLine);
		// 画水平线
		int count = levles.length - 1;
		float pts[] = new float[4];
		float perHeight = mHeight / count;
		for (int i = 0; i < count; i++) {
			pts[0] = 0;
			pts[1] = perHeight * i + otherHeight;
			pts[2] = mWidth;
			pts[3] = perHeight * i + otherHeight;
			// 画线
			// canvas.drawLines(pts, mPaintHorizontalLine);
			canvas.drawLine(pts[0], pts[1], pts[2], pts[3], mPaintHorizontalLine);
			// 画数字标
			rectText = new Rect();
			mPaintText.getTextBounds(levles[i], 0, levles[i].length(), rectText);
			canvas.drawText(levles[i], otherWidth - rectText.width(), pts[1] + (rectText.height()), mPaintText);
		}
		rectText = new Rect();
		mPaintText.getTextBounds(levles[levles.length - 1], 0, levles[levles.length - 1].length(), rectText);
		// canvas.drawText(levles[levles.length - 1], otherWidth, getHeight() -
		// otherHeight + (rectText.height()),
		// mPaintText);

	}

	/**
	 * 取点
	 */
	private void getPoints() {

		// 20个点
		countPoint = 20;
		perWith = (mWidth - otherWidth * 2) / 20;
		points = new ArrayList<>();
		for (int i = 0; i < listHeartRates.size(); i++) {
			Point p = new Point();
			p.x = (int) (perWith * i + otherWidth);
			p.y = (int) (mHeight * (1 - 1.0 * listHeartRates.get(i) / 240) + otherHeight);
			points.add(p);
		}
	}

	private int flag = 0;
	private int size;

	/**
	 * 取点 大于20个点时 去掉第一个点
	 */
	private void getPoints2() {

		// 20个点
		countPoint = 20;
		perWith = (mWidth - otherWidth * 2) / 20;
		points = new ArrayList<>();
		showList = new LinkedList<>();
		showList = listHeartRates;
		// 当记录为21个时，去掉第一个记录
		if (showList.size() == 21) {
			flag++;
			showList.removeFirst();
		}
		Log.v("HeartRateView", "----------------------");
		Log.v("", "listHeartRates : " + listHeartRates.size());
		Log.v("", "showList : " + showList.toString());
		Log.v("HeartRateView", "[flag -->" + flag + "]");
		Log.v("HeartRateView", "----------------------");

		for (int i = 0; i < showList.size(); i++) {
			Point p = new Point();
			p.x = (int) (perWith * i + otherWidth);
			p.y = (int) (mHeight * (1 - 1.0 * showList.get(i) / 240) + otherHeight);
			points.add(p);
		}

	}

	private void getPoints3() {
		Log.v("", "listHeartRates : " + listHeartRates.size());
		// 20个点
		countPoint = 20;
		perWith = (mWidth - otherWidth * 2) / 20;
		points = new ArrayList<>();
		// size = listHeartRates.size();
		if (size <= 20) {
			for (int i = 0; i < size; i++) {
				Point p = new Point();
				p.x = (int) (perWith * i + otherWidth);
				p.y = (int) (mHeight * (1 - 1.0 * listHeartRates.get(i) / 240) + otherHeight);
				points.add(p);
			}
		} else {
			for (int i = size - 20; i < size; i++) {
				Point p = new Point();
				p.x = (int) (perWith * i + otherWidth - (size - 20) * perWith);
				p.y = (int) (mHeight * (1 - 1.0 * listHeartRates.get(i) / 240) + otherHeight);
				points.add(p);
			}
		}

	}

	/**
	 * 画路径
	 * 
	 * @param canvas
	 */
	private void drawPath(Canvas canvas) {
		p = new Path();
		Log.e("HeartRateView", "points : " + points.size());
		int size = points.size();
		// 当数据小于20时
		p.moveTo(points.get(0).x, points.get(0).y);// 起点
		for (int i = 1; i < size; i++) {
			p.lineTo(points.get(i).x, points.get(i).y);
			canvas.drawPoint(points.get(i).x, points.get(i).y, mPaintPoint);
		}
		canvas.drawPath(p, mPaintPath);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.setClickable(true);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getX();
			Log.e("HeartRateView", "按下坐标  xDown：" + xDown);
			break;
		case MotionEvent.ACTION_MOVE:
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			float xUp = event.getX();
			float diff = xDown - xUp;
			Log.e("HeartRateView", "取消坐标  xUp：" + xUp);
			if (Math.abs(diff) > 10 && diff > 0) {
				Log.e("HeartRateView", "<--左移");
				// scrollBy((int) diff/10, 0);
				// scrollTo((int) diff, 0);
				if (mOnScrollListener != null) {
					mOnScrollListener.onScroll();// 当移动时，进行的一些操作
				}
				size++;
				if (size > listHeartRates.size()) {
					size = listHeartRates.size();
				}
				invalidate();

			} else if (Math.abs(diff) > 10 && diff < 0) {
				Log.e("HeartRateView", "右移-->");
				// scrollBy((int) diff/10, 0);
				// scrollTo((int) diff, 0);

				// test2
				// if(showList.size()==20){
				// if(flag>0){
				// mOnScrollListener.onScroll();//当移动时，进行的一些操作
				// //删除最后一个数据
				// showList.removeLast();
				// //添加first
				// showList.addFirst(listHeartRates.get(flag-1));
				// flag--;
				// }
				// }else{
				// for (int i = 0; i < 20; i++) {
				// showList = new LinkedList<Float>();
				// showList.add(listHeartRates.get(i));
				// }
				// }
				// test3
				if (size > 20) {
					if (mOnScrollListener != null) {
						mOnScrollListener.onScroll();// 当移动时，进行的一些操作
					}
					size--;
					if (size < 0) {
						break;
					}
					// listHeartRates.removeLast();

				}
				invalidate();
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
