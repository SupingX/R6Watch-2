package com.mycj.healthy;
import android.support.v4.app.Fragment;


public abstract class LazyFragment extends Fragment{

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		
		if (getUserVisibleHint()) {
			isVisibleToUser = true;
			onVisiable();
		}else {
			isVisibleToUser = false;
			onInVisiable();
		}
		
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	
	private void onInVisiable() {
		lazyLoad();
	}

	protected void onVisiable() {
		
	}
	
	/**
	 * 懒加载
	 */
	public abstract void lazyLoad();
	
}
