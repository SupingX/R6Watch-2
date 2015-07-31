package com.mycj.healthy.view;

import java.text.DecimalFormat;

import android.R.fraction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mycj.healthy.R;

public class DistanceView extends View {
	/** 人 bitmap **/
	private Bitmap bitmapPerson;
	/** 游标 bitmap **/
	private Bitmap bitmapThumb;
	/** X轴 画笔 **/
	private Paint mLinePaint;
	/** 坐标点 画笔 **/
	private Paint mDotPaint;
	/** 图像画笔 **/
	private Paint mBitmapPaint;
	/** 文字画笔 **/
	private Paint mTextPaint;
	private float currentPoint = 2; // 1-12
	/** 目标距离 **/
	private float maxDistance = 25.46f;
	/** 当前完成距离 **/
	private float currentDistance = 0f;

	/**
	 * 小圆点半径
	 */
	private float radio = 5;

	private OnPointChange mOnPointChange;
	/**
	 * 没一小段距离为整个view/12
	 */
	private float widthShort;

	public DistanceView(Context context) {
		super(context);
		init();
	}

	public DistanceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DistanceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private float getDistanceWith() {
		return currentDistance / maxDistance * widthShort * 10;
	}

	private void init() {
		bitmapThumb = BitmapFactory.decodeResource(getResources(), R.drawable.my_state_distance_goal);
		bitmapPerson = BitmapFactory.decodeResource(getResources(), R.drawable.ic_walkman);

		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(4);
		mLinePaint.setColor(getResources().getColor(R.color.blue));

		mDotPaint = new Paint();
		mDotPaint.setAntiAlias(true);
		mDotPaint.setStrokeWidth(20);
		mDotPaint.setColor(getResources().getColor(R.color.blue));

		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		Log.e("distanceView", "bitmap : " + bitmapPerson);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(getResources().getColor(R.color.blue));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 画X轴
		canvas.drawLine(0, getHeight() - 50, getWidth(), getHeight() - 50, mLinePaint);
		// 每一小段的距离
		widthShort = getWidth() / 12;
		// 第一个点
		float x = widthShort / 2;
		float y = getHeight() - 50;

		// 画指示图标thumb
		if (bitmapThumb != null) {
			// canvas.drawBitmap(bitmapThumb,
			// (currentPoint-1)*widthShort+widthShort/2 - bitmapThumb.getWidth()
			// + radio, y-50-radio,mBitmapPaint );
			canvas.drawBitmap(bitmapThumb, getDistanceWith() - bitmapThumb.getWidth() / 2 + widthShort * 3 / 2, y - 50 - radio, mBitmapPaint);
		}
		if (bitmapPerson != null) {
			// 画人
			// canvas.drawBitmap(bitmapPerson,
			// (currentPoint-1)*widthShort+widthShort/2-radio-bitmapPerson.getWidth(),
			// y-50-radio-bitmapPerson.getHeight(),mBitmapPaint );
			canvas.drawBitmap(bitmapPerson, getDistanceWith() - bitmapPerson.getWidth() + widthShort * 3 / 2, y - 50 - radio - bitmapPerson.getHeight(), mBitmapPaint);
		}
		// 初始化数字
		String[] ruler = initText();
		for (int i = 0; i < 12; i++) {
			// 画小点点
			canvas.drawCircle(x, y, radio, mDotPaint);
			// 画文字
			if (i % 2 != 0) {
				Rect rectText = new Rect();
				mTextPaint.getTextBounds(ruler[(int) i / 2], 0, ruler[(int) i / 2].length(), rectText);
				canvas.drawText(ruler[(int) i / 2], x - rectText.width() / 2, y + rectText.height() + 5, mTextPaint);
			}
			x += widthShort;
		}
		super.onDraw(canvas);
	}

	/**
	 * 设置当前point
	 * 
	 * @param point
	 */
	public void setCurrentPoint(int point) {
		if (mOnPointChange != null) {
			mOnPointChange.onChange(point);
		}
		this.currentPoint = point;
		invalidate();
	}

	/**
	 * 初始化数字
	 * 
	 * @return
	 */
	private String[] initText() {

		DecimalFormat df = new DecimalFormat("#.00");
		// df.format(你要格式化的数字);
		String[] ruler = new String[] {
				// "0.00","8.00","16.00","24.00","32.00","40.00"
				"0.00", df.format(maxDistance * 2 / 10), df.format(maxDistance * 4 / 10), df.format(maxDistance * 5 / 10), df.format(maxDistance * 6 / 10), df.format(maxDistance) };
		return ruler;
	}

	/**
	 * 得到当前距离
	 * 
	 * @return
	 */
	public float getDistance() {
		return (currentPoint - 2) * 4;
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(float maxDistance) {
		if (maxDistance < currentDistance) {
			maxDistance = currentDistance;
		}
		if (maxDistance < 0) {
			maxDistance = 100;
		}
		this.maxDistance = maxDistance;
		if (mOnDistanceChange != null) {
			mOnDistanceChange.onMaxDistanceChange(maxDistance);
		}
		invalidate();
	}

	public float getCurrentDistance() {
		return currentDistance;
	}

	public void setCurrentDistance(float currentDistance) {
		if (currentDistance < 0) {
			currentDistance = 0;
		}
		if (currentDistance > maxDistance) {
			currentDistance = maxDistance;
		}
		this.currentDistance = currentDistance;
		if (mOnDistanceChange != null) {
			mOnDistanceChange.onChange(currentDistance);
		}
		invalidate();
	}

	/**
	 * 接口point变化
	 * 
	 * @author Administrator
	 *
	 */
	private interface OnPointChange {
		public void onChange(int point);
	}

	public interface OnDistanceChange {
		public void onChange(float distance);

		public void onMaxDistanceChange(float max);
	}

	private OnDistanceChange mOnDistanceChange;

	public void setOnDistanceChange(OnDistanceChange l) {
		this.mOnDistanceChange = l;
	}
}
