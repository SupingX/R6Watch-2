package com.mycj.healthy.fragment;

import com.mycj.healthy.R;
import com.mycj.healthy.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 移除
 * 
 * @author Administrator
 *
 */
public class SportFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_sport, container, false);

		return view;
	}
}
