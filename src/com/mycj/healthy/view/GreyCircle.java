package com.mycj.healthy.view;

import com.mycj.healthy.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GreyCircle extends View{

	public GreyCircle(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GreyCircle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public GreyCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		setMeasuredDimension(getWidth()-25, getHeight()-25);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setStrokeWidth(8);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(getResources().getColor(R.color.grey_light));
		canvas.drawCircle(getWidth()/2, getHeight()/2, getHeight()/2-40, p);
		super.onDraw(canvas);
	}

}
