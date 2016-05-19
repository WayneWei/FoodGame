package com.example.waynewei.foodgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.waynewei.foodgame.widget.SystemUiHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FinalActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final);

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.fullscreen_content);
		TextView mResult = (TextView) findViewById(R.id.status);
		TextView mTime = (TextView) findViewById(R.id.time);

		float time = getIntent().getLongExtra("Spend Time", 0);
		if(time==0) {
			MediaPlayer.create(this, R.raw.fail).start();
			mResult.setText(getString(R.string.fail));
			relativeLayout.setBackgroundColor(getResources().getColor(R.color.red));
			mResult.setTextColor(getResources().getColor(R.color.white));
			mTime.setVisibility(View.GONE);
		}
		else {
			MediaPlayer.create(this, R.raw.success).start();
			mResult.setText(getString(R.string.success));
			relativeLayout.setBackgroundColor(getResources().getColor(R.color.blue));
			mResult.setTextColor(getResources().getColor(R.color.white));
			int seconds = (int) time;
			int minutes = seconds / 60;
			int hours = minutes / 60;
			minutes = minutes % 60;
			seconds = seconds % 60;
			mTime.setText(hours + " 時 " + minutes + " 分 " + seconds + " 秒");
		}
		Log.d("test", String.valueOf(time));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

		SystemUiHelper uiHelper = new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE ,flags);
		uiHelper.hide();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(FinalActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
}
