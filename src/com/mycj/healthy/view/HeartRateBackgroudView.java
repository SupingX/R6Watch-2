package com.mycj.healthy.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
/**
 *  心率统计 之  背景
 * @author Administrator
 *
 */
public class HeartRateBackgroudView extends View {
	private Paint mPaintXY;
	private Paint mPaintLine;
	private Paint mPaintPoint;
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
	 * 整个数据的宽度
	 */
	private float widthX;
	/**
	 * 整个数据的高度
	 */
	private float heightY;

	public HeartRateBackgroudView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(context);
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
		mPaintText.setColor(Color.BLACK);
		mPaintText.setAntiAlias(true);
		mPaintText.setStrokeWidth(5);

	}

	public HeartRateBackgroudView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HeartRateBackgroudView(Context context) {
		super(context);
		init(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {

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
		super.onDraw(canvas);
	}

	/**
	 * 画X Y轴
	 * 
	 * @param canvas
	 */
	private void drawXY(Canvas canvas) {
		canvas.drawLine(0, axisY, widthX, axisY, mPaintXY);// X
		canvas.drawLine(axisX, 0, axisX, widthX, mPaintXY);// y

	}

	/**
	 * 画线 字 点
	 * 
	 * @param canvas
	 */
	private void drawLines(Canvas canvas) {
		for (int i = 0; i < 10; i++) {
			canvas.drawLine(0, heightSpace + i * perY, widthX, heightSpace + i * perY, mPaintLine);// 横线
			if (i % 2 == 0) {
				canvas.drawCircle(axisX, heightSpace + i * perY, 5, mPaintPoint);// Y轴点
				Rect rectText = new Rect();
				mPaintText.getTextBounds(levles[i / 2], 0, levles[i / 2].length(), rectText);
				canvas.drawText(levles[i / 2], widthSpace - rectText.width() - 10, heightSpace + perY * i, mPaintText);// Yzhou
			}
		}
	}

}
