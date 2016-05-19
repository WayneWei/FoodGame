package com.example.waynewei.foodgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.waynewei.foodgame.info.model.Friend;
import com.example.waynewei.foodgame.info.model.User;
import com.example.waynewei.foodgame.widget.SystemUiHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = "MainActivity";
	private Realm realm;
	private CallbackManager callbackManager;
	private LoginButton loginButton;
	private FancyButton btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		realm = Realm.getDefaultInstance();
		RealmResults<User> userResult = realm.where(User.class)
				.findAll();

		if(checkInternet()) {
			if (!userResult.isEmpty()) {
				Intent intent = new Intent(MainActivity.this, HomeActivity.class);
				startActivity(intent);
			}
		}
		else {

		}


	}

	public boolean checkInternet() {
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppEventsLogger.activateApp(this);

		callbackManager = CallbackManager.Factory.create();

		loginButton= (LoginButton)findViewById(R.id.login_button);

		loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));

		btnLogin= (FancyButton) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				loginButton.performClick();

				loginButton.setPressed(true);

				loginButton.invalidate();

				loginButton.registerCallback(callbackManager, facebookCallback);

				loginButton.setPressed(false);

				loginButton.invalidate();

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private ProfileTracker mProfileTracker;
	private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {
			AppEventsLogger logger = AppEventsLogger.newLogger(MainActivity.this);
			logger.logEvent("login");

			loginButton.setVisibility(View.GONE);

			realm = Realm.getDefaultInstance();
			realm.beginTransaction();
			final User user = realm.createObject(User.class);

			if (Profile.getCurrentProfile() == null) {
				mProfileTracker = new ProfileTracker() {
					@Override
					protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
						Log.v("facebook - profile", profile2.getFirstName());
						mProfileTracker.stopTracking();
					}
				};
				mProfileTracker.startTracking();
			} else {
				Profile profile = Profile.getCurrentProfile();
				Log.v("facebook - profile", profile.getFirstName());
				Log.d("Profile", String.valueOf(profile));
				final String userPhoto = profile.getProfilePictureUri(150, 150).toString();
				Log.d("Image", userPhoto);
				user.setPhoto(userPhoto);
			}

			Bundle parameters = new Bundle();
			parameters.putString("fields", "name,email,cover,friends{name,picture}");

			new GraphRequest(
					AccessToken.getCurrentAccessToken(),
					"me",
					parameters,
					HttpMethod.GET,
					new GraphRequest.Callback() {
						public String userCover;
						public String userEmail;
						public String friendId;
						public String friendName;
						public String friendPhoto;

						public void onCompleted(GraphResponse response) {
							Log.d("response", response.toString());
							JSONObject object = response.getJSONObject();

							String userId = object.optString("id");
							user.setId(userId);

							String userName = object.optString("name");
							user.setName(userName);

							if (object.has("email")) {
								userEmail = object.optString("email");
								user.setEmail(userEmail);
							}

							if (object.has("cover")) {
								userCover = object.optJSONObject("cover").optString("source");
								user.setCover(userCover);
							}

							realm.commitTransaction();

							RealmList<Friend> friends = new RealmList<Friend>();

							if (object.has("friends")) {
								JSONArray jsonArray = object.optJSONObject("friends").optJSONArray("data");

								for (int i = 0; i < jsonArray.length(); i++) {
									realm.beginTransaction();
									final Friend friend = realm.createObject(Friend.class);
									try {
										friendName = jsonArray.getJSONObject(i).getString("name");
										Log.d("friend", friendName);
										friend.setName(friendName);
									} catch (JSONException e) {
										e.printStackTrace();
									}

									try {
										friendId = jsonArray.getJSONObject(i).getString("id");
										Log.d("friend", friendId);
										friend.setId(friendId);
									} catch (JSONException e) {
										e.printStackTrace();
									}

									try {
										friendPhoto = jsonArray.getJSONObject(i).getJSONObject("picture").optJSONObject("data").optString("url");
										Log.d("friend", friendPhoto);
										friend.setPhotoUrl(friendPhoto);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									user.getFriends().add(friend);
									realm.commitTransaction();
								}

							}

							Intent intent = new Intent(MainActivity.this, HomeActivity.class);
							startActivity(intent);
							finish();

						}
					}
			).executeAsync();

		}

		@Override
		public void onCancel() {
			Log.d("Login Status", "Login attempt canceled");
		}

		@Override
		public void onError(FacebookException e) {
			Log.d("Login Status", "Login attempt failed");
		}
	};


	@Override
	protected void onStop() {
		super.onStop();
		AppEventsLogger.deactivateApp(this);
	}

	private View.OnClickListener errorClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(getApplication(), "Try again button clicked", Toast.LENGTH_LONG).show();
		}
	};

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
