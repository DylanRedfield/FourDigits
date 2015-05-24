package me.dylanredfield.fourdigits;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Random;

public class SelectGameActivity extends ActionBarActivity {
    private TextView mCollaborativeText;
    private Button mCollab;
    private TextView mVsText;
    private Button mComputer;
    private Button mFriendsVs;
    private Typeface mFont;
    private ParseUser mCurrentUser;
    private static Activity mActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        mActivityContext = this;
        while (mActivityContext.getParent() != null) {
            mActivityContext = mActivityContext.getParent();
        }
        mCurrentUser = ParseUser.getCurrentUser();
        instantiateViews();
        setFonts();

        setListners();
    }

    public void instantiateViews() {
        mCollaborativeText = (TextView) findViewById(R.id.collaborative_text);
        mCollab = (Button) findViewById(R.id.friends_button);
        mVsText = (TextView) findViewById(R.id.vs_text);
        mComputer = (Button) findViewById(R.id.computer_button);
        mFriendsVs = (Button) findViewById(R.id.separate_button);
    }

    public void setFonts() {
        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");
        mCollaborativeText.setTypeface(mFont);
        mCollab.setTypeface(mFont);
        mComputer.setTypeface(mFont);
        mVsText.setTypeface(mFont);
        mFriendsVs.setTypeface(mFont);
    }

    public void setListners() {
        mComputer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeParseObject("Single", SelectGameActivity.this, mActivityContext);


            }
        });
        mFriendsVs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentUser.getInt(Keys.NUM_FRIENDS_KEY) > 0) {
                    Intent i = new Intent(getApplicationContext(), SelectFriendsActivity.class);
                    i.putExtra(Keys.GAME_TYPE_EXTRA, Keys.GAME_TYPE_WHO_FIRST_STRING);
                    startActivity(i);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            SelectGameActivity.this);
                    builder.setMessage("You need atleast one friend")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.setTitle("Uh oh!");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        mCollab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentUser.getInt(Keys.NUM_FRIENDS_KEY) > 1) {
                    Intent i = new Intent(getApplicationContext(), SelectFriendsActivity.class);
                    i.putExtra(Keys.GAME_TYPE_EXTRA, Keys.GAME_TYPE_COLLAB_STRING);
                    startActivity(i);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            SelectGameActivity.this);
                    builder.setMessage("You need atleast two friend;")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.setTitle("Uh oh!");
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    public static ParseObject makeParseObject(String type, final Context context,
                                              final Activity activity) {
        final ProgressDialog progressDialog;
        final ParseObject gameObject = new ParseObject("Game");
        String newType = "";

        if (type.equals("Single")) {
            newType = "qIVH6Y13Dw";
        } else if (type.equals("WhoFirst")) {
            newType = "mSymctKi0s";
        }

        gameObject.put("GameType",
                ParseObject.createWithoutData("GameType", newType));
        gameObject.addAll(Keys.CODE_KEY, Arrays.asList(makeAnswer()));
        gameObject.addAll("guessesRemaining", Arrays.asList(new Integer[]{10}));
        gameObject.put("isOver", false);
        gameObject.put("numPlayers", 1);
        gameObject.put("playerStrings", ParseUser.getCurrentUser().getUsername());
        gameObject.getRelation("players").add(ParseUser.getCurrentUser());
        gameObject.addAll("usersTurn",
                Arrays.asList(new String[]{ParseUser.getCurrentUser().getObjectId()}));
        gameObject.getRelation("whoseTurn").add(ParseUser.getCurrentUser());
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        gameObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                Intent i = new Intent(context, GameActivity.class);
                i.putExtra(Keys.OBJECT_ID_STRING, gameObject.getObjectId());
                context.startActivity(i);
                activity.finish();
            }
        });
        return gameObject;

    }

    public static String[] makeAnswer() {
        int one, two, three, four;
        String[] stringArray = new String[4];
        Random generator = new Random();

        one = generator.nextInt(9) + 1;
        two = generator.nextInt(9) + 1;

        while (one == two) {
            two = generator.nextInt(9) + 1;
        }

        three = generator.nextInt(9) + 1;
        while (one == three || two == three) {
            three = generator.nextInt(9) + 1;
        }

        four = generator.nextInt(9) + 1;
        while (one == four || two == four || three == four) {
            four = generator.nextInt(9) + 1;
        }

        stringArray[0] = "" + one;
        stringArray[1] = "" + two;
        stringArray[2] = "" + three;
        stringArray[3] = "" + four;
        return stringArray;
    }
}
