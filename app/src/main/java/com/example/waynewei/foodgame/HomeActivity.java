package com.example.waynewei.foodgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.waynewei.foodgame.info.model.User;
import com.example.waynewei.foodgame.widget.SystemUiHelper;
import com.facebook.login.LoginManager;

import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class HomeActivity extends AppCompatActivity {

	private Realm realm;
	private String photo;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		realm = Realm.getDefaultInstance();

		RealmResults<User> userRealmResults = realm.where(User.class).findAll();
		for(User user : userRealmResults){
			name = user.getName();
			photo = user.getPhoto();
		}


		ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);
		TextView userName = (TextView) findViewById(R.id.userName);

		userName.setText(name);
		Glide.with(this).load(photo).bitmapTransform(new CropCircleTransformation(this)).into(thumbnail);

		Button startBtn = (Button) findViewById(R.id.start);

		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, GameActivity.class);
				startActivity(intent);
			}
		});

		Button logoutBtn = (Button) findViewById(R.id.log_out);

		logoutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new MaterialDialog.Builder(HomeActivity.this)
						.title(R.string.log_out_confirm)
						.positiveText(R.string.confirm)
						.negativeText(R.string.cancel)
						.onPositive(new MaterialDialog.SingleButtonCallback() {
							public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
								LoginManager.getInstance().logOut();
								RealmResults<User> results = realm.where(User.class).findAll();
								realm.beginTransaction();
								results.deleteAllFromRealm();
								realm.commitTransaction();
								Intent intent = new Intent(HomeActivity.this, MainActivity.class);
								startActivity(intent);
								finish();
							}
						})
						.show();
			}
		});

	}

	@Override
	public void onBackPressed() {
		this.finishAffinity();
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

		SystemUiHelper uiHelper =  new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE ,flags);
		uiHelper.hide();
	}

}
