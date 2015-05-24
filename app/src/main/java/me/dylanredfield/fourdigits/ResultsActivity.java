package me.dylanredfield.fourdigits;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends ActionBarActivity {

    private TextView mResult;
    private TextView mGameType;
    private TextView mPlayers;
    private TextView mAnswerOne;
    private TextView mAnswerTwo;
    private TextView mAnswerThree;
    private TextView mAnswerFour;
    private Button mViewBoard;

    private Typeface mFont;
    private ParseUser mCurrentUser;
    private ParseObject mGameObject;
    private String mGameTypeString;

    private ArrayList<RelativeLayout> mLayoutList = new ArrayList<>();
    private ArrayList<TextView> mNameList = new ArrayList<>();
    private ArrayList<TextView> mAttemptList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        mCurrentUser = ParseUser.getCurrentUser();

        try {
            mGameObject = ParseObject.createWithoutData(Keys.GAME_KEY, getIntent()
                    .getStringExtra(Keys.OBJECT_ID_STRING)).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            mGameTypeString = ((ParseObject) mGameObject.get("GameType"))
                    .fetchIfNeeded().getString("type");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (mGameTypeString.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
            setList();
        }

        mResult = (TextView) findViewById(R.id.result);
        mGameType = (TextView) findViewById(R.id.game_type);
        mPlayers = (TextView) findViewById(R.id.players);
        mAnswerOne = (TextView) findViewById(R.id.answer_1);
        mAnswerTwo = (TextView) findViewById(R.id.answer_2);
        mAnswerThree = (TextView) findViewById(R.id.answer_3);
        mAnswerFour = (TextView) findViewById(R.id.answer_4);

        mViewBoard = (Button) findViewById(R.id.view_board);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mResult.setTypeface(mFont);
        mGameType.setTypeface(mFont);
        mPlayers.setTypeface(mFont);
        mAnswerOne.setTypeface(mFont);
        mAnswerTwo.setTypeface(mFont);
        mAnswerThree.setTypeface(mFont);
        mAnswerFour.setTypeface(mFont);
        mViewBoard.setTypeface(mFont);

        if (mGameTypeString.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
            if (mGameObject.getList(Keys.WINNERS_KEY).size() > 0) {
                mResult.setText("You Won");
            } else {
                mResult.setText("You Lost");
            }
        } else {
            if (mGameObject.getList(Keys.WINNERS_KEY) != null) {
                String[] winnerList = mGameObject.getList(Keys.WINNERS_KEY)
                        .toArray(new String[0]);
                for (int i = 0; i < winnerList.length; i++) {
                    if (mCurrentUser.getObjectId().equals(winnerList[i])) {
                        if (winnerList.length > 1) {
                            mResult.setText("You Tied");
                        } else {
                            mResult.setText("You Won");
                        }
                    }
                }
                if (!mResult.getText().toString().equals("You Won") &&
                        !mResult.getText().toString().equals("You Tied")) {
                    mResult.setText("You Lost");
                }
            } else {
                mResult.setText("You Lost");
            }
        }

        if (mGameTypeString.equals(Keys.GAME_TYPE_COLLAB_STRING)) {

            mGameType.setText("Group");
        } else if (mGameTypeString.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
            mGameType.setText("Vs.");
        } else {
            mGameType.setText("Single");
        }

        mPlayers.setText(mGameObject.getString(Keys.PLAYER_STRINGS_KEY));

        List tempList = mGameObject.getList(Keys.CODE_KEY);

        mAnswerOne.setText((String) tempList.get(0));
        mAnswerTwo.setText((String) tempList.get(1));
        mAnswerThree.setText((String) tempList.get(2));
        mAnswerFour.setText((String) tempList.get(3));


        mViewBoard.setText("View Your Board");
        mViewBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GameActivity.class);
                i.putExtra(Keys.OBJECT_ID_STRING, mGameObject.getObjectId());
                startActivity(i);
            }
        });

        if (mGameTypeString.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
            ParseRelation attemptsRelation = mGameObject.getRelation("players");
            ParseQuery userQuery = attemptsRelation.getQuery();
            userQuery.findInBackground(new FindCallback() {
                @Override
                public void done(List list, ParseException e) {

                }

                @Override
                public void done(Object o, Throwable throwable) {
                    List<ParseObject> userList = (List<ParseObject>) o;

                    ParseObject tempObject = null;

                    for (int i = 0; i < userList.size(); i++) {
                        if (userList.get(i).getString(Keys.USERNAME_KEY)
                                .equals(ParseUser.getCurrentUser().getUsername())) {
                            tempObject = userList.remove(i);
                        }
                    }
                    userList.add(0, tempObject);

                    for (int i = 0; i < userList.size(); i++) {
                        mLayoutList.get(i).setVisibility(View.VISIBLE);
                        mNameList.get(i).setText(userList.get(i).getString(Keys.USERNAME_KEY));

                        ParseQuery guessQuery = ParseQuery.getQuery("Guess");
                        guessQuery.whereEqualTo("player", userList.get(i));
                        guessQuery.whereEqualTo("Game", mGameObject);
                        try {
                            mAttemptList.get(i).setText("" + guessQuery.count());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        mAnswerOne.getViewTreeObserver().addOnGlobalLayoutListener
                (new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        mAnswerOne.setHeight(mAnswerOne.getWidth());
                        mAnswerTwo.setHeight(mAnswerOne.getWidth());
                        mAnswerThree.setHeight(mAnswerOne.getWidth());
                        mAnswerFour.setHeight(mAnswerOne.getWidth());
                    }
                });
    }

    public void setList() {
        mLayoutList.add((RelativeLayout) findViewById(R.id.layout_1));
        mLayoutList.add((RelativeLayout) findViewById(R.id.layout_2));
        mLayoutList.add((RelativeLayout) findViewById(R.id.layout_3));
        mLayoutList.add((RelativeLayout) findViewById(R.id.layout_4));
        mLayoutList.add((RelativeLayout) findViewById(R.id.layout_5));
        mLayoutList.add((RelativeLayout) findViewById(R.id.layout_6));

        mNameList.add((TextView) findViewById(R.id.attempt_name_1));
        mNameList.add((TextView) findViewById(R.id.attempt_name_2));
        mNameList.add((TextView) findViewById(R.id.attempt_name_3));
        mNameList.add((TextView) findViewById(R.id.attempt_name_4));
        mNameList.add((TextView) findViewById(R.id.attempt_name_5));
        mNameList.add((TextView) findViewById(R.id.attempt_name_6));

        mAttemptList.add((TextView) findViewById(R.id.attempt_1));
        mAttemptList.add((TextView) findViewById(R.id.attempt_2));
        mAttemptList.add((TextView) findViewById(R.id.attempt_3));
        mAttemptList.add((TextView) findViewById(R.id.attempt_4));
        mAttemptList.add((TextView) findViewById(R.id.attempt_5));
        mAttemptList.add((TextView) findViewById(R.id.attempt_6));

        for (TextView t : mNameList) {
            t.setTypeface(mFont);
        }
        for (TextView t : mAttemptList) {
            t.setTypeface(mFont);
        }
    }
}
