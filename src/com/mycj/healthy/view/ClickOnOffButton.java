package com.mycj.healthy.view;

import com.mycj.healthy.R;
import com.mycj.healthy.util.DensityUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * 弃用
 * @author Administrator
 *
 */
public class ClickOnOffButton extends View {
	private Bitmap bottomBitmap;
	private Bitmap frame;
	private Bitmap thumbPressed;
	// private Bitmap thumbUnPressed;
	/** 滑块的偏移距离 [0,maxOffset] **/
	private float offset = 0;
	/** 滑块初始位置 **/
	private float defaultPosition;
	private boolean isOn;
	private boolean isDrag;// 是否拖拽

	private Paint p;
	private float downX;
	private float maxOffset;

	private OnCheckListener mOnCheckListener;
	private float xMove;
	private int leftDst;
	private int top;
	private int rightDst;
	private int bottom;
	private int leftSrc;
	private int rightSrc;
	private Bitmap bitmapClip;

	public interface OnCheckListener {
		public void onChange(boolean isOn);
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
		if (mOnCheckListener != null) {
			mOnCheckListener.onChange(isOn);
		}
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOnCheckListener(OnCheckListener l) {
		this.mOnCheckListener = l;
	}

	public ClickOnOffButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ClickOnOffButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ClickOnOffButton(Context context) {
		super(context);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// float max = bottom.getWidth()/2-bottom.getHeight();
		// float max = getWidth()-bottom.getHeight();
		super.onTouchEvent(event);// 这一句不能少，否则无法触发onclick事件
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			top = 0;
			bottom = bottomBitmap.getHeight();
			leftSrc = frame.getHeight() / 2;
			rightSrc = frame.getWidth() - frame.getHeight();

			if (isOn) {
				defaultPosition = -(bottomBitmap.getWidth() - frame.getWidth());
				leftDst = -(bottomBitmap.getWidth() - frame.getWidth());
			} else {
				defaultPosition = 0;
				leftDst = 0;
			}
			rightDst = leftDst + bottomBitmap.getWidth();

			isOn = !isOn;
			if (mOnCheckListener != null) {
				mOnCheckListener.onChange(isOn);
			}

			invalidate();
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(frame.getWidth(), frame.getHeight());

	}

	@Override
	protected void onDraw(Canvas canvas) {

		// 显示的区域
		Rect src = new Rect(leftSrc, top, rightSrc, bottom);
		// 图片的区域
		Rect dst = new Rect(leftDst, top, rightDst, bottom);

		// 画底部
		canvas.drawBitmap(bottomBitmap, defaultPosition + offset, 0, p);
		// canvas.drawBitmap(bottomBitmap, src, dst, p);
		// 画滑块
		canvas.drawBitmap(thumbPressed, defaultPosition + offset, 0, p);
		// 画边框
		canvas.drawBitmap(frame, 0, 0, p);

		// Rect src = new
		// Rect(frame.getWidth()/2,0,frame.getWidth()/2,frame.getHeight());
		// Rect dst = new Rect(0,0,frame.getWidth(),frame.getHeight());
		// canvas.drawBitmap(frame, src, dst, p);

		super.onDraw(canvas);
	}

	private void init() {
		Resources r = getResources();
		bottomBitmap = BitmapFactory.decodeResource(r, R.drawable.bottom);
		frame = BitmapFactory.decodeResource(r, R.drawable.frame);
		thumbPressed = BitmapFactory.decodeResource(r, R.drawable.btn_pressed);
		// thumbUnPressed = BitmapFactory.decodeResource(r,
		// R.drawable.btn_unpressed);
		p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setAntiAlias(true);
		setClickable(true);
		setLongClickable(true);
		// 滑块的最大偏移距离
		maxOffset = bottomBitmap.getWidth() / 2 - bottomBitmap.getHeight() / 2;

		defaultPosition = 0;
	}

}
