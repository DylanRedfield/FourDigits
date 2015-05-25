package me.dylanredfield.fourdigits;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class SplashScreenActivity extends Activity {
    private ImageView mLogo;
    private ParseInstallation mInstallation;
    private SharedPreferences mPref;
    private boolean isFirstTime;
    private ParseUser mCurrentUser;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
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
                mCurrentUser = ParseUser.getCurrentUser();
                if (!isFirstTime) {
                    ParseInstallation.getCurrentInstallation()
                            .saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (isOnline()) {
                                        Intent i = new Intent
                                                (getApplicationContext(), MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                SplashScreenActivity.this);
                                        builder.setMessage("No Connection")
                                                .setPositiveButton("Try Again?", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        onCreate(savedInstanceState);
                                                    }
                                                })
                                                .setCancelable(false);
                                        builder.setTitle("Whoops!");
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }

                                }
                            });
                    // Send logged in users to Welcome.class

                } else {
                    ParseInstallation.getCurrentInstallation()
                            .saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        mInstallation = ParseInstallation.getCurrentInstallation();

                                        mCurrentUser.put(Keys.COINS_KEY, 0);
                                        mCurrentUser.put(Keys.COLLAB_WINS_KEY, 0);
                                        mCurrentUser.put(Keys.NUM_FRIENDS_KEY, 0);
                                        mCurrentUser.put(Keys.SINGLE_LOSSES_KEY, 0);
                                        mCurrentUser.put(Keys.SINGLE_WINS_KEY, 0);
                                        mCurrentUser.put(Keys.TOTAL_LOSSES_KEY, 0);
                                        mCurrentUser.put(Keys.TOTAL_TIES_KEY, 0);
                                        mCurrentUser.put(Keys.TOTAL_WINS_KEY, 0);
                                        mCurrentUser.put(Keys.VS_LOSSES_KEY, 0);
                                        mCurrentUser.put(Keys.VS_TIES_KEY, 0);
                                        mCurrentUser.put(Keys.VS_WINS_KEY, 0);
                                        mCurrentUser.put(Keys.COLLAB_LOSSES_KEY, 0);
                                        mCurrentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                mInstallation.put(Keys.USER_KEY, mCurrentUser);

                                                mInstallation.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            mPref.edit().putBoolean(Keys.FIRST_TIME_STRING
                                                                    , false).apply();
                                                            Intent i = new Intent(getApplicationContext()
                                                                    , MainActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        } else {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                                                    SplashScreenActivity.this);
                                                            builder.setMessage("No connection")
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            //do things
                                                                            onCreate(savedInstanceState);
                                                                        }
                                                                    });
                                                            builder.setTitle("Whoops!");
                                                            AlertDialog alert = builder.create();
                                                            alert.show();
                                                        }
                                                    }
                                                });

                                            }
                                        });


                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                SplashScreenActivity.this);
                                        builder.setMessage("No connection")
                                                .setCancelable(false)
                                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        onCreate(savedInstanceState);
                                                    }
                                                });
                                        builder.setTitle("Whoops!");
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                }
                            });


                }
            }
        }, 1000);


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
