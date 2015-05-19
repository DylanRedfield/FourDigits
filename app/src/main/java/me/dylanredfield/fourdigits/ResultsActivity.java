package me.dylanredfield.fourdigits;

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
            String[] winnerList = mGameObject.getList(Keys.WINNERS_KEY)
                    .toArray(new String[0]);
            for (int i = 0; i < winnerList.length; i++) {
                if (mCurrentUser.getObjectId().equals(winnerList[i])) {
                    mResult.setText("You Won");
                }
            }
            if (!mResult.getText().toString().equals("You Won")) {
                mResult.setText("You Lost");
            }
        }

        mGameType.setText(mGameTypeString);

        mPlayers.setText(mGameObject.getString(Keys.PLAYER_STRINGS_KEY));

        List tempList = mGameObject.getList(Keys.CODE_KEY);

        //TODO make square
        mAnswerOne.setText((String) tempList.get(0));
        mAnswerTwo.setText((String) tempList.get(1));
        mAnswerThree.setText((String) tempList.get(2));
        mAnswerFour.setText((String) tempList.get(3));

        //TODO show attempts

        mViewBoard.setText("View Your Board");
        mViewBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GameActivity.class);
                i.putExtra(Keys.OBJECT_ID_STRING, mGameObject.getObjectId());
                startActivity(i);
            }
        });

    }
}
