package com.mycj.healthy.view;

import java.util.ArrayList;
import java.util.List;





import com.mycj.healthy.entity.HeartRateData;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class HeartRateFrameLayout extends FrameLayout{
	/**
	 * 背景
	 */
	private HeartRateBackgroudView bgView;
	/**
	 * 前面移动的数据
	 */
	private HeartRatePathView pathView;
	/**
	 * 数据 ：心率
	 */
	private List<HeartRateData> heartRates = new ArrayList<HeartRateData>();
	
	public HeartRateFrameLayout(Context context) {
		super(context);
		init(context);
	}

	public HeartRateFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public HeartRateFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context){
		bgView = new HeartRateBackgroudView(context);
		pathView = new HeartRatePathView(context);
		this.addView(bgView);
		this.addView(pathView);
		
	}
	
	public  List<HeartRateData> getData()	{
		return heartRates;
	}
	
	public void setData(HeartRateData data){
		Log.e("HeartRateFrameLayout", "_____pathView :" + pathView);
		pathView.setData(data);
		heartRates.add(data);
		if(mOnHeartRateChangeListener!=null){
			mOnHeartRateChangeListener.onChange(heartRates);
		}
	}
	
	/**
	 * 心率变化时的回调，可以获得所有的心率数据
	 * @author Administrator
	 *
	 */
	public interface OnHeartRateChangeListener {
		public void onChange(List<HeartRateData> heartRates);
	}
	private OnHeartRateChangeListener mOnHeartRateChangeListener;
	public void setOnHeartRateChangeListener(OnHeartRateChangeListener l){
		this.mOnHeartRateChangeListener = l;
	}
	
	public void reset(){
		pathView.reset();
	}
}
