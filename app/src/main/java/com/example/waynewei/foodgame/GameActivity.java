package com.example.waynewei.foodgame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.waynewei.foodgame.info.adapter.FoodListAdapter;
import com.example.waynewei.foodgame.info.model.Food;
import com.example.waynewei.foodgame.info.model.Question;
import com.example.waynewei.foodgame.save_data.ParseFood;
import com.example.waynewei.foodgame.service.HttpAsyncTask;
import com.example.waynewei.foodgame.service.TaskCompleted;
import com.example.waynewei.foodgame.widget.RecyclerViewDisabler;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class GameActivity extends AppCompatActivity implements TaskCompleted {
	private static final int GRID_VIEW = 0;
	private static final int LIST_VIEW = 1;
	private static final int total_time = 300000;
	private RecyclerView recyclerView;
	private int currentView;
	private Menu menu;
	private ArrayList<Food> foods = new ArrayList<>();
	private FoodListAdapter listAdapter;
	private LinearLayoutManager listView;
	private GridLayoutManager gridView;
	private ProgressBar progressBar;
	private CountDownTimer timer;
	private long time_spend = 0;
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Realm realm = Realm.getDefaultInstance();
		RealmResults<Question> results = realm.where(Question.class).findAll();
		realm.beginTransaction();
		results.deleteAllFromRealm();
		realm.commitTransaction();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		assert getSupportActionBar() != null;
		getSupportActionBar().setIcon(R.drawable.ic_timer_white_24dp);
		loadData();

		listAdapter = new FoodListAdapter(this, foods);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setHasFixedSize(true);
		recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
		listView = new LinearLayoutManager(this);
		gridView = new GridLayoutManager(this, 2);

		recyclerView.setLayoutManager(listView);
		recyclerView.setAdapter(listAdapter);
		currentView = LIST_VIEW;

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		mediaPlayer = new MediaPlayer();

	}

	public void loadData(){
		String url=getResources().getString(R.string.api_get_food);
		new HttpAsyncTask(this).execute(url);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_game, menu);
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.grid_view:
				currentView = GRID_VIEW;
				menu.findItem(R.id.grid_view).setVisible(false);
				menu.findItem(R.id.list_view).setVisible(true);
				recyclerView.setLayoutManager(gridView);
				recyclerView.setAdapter(listAdapter);
				return true;

			case R.id.list_view:
				currentView = LIST_VIEW;
				menu.findItem(R.id.grid_view).setVisible(true);
				menu.findItem(R.id.list_view).setVisible(false);
				recyclerView.setLayoutManager(listView);
				recyclerView.setAdapter(listAdapter);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onTaskComplete(String response) {
		Log.d("Response", response+"");
		ParseFood parseFood = new ParseFood(response);
		foods = parseFood.getFoodList();
		listAdapter.updateItems(foods);
		timer = new CountDownTimer(total_time, 1000) {

			public void onTick(long millisUntilFinished) {
				time_spend = (total_time - millisUntilFinished)/1000;
				int seconds = (int) (millisUntilFinished / 1000);
				int timeLeft = seconds;
				int minutes = seconds / 60;
				int hours = minutes / 60;
				minutes = minutes % 60;
				seconds = seconds % 60;
				getSupportActionBar().setTitle("   " + hours + " 時 " + minutes + " 分 " + seconds + " 秒");
				int progress = (int) ((time_spend*100/(total_time/1000)));
				Log.d("test", String.valueOf(time_spend)+ " " + progress + " %");
				progressBar.setSecondaryProgress(progress);
				if(timeLeft==20){
					mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.countdown);
					mediaPlayer.start();
				}
			}

			public void onFinish() {
				progressBar.setSecondaryProgress(100);
				getSupportActionBar().setTitle("時間到！");
				RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
				recyclerView.addOnItemTouchListener(disabler);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(GameActivity.this, FinalActivity.class);
						startActivity(intent);
						finish();
					}
				}, 2000);
			}
		}.start();
	}

	public void setCurrentProgress(int currentProgress) {
		progressBar.setProgress(currentProgress);
		if(currentProgress==100){
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
			timer.cancel();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(GameActivity.this, FinalActivity.class);
					intent.putExtra("Spend Time",time_spend);
					startActivity(intent);
					finish();
				}
			}, 2000);
		}
	}

	@Override
	public void onBackPressed() {

		new MaterialDialog.Builder(this)
				.title(getString(R.string.exit))
				.titleGravity(GravityEnum.CENTER)
				.positiveText(R.string.confirm)
				.negativeText(R.string.cancel)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
						if (mediaPlayer != null) {
							mediaPlayer.stop();
							mediaPlayer.release();
							mediaPlayer = null;
						}
						timer.cancel();
						finish();
					}
				})
				.show();
	}
}
