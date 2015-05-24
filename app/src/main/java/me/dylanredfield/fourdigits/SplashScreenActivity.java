package me.dylanredfield.fourdigits;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;


public class SplashScreenActivity extends Activity {
    private ImageView mLogo;
    private ParseInstallation mInstallation;
    private SharedPreferences mPref;
    private boolean isFirstTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                mPref = getSharedPreferences(Keys.PREF_STRING, Activity.MODE_PRIVATE);
                isFirstTime = mPref.getBoolean(Keys.FIRST_TIME_STRING, true);

                Log.d("Users", "!isLinked");
                // If current user is NOT anonymous user
                // Get current user data from Parse.com
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (!isFirstTime) {
                    try {
                        ParseInstallation.getCurrentInstallation().save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Send logged in users to Welcome.class
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    try {
                        ParseInstallation.getCurrentInstallation().save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mInstallation = ParseInstallation.getCurrentInstallation();

                    currentUser.put(Keys.COINS_KEY, 0);
                    currentUser.put(Keys.COLLAB_WINS_KEY, 0);
                    currentUser.put(Keys.NUM_FRIENDS_KEY, 0);
                    currentUser.put(Keys.SINGLE_LOSSES_KEY, 0);
                    currentUser.put(Keys.SINGLE_WINS_KEY, 0);
                    currentUser.put(Keys.TOTAL_LOSSES_KEY, 0);
                    currentUser.put(Keys.TOTAL_TIES_KEY, 0);
                    currentUser.put(Keys.TOTAL_WINS_KEY, 0);
                    currentUser.put(Keys.VS_LOSSES_KEY, 0);
                    currentUser.put(Keys.VS_TIES_KEY, 0);
                    currentUser.put(Keys.VS_WINS_KEY, 0);
                    currentUser.put(Keys.COLLAB_LOSSES_KEY, 0);
                    try {
                        currentUser.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    mInstallation.put(Keys.USER_KEY, currentUser);

                    try {
                        mInstallation.save();
                        mPref.edit().putBoolean(Keys.FIRST_TIME_STRING, false).commit();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }


                }
            }
        }, 1000);


    }

}
