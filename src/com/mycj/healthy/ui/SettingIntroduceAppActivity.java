package com.mycj.healthy.ui;

import java.util.zip.CheckedInputStream;

import com.mycj.healthy.R;
import com.mycj.healthy.R.layout;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingIntroduceAppActivity extends FragmentActivity {
	private ViewPager viewPager;
	private RadioGroup rgBottom;
	private ImageView imgPoint1, imgPoint2, imgPoint3, imgPoint4;
	private int[] layouts = new int[] { R.layout.app_introduce_1, R.layout.app_introduce_2, R.layout.app_introduce_3, R.layout.app_introduce_4 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_introduce_app);
		viewPager = (ViewPager) findViewById(R.id.vp_instroduce_app);
		rgBottom = (RadioGroup) findViewById(R.id.app_bottom);
		imgPoint1 = ((ImageView) findViewById(R.id.img_point_1));
		imgPoint2 = ((ImageView) findViewById(R.id.img_point_2));
		imgPoint3 = ((ImageView) findViewById(R.id.img_point_3));
		imgPoint4 = ((ImageView) findViewById(R.id.img_point_4));
		imgPoint1.setPressed(true);
		viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return layouts.length;
			}

			@Override
			public Fragment getItem(int id) {
				MyFragment f = new MyFragment();
				Bundle b = new Bundle();
				b.putInt("layout", layouts[id]);
				f.setArguments(b);
				return f;
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				showAnimation();
				clearPonit();
				switch (position) {
				case 0:
					imgPoint1.setPressed(true);
					break;
				case 1:
					imgPoint2.setPressed(true);
					break;
				case 2:
					imgPoint3.setPressed(true);
					break;
				case 3:
					imgPoint4.setPressed(true);
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});

	}

	private class MyFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			int layout = getArguments().getInt("layout");
			View view = inflater.inflate(layout, container, false);
			return view;
		}
	}

	private void clearPonit() {
		imgPoint1.setPressed(false);
		imgPoint2.setPressed(false);
		imgPoint3.setPressed(false);
		imgPoint4.setPressed(false);
	}

	private void showAnimation() {
		rgBottom.setVisibility(View.VISIBLE);
		final ObjectAnimator animator1 = ObjectAnimator.ofFloat(rgBottom, "alpha", 0f, 1f, 0f);
		animator1.setDuration(2000);
		// animator1.setRepeatCount(1); // 动画循环播放的次数
		animator1.start();
		animator1.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				rgBottom.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
	}

}
