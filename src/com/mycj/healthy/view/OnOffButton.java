package com.mycj.healthy.view;

import com.mycj.healthy.R;
import com.mycj.healthy.util.DensityUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OnOffButton extends View {
	private Bitmap bottom;
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

	public OnOffButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OnOffButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public OnOffButton(Context context) {
		super(context);
		init();
		setClickable(true);
		setLongClickable(true);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isDrag) {
					isOn = !isOn;
					if (isOn) {
						defaultPosition = -(bottom.getWidth() - frame.getWidth());
					} else {
						defaultPosition = 0;
					}
					invalidate();
				} else {

				}
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// float max = bottom.getWidth()/2-bottom.getHeight();
		// float max = getWidth()-bottom.getHeight();
		super.onTouchEvent(event);// 这一句不能少，否则无法触发onclick事件
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			downX = xMove = event.getX();
			isDrag = false;
			break;
		case MotionEvent.ACTION_MOVE:

			xMove = event.getX();
			offset = xMove - downX;
			if (Math.abs(offset) < 5) {
				break;
			}
			isDrag = true;
			// 开
			if (isOn) {
				if (offset < 0) {
					break;
				} else {
					defaultPosition = -(bottom.getWidth() - frame.getWidth());
					// 当超过最大偏移量时
					if (Math.abs(offset) >= maxOffset) {
						offset = maxOffset;// 开关右移 offset为正
						invalidate();
						isOn = false; // 关闭
						if (mOnCheckListener != null) {
							mOnCheckListener.onChange(isOn);
						}
						break;
					} else if (Math.abs(offset) < 0) {
						offset = 0;
					}
					invalidate();
				}
			}
			// 关
			else {
				if (offset > 0) {
					break;
				} else {
					defaultPosition = 0;
					// 当超过最大偏移量时
					if (Math.abs(offset) >= maxOffset) {
						offset = -maxOffset;// 开关←移 offset为副
						isOn = true;
						if (mOnCheckListener != null) {
							mOnCheckListener.onChange(isOn);
						}
						break;
					} else if (Math.abs(offset) < 0) {
						offset = 0;
					}

					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// float xUp = event.getX();
			// offset = xUp - downX;
			if (Math.abs(offset) < 5) {
				break;
			}
			if (isOn) {
				if (offset < 0) {
					break;
				} else {
					defaultPosition = -(bottom.getWidth() - frame.getWidth());
					// 当滑到一半以上时，松开手指就移动到关闭位置
					if (Math.abs(offset) > maxOffset / 2) {
						offset = maxOffset;
						isOn = false;
						if (mOnCheckListener != null) {
							mOnCheckListener.onChange(isOn);
						}
						invalidate();
						break;
					} else {
						offset = 0;
						if (mOnCheckListener != null) {
							mOnCheckListener.onChange(isOn);
						}
						invalidate();
						break;
					}
				}
			} else {
				if (offset > 0) {
					break;
				} else {
					defaultPosition = 0;
					// 当滑到一半以上时，松开手指就移动到关闭位置
					if (Math.abs(offset) > maxOffset / 2) {
						offset = -maxOffset;
						isOn = true;
						if (mOnCheckListener != null) {
							mOnCheckListener.onChange(isOn);
						}
						invalidate();
						break;
					} else {
						offset = 0;
						if (mOnCheckListener != null) {
							mOnCheckListener.onChange(isOn);
						}
						invalidate();
						break;
					}
				}
			}
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

		// if(isOn){
		// //画底部
		// canvas.drawBitmap(bottom , maxOffset-offset,0, p);
		// //画滑块
		// canvas.drawBitmap(thumbPressed,maxOffset-offset, 0, p);
		// }else{
		// 画底部
		canvas.drawBitmap(bottom, defaultPosition + offset, 0, p);
		// 画滑块
		canvas.drawBitmap(thumbPressed, defaultPosition + offset, 0, p);
		// }

		// 画边框
		canvas.drawBitmap(frame, 0, 0, p);
		super.onDraw(canvas);
	}

	private void init() {
		Resources r = getResources();
		bottom = BitmapFactory.decodeResource(r, R.drawable.bottom);
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
		maxOffset = bottom.getWidth() / 2 - bottom.getHeight() / 2;

		defaultPosition = 0;
	}

}
