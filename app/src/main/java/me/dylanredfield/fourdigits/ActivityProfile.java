package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FunctionCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ActivityProfile extends ActionBarActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private TextView mWonText;
    private TextView mLostText;
    private TextView mAvgGuesses;
    private TextView mWonNumber;
    private TextView mLostNumber;
    private TextView mAvgNumber;

    private Button mChangePassword;
    private Button mEditProfile;
    private Button mEditFriends;
    private Button mLogOut;
    private ParseUser mCurrentUser;
    private int mGames;
    private int mGuesses;
    private ProgressDialog progressDialog;

    private Typeface mFont;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mCurrentUser = ParseUser.getCurrentUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mCurrentUser.getUsername());

        mWonText = (TextView) findViewById(R.id.won);
        mLostText = (TextView) findViewById(R.id.lost);
        mAvgGuesses = (TextView) findViewById(R.id.avg_guess);
        mWonNumber = (TextView) findViewById(R.id.won_number);
        mLostNumber = (TextView) findViewById(R.id.lost_number);
        mAvgNumber = (TextView) findViewById(R.id.avg_guess_number);

        mWonText.setTypeface(mFont);
        mLostText.setTypeface(mFont);
        mAvgGuesses.setTypeface(mFont);
        mWonNumber.setTypeface(mFont);
        mLostNumber.setTypeface(mFont);
        mAvgNumber.setTypeface(mFont);

        mChangePassword = (Button) findViewById(R.id.change_pass);
        mEditProfile = (Button) findViewById(R.id.edit_profile_picture);
        mEditFriends = (Button) findViewById(R.id.edit_friends);
        mLogOut = (Button) findViewById(R.id.logout);

        mChangePassword.setTypeface(mFont);
        mEditProfile.setTypeface(mFont);
        mEditFriends.setTypeface(mFont);
        mLogOut.setTypeface(mFont);
        setListeners();

        mWonText.setText("Games Won");
        mLostText.setText("Games Lost");
        mAvgGuesses.setText("Average Guesses");

        mChangePassword.setText("Change Password");
        mEditProfile.setText("Edit Profile Picture");
        mEditFriends.setText("Add Friend");
        mLogOut.setText("Logout");

        mWonNumber.setText("" + mCurrentUser.getInt(Keys.TOTAL_WINS_KEY));
        mLostNumber.setText("" + mCurrentUser.getInt(Keys.TOTAL_WINS_KEY));
        ParseQuery gameQuery = ParseQuery.getQuery("Game");
        gameQuery.whereEqualTo("players", mCurrentUser);
        gameQuery.whereEqualTo(Keys.IS_OVER_KEY, true);

        final ParseQuery guessQuery = ParseQuery.getQuery("Guess");
        guessQuery.whereEqualTo("player", mCurrentUser);
        guessQuery.whereMatchesQuery("Game", gameQuery);

        gameQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                mGames = i;
                guessQuery.countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, ParseException e) {
                        mGuesses = i;
                        if (mGames != 0) {
                            mAvgNumber.setText("" + (mGuesses / mGames));
                        } else {
                            mAvgNumber.setText("0");
                        }
                    }
                });
            }
        });
    }

    public void setListeners() {
        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityProfile.this);

                final EditText edittext = new EditText(ActivityProfile.this);
                edittext.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alert.setMessage("Ensure the password is correct before submitting");
                alert.setTitle("Change Password");

                alert.setView(edittext);

                alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put("myNewPass", edittext.getText().toString().trim());

                        ParseCloud.callFunctionInBackground("editUser", params,
                                new FunctionCallback<Object>() {
                                    @Override
                                    public void done(Object o, ParseException e) {
                                        if (e == null) {
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(),
                                                    "Password Changed",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            AlertDialog.Builder builder = new
                                                    AlertDialog.Builder(ActivityProfile.this);
                                            builder.setMessage(e.getMessage())
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new
                                                            DialogInterface.OnClickListener() {
                                                                public void onClick
                                                                        (DialogInterface dialog,
                                                                         int id) {
                                                                    dialog.cancel();
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

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        mEditFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityProfile.this);

                final EditText edittext = new EditText(ActivityProfile.this);
                edittext.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                alert.setMessage("Ensure the username is correct before adding");
                alert.setTitle("New Friend");

                alert.setView(edittext);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        String inputUsername = edittext.getText()
                                .toString().trim().toLowerCase();

                        ParseQuery userQuery = ParseUser.getQuery();
                        userQuery.whereEqualTo(Keys.USERNAME_KEY, inputUsername);

                        try {
                            if (userQuery.count() > 0 && !(userQuery.getFirst().getObjectId()
                                    .equals(mCurrentUser.getObjectId()))) {
                                ParseRelation myFriends = ParseUser.getCurrentUser()
                                        .getRelation(Keys.FRIENDS_KEY);
                                myFriends.add(userQuery.getFirst());
                                mCurrentUser.saveInBackground();
                                HashMap<String, Object> params = new HashMap<String, Object>();

                                params.put("friendId", userQuery.getFirst().getObjectId());
                                params.put("myName", ParseUser.getCurrentUser().getUsername());

                                ParseCloud.callFunctionInBackground
                                        ("addFriend", params, new FunctionCallback<String>() {
                                            @Override
                                            public void done(String s, ParseException e) {
                                                if (e == null) {
                                                    mCurrentUser.increment
                                                            (Keys.NUM_FRIENDS_KEY);
                                                    mCurrentUser.saveInBackground();

                                                } else {
                                                    AlertDialog.Builder builder = new
                                                            AlertDialog.Builder(ActivityProfile.this);
                                                    builder.setMessage(e.getMessage())
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK", new
                                                                    DialogInterface.OnClickListener() {
                                                                        public void onClick
                                                                                (DialogInterface dialog,
                                                                                 int id) {
                                                                            dialog.cancel();
                                                                        }
                                                                    });
                                                    builder.setTitle("Whoops!");
                                                    AlertDialog alert = builder.create();
                                                    alert.show();
                                                }
                                            }
                                        });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });
    }

    public void logOut() {
        ParseUser.logOut();
        mCurrentUser = ParseUser.getCurrentUser();

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
                finish();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "1mind_" + timeStamp + ".png";
        File photo = new File(Environment.getExternalStorageDirectory(), imageFileName);
        mCurrentPhotoPath = photo.getAbsolutePath();
        return photo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //bitmap = ImageHelper.convertToMutable(bitmap);

            bitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

            byte[] bytearray = stream.toByteArray();

            if (bytearray != null) {
                ParseFile parseFile = new ParseFile("ProfilePicture.png", bytearray);
                mCurrentUser.put("profilePicture", parseFile);
                progressDialog = new ProgressDialog(ActivityProfile.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Saving");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                    }
                });
            }
        }
    }
}
