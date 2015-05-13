package me.dylanredfield.fourdigits;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
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
        mLogo = new ImageView(getApplicationContext());
        setContentView(mLogo);

        mPref = getSharedPreferences(ParseKeys.PREF_STRING, Activity.MODE_PRIVATE);
        isFirstTime = mPref.getBoolean(ParseKeys.FIRST_TIME_STRING, true);

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

            currentUser.put(ParseKeys.COINS_KEY, 0);
            currentUser.put(ParseKeys.COLLAB_WINS_KEY, 0);
            currentUser.put(ParseKeys.NUM_FRIENDS_KEY, 0);
            currentUser.put(ParseKeys.SINGLE_LOSSES_KEY, 0);
            currentUser.put(ParseKeys.SINGLE_WINS_KEY, 0);
            currentUser.put(ParseKeys.TOTAL_LOSSES_KEY, 0);
            currentUser.put(ParseKeys.TOTAL_TIES_KEY, 0);
            currentUser.put(ParseKeys.TOTAL_WINS_KEY, 0);
            currentUser.put(ParseKeys.VS_LOSSES_KEY, 0);
            currentUser.put(ParseKeys.VS_TIES_KEY, 0);
            currentUser.put(ParseKeys.VS_WINS_KEY, 0);
            currentUser.put(ParseKeys.COLLAB_LOSSES_KEY, 0);
            try {
                currentUser.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mInstallation.put(ParseKeys.USER_KEY, currentUser);

            try {
                mInstallation.save();
                mPref.edit().putBoolean(ParseKeys.FIRST_TIME_STRING, false).commit();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }


        }
    }

}
