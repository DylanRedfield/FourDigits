package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class GameActivity extends ActionBarActivity {
    private ParseObject mGameObject;
    private String[] mAnswerArray;
    private ArrayList<Button> mButtonList;
    private ArrayList<Button> mInputList;
    private ArrayList<TextView> mTextLabels;
    private ArrayList<TextView> mTextSpots;
    private ArrayList<TextView> mTextNums;
    private Typeface mFont;
    private Button mCheck;
    private int mCurrentButtonInt;
    private int mPreviousButtonInt;
    private int mCurrentNumCorrectSpot;
    private int mInput;
    private boolean isChecked;
    private int mNumCorrect;
    private int mNumCorrectSpot;
    private String mGameType;
    private ParseQuery mGamesQuery;
    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getStuff();

        addToList();

        setDefaults();

        localTextViews();

        queryParse();
    }

    public void getStuff() {

        mCurrentUser = ParseUser.getCurrentUser();

        try {
            mGameObject = ParseObject.createWithoutData("Game", getIntent()
                    .getStringExtra(ParseKeys.OBJECT_ID_STRING)).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mAnswerArray = new String[4];
        mAnswerArray = mGameObject.getList(ParseKeys.CODE_KEY).toArray(mAnswerArray);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mButtonList = new ArrayList<Button>();
        mInputList = new ArrayList<Button>();
        mTextLabels = new ArrayList<TextView>();
        mTextSpots = new ArrayList<TextView>();
        mTextNums = new ArrayList<TextView>();

        try {
            mGameType = mGameObject.getParseObject("GameType").fetchIfNeeded().getString("type");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (mGameType.equals("WhoFirst")) {
            ParseQuery vsQuery = ParseQuery.getQuery("VsGame");
            vsQuery.whereEqualTo()

        }

    }

    public void queryParse() {

        mGamesQuery = ParseQuery.getQuery("Guess");
        mGamesQuery.whereEqualTo(ParseKeys.GAME_KEY, mGameObject);

        if (mGameType.equals(ParseKeys.GAME_TYPE_WHO_FIRST_STRING)
                || mGameType.equals(ParseKeys.GAME_TYPE_SINGLE_STRING)) {
            mGamesQuery.whereEqualTo(ParseKeys.PLAYER_KEY, mCurrentUser);
            //TODO popup with guess numbers
        }
        mGamesQuery.orderByAscending(ParseKeys.ROUND_KEY);


        try {
            ArrayList<ParseObject> guessList = (ArrayList<ParseObject>) mGamesQuery.find();

            for (int i = 0; i < guessList.size(); i++) {
                Log.d("listTest", "queryParse");
                String[] tempGuess = new String[4];
                tempGuess = guessList.get(i).getList(ParseKeys.GUESS_KEY).toArray(tempGuess);
                mButtonList.get(i * 4).setText(tempGuess[0]);
                mButtonList.get(i * 4 + 1).setText(tempGuess[1]);
                mButtonList.get(i * 4 + 2).setText(tempGuess[2]);
                mButtonList.get(i * 4 + 3).setText(tempGuess[3]);

                mButtonList.get(i * 4).getBackground().setColorFilter(
                        getResources().getColor(R.color.button_white),
                        PorterDuff.Mode.LIGHTEN);
                mButtonList.get(i * 4 + 1).getBackground().setColorFilter(
                        getResources().getColor(R.color.button_white),
                        PorterDuff.Mode.LIGHTEN);
                mButtonList.get(i * 4 + 2).getBackground().setColorFilter(
                        getResources().getColor(R.color.button_white),
                        PorterDuff.Mode.LIGHTEN);
                mButtonList.get(i * 4 + 3).getBackground().setColorFilter(
                        getResources().getColor(R.color.button_white),
                        PorterDuff.Mode.LIGHTEN);
                mTextNums.get(i).setText("" +
                        guessList.get(i).getInt(ParseKeys.CORRECT_NUM_KEY));
                mTextSpots.get(i).setText("" +
                        guessList.get(i).getInt(ParseKeys.CORRECT_SPOT_KEY));

                if (i == guessList.size() - 1) {
                    mCurrentNumCorrectSpot = guessList.get(i).getInt(ParseKeys.ROUND_KEY);
                    mCurrentButtonInt = i * 4 + 4;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public void addToList() {
        mButtonList.add((Button) findViewById(R.id.button_1_1));
        mButtonList.add((Button) findViewById(R.id.button_1_2));
        mButtonList.add((Button) findViewById(R.id.button_1_3));
        mButtonList.add((Button) findViewById(R.id.button_1_4));
        mButtonList.add((Button) findViewById(R.id.button_2_1));
        mButtonList.add((Button) findViewById(R.id.button_2_2));
        mButtonList.add((Button) findViewById(R.id.button_2_3));
        mButtonList.add((Button) findViewById(R.id.button_2_4));
        mButtonList.add((Button) findViewById(R.id.button_3_1));
        mButtonList.add((Button) findViewById(R.id.button_3_2));
        mButtonList.add((Button) findViewById(R.id.button_3_3));
        mButtonList.add((Button) findViewById(R.id.button_3_4));
        mButtonList.add((Button) findViewById(R.id.button_4_1));
        mButtonList.add((Button) findViewById(R.id.button_4_2));
        mButtonList.add((Button) findViewById(R.id.button_4_3));
        mButtonList.add((Button) findViewById(R.id.button_4_4));
        mButtonList.add((Button) findViewById(R.id.button_5_1));
        mButtonList.add((Button) findViewById(R.id.button_5_2));
        mButtonList.add((Button) findViewById(R.id.button_5_3));
        mButtonList.add((Button) findViewById(R.id.button_5_4));
        mButtonList.add((Button) findViewById(R.id.button_6_1));
        mButtonList.add((Button) findViewById(R.id.button_6_2));
        mButtonList.add((Button) findViewById(R.id.button_6_3));
        mButtonList.add((Button) findViewById(R.id.button_6_4));
        mButtonList.add((Button) findViewById(R.id.button_7_1));
        mButtonList.add((Button) findViewById(R.id.button_7_2));
        mButtonList.add((Button) findViewById(R.id.button_7_3));
        mButtonList.add((Button) findViewById(R.id.button_7_4));
        mButtonList.add((Button) findViewById(R.id.button_8_1));
        mButtonList.add((Button) findViewById(R.id.button_8_2));
        mButtonList.add((Button) findViewById(R.id.button_8_3));
        mButtonList.add((Button) findViewById(R.id.button_8_4));
        mButtonList.add((Button) findViewById(R.id.button_9_1));
        mButtonList.add((Button) findViewById(R.id.button_9_2));
        mButtonList.add((Button) findViewById(R.id.button_9_3));
        mButtonList.add((Button) findViewById(R.id.button_9_4));
        mButtonList.add((Button) findViewById(R.id.button_10_1));
        mButtonList.add((Button) findViewById(R.id.button_10_2));
        mButtonList.add((Button) findViewById(R.id.button_10_3));
        mButtonList.add((Button) findViewById(R.id.button_10_4));

        mInputList.add((Button) findViewById(R.id.input_1));
        mInputList.add((Button) findViewById(R.id.input_2));
        mInputList.add((Button) findViewById(R.id.input_3));
        mInputList.add((Button) findViewById(R.id.input_4));
        mInputList.add((Button) findViewById(R.id.input_5));
        mInputList.add((Button) findViewById(R.id.input_6));
        mInputList.add((Button) findViewById(R.id.input_7));
        mInputList.add((Button) findViewById(R.id.input_8));
        mInputList.add((Button) findViewById(R.id.input_9));
        mInputList.add((Button) findViewById(R.id.input_10));

        mTextLabels.add((TextView) findViewById(R.id.one));
        mTextLabels.add((TextView) findViewById(R.id.two));
        mTextLabels.add((TextView) findViewById(R.id.three));
        mTextLabels.add((TextView) findViewById(R.id.four));
        mTextLabels.add((TextView) findViewById(R.id.five));
        mTextLabels.add((TextView) findViewById(R.id.six));
        mTextLabels.add((TextView) findViewById(R.id.seven));
        mTextLabels.add((TextView) findViewById(R.id.eight));
        mTextLabels.add((TextView) findViewById(R.id.nine));
        mTextLabels.add((TextView) findViewById(R.id.ten));

        mTextSpots.add((TextView) findViewById(R.id.text_1_2));
        mTextSpots.add((TextView) findViewById(R.id.text_2_2));
        mTextSpots.add((TextView) findViewById(R.id.text_3_2));
        mTextSpots.add((TextView) findViewById(R.id.text_4_2));
        mTextSpots.add((TextView) findViewById(R.id.text_5_2));
        mTextSpots.add((TextView) findViewById(R.id.text_6_2));
        mTextSpots.add((TextView) findViewById(R.id.text_7_2));
        mTextSpots.add((TextView) findViewById(R.id.text_8_2));
        mTextSpots.add((TextView) findViewById(R.id.text_9_2));
        mTextSpots.add((TextView) findViewById(R.id.text_10_2));

        mTextNums.add((TextView) findViewById(R.id.text_1_1));
        mTextNums.add((TextView) findViewById(R.id.text_2_1));
        mTextNums.add((TextView) findViewById(R.id.text_3_1));
        mTextNums.add((TextView) findViewById(R.id.text_4_1));
        mTextNums.add((TextView) findViewById(R.id.text_5_1));
        mTextNums.add((TextView) findViewById(R.id.text_6_1));
        mTextNums.add((TextView) findViewById(R.id.text_7_1));
        mTextNums.add((TextView) findViewById(R.id.text_8_1));
        mTextNums.add((TextView) findViewById(R.id.text_9_1));
        mTextNums.add((TextView) findViewById(R.id.text_10_1));
    }

    public void setDefaults() {
        mPreviousButtonInt = -1;
        for (Button b : mButtonList) {
            b.getBackground().setColorFilter(getResources().getColor(R.color.dark_purple),
                    PorterDuff.Mode.DARKEN);
            b.setTypeface(mFont);
            b.setTextColor(getResources().getColor(R.color.light_purple));
            b.setClickable(false);
        }
        for (Button b : mInputList) {
            b.getBackground().setColorFilter(getResources().getColor(R.color.button_white),
                    PorterDuff.Mode.DARKEN);
            b.setTypeface(mFont);
            b.setTextSize(18);
            b.setTextColor(getResources().getColor(R.color.light_purple));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (((Button) v).getText().toString().equals("0") &&
                            mCurrentButtonInt % 4 != 0) {
                        Log.d("backtest", "test");
                        mPreviousButtonInt--;
                        mCurrentButtonInt--;
                        mButtonList.get(mCurrentButtonInt).getBackground().setColorFilter(
                                getResources().getColor(R.color.dark_purple),
                                PorterDuff.Mode.DARKEN);
                        mButtonList.get(mCurrentButtonInt).setClickable(true);
                    }*/
                    if (mPreviousButtonInt % 4 == 3 && !isChecked) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                GameActivity.this);
                        builder.setMessage("Check Row First!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        builder.setTitle("Stop!");
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        isChecked = false;
                        mInput = Integer.parseInt(((Button) v).getText().toString());
                        mButtonList.get(mCurrentButtonInt).setText("" + mInput);
                        mButtonList.get(mCurrentButtonInt).getBackground().setColorFilter(
                                getResources().getColor(R.color.button_white),
                                PorterDuff.Mode.LIGHTEN);
                        mCurrentButtonInt++;
                        mPreviousButtonInt++;

                        //TODO set color disabled
                        v.setClickable(false);
                    }

                }
            });

        }

        for (TextView t : mTextLabels) {
            t.setTypeface(mFont);
        }

        mCheck = (Button) findViewById(R.id.enter);
        mCheck.setTypeface(mFont);

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviousButtonInt % 4 == 3 && !isChecked) {
                    String[] tempArray = new String[]
                            {
                                    mButtonList.get(mCurrentButtonInt - 4).getText().toString(),
                                    mButtonList.get(mCurrentButtonInt - 3).getText().toString(),
                                    mButtonList.get(mCurrentButtonInt - 2).getText().toString(),
                                    mButtonList.get(mCurrentButtonInt - 1).getText().toString(),
                            };
                    if (tempArray[0].equals(mAnswerArray[0])) {
                        mNumCorrectSpot++;
                    }
                    if (tempArray[1].equals(mAnswerArray[1])) {
                        mNumCorrectSpot++;
                    }
                    if (tempArray[2].equals(mAnswerArray[2])) {
                        mNumCorrectSpot++;
                    }
                    if (tempArray[3].equals(mAnswerArray[3])) {
                        mNumCorrectSpot++;
                    }
                    mTextSpots.get(mCurrentNumCorrectSpot).setText("" + mNumCorrectSpot);

                    for (int i = 0; i < tempArray.length; i++) {
                        if (tempArray[0].equals(mAnswerArray[i])) {
                            mNumCorrect++;
                        }
                        if (tempArray[1].equals(mAnswerArray[i])) {
                            mNumCorrect++;
                        }
                        if (tempArray[2].equals(mAnswerArray[i])) {
                            mNumCorrect++;
                        }
                        if (tempArray[3].equals(mAnswerArray[i])) {
                            mNumCorrect++;
                        }
                    }

                    mTextNums.get(mCurrentNumCorrectSpot).setText("" + mNumCorrect);

                    if (mNumCorrectSpot == 4) {
                        mGameObject.addAll(ParseKeys.WINNERS_KEY,
                                Arrays.asList(new String[]{
                                        ParseUser.getCurrentUser().getObjectId()}));
                        ParseUser.getCurrentUser().increment(ParseKeys.TOTAL_WINS_KEY);
                        try {
                            mGameObject.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                GameActivity.this);
                        builder.setMessage("You have won")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        endGame();
                                    }
                                });
                        builder.setTitle("Winner!");
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    Log.d("testLog", "testLog");
                    ParseObject guess = new ParseObject("Guess");
                    guess.put(ParseKeys.CORRECT_NUM_KEY, mNumCorrect);
                    guess.put(ParseKeys.CORRECT_SPOT_KEY, mNumCorrectSpot);
                    guess.addAll(ParseKeys.GUESS_KEY, Arrays.asList(tempArray));
                    guess.put(ParseKeys.ROUND_KEY, mCurrentNumCorrectSpot + 1);
                    guess.put(ParseKeys.GAME_KEY, mGameObject);
                    guess.put(ParseKeys.PLAYER_KEY, ParseUser.getCurrentUser());
                    guess.saveInBackground();
                    mNumCorrectSpot = 0;
                    mNumCorrect = 0;
                    mCurrentNumCorrectSpot++;
                    isChecked = true;
                    for (Button b : mInputList) {

                        //TODO set color enabled
                        b.setClickable(true);
                    }
                    if (mCurrentNumCorrectSpot == 10) {
                        ParseUser.getCurrentUser().increment(ParseKeys.TOTAL_LOSSES_KEY);
                        String code = "";
                        for (String s : mAnswerArray) {
                            code += s;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                GameActivity.this);
                        builder.setMessage("The Correct code was " + code)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        endGame();
                                    }
                                });
                        builder.setTitle("Game Over");
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            }
        });
    }

    public void endGame() {
        //TODO do shit with game data
        mGameObject.put(ParseKeys.IS_OVER_KEY, true);
        try {
            mGameObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void localTextViews() {
        TextView round = (TextView) findViewById(R.id.round);
        TextView guess = (TextView) findViewById(R.id.guess);
        TextView correct = (TextView) findViewById(R.id.correct);
        TextView correctSpot = (TextView) findViewById(R.id.correct_spot);
        round.setTypeface(mFont);
        guess.setTypeface(mFont);
        correct.setTypeface(mFont);
        correctSpot.setTypeface(mFont);

    }

    @Override
    protected void onStop() {
        super.onStop();
        int indexInArray = 0;
        String[] list = mGameObject.getList(ParseKeys.USERS_TURN_KEY)
                .toArray(new String[0]);
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(ParseUser.getCurrentUser().getObjectId())) {
                indexInArray = i;
            }
        }


        Integer[] guessesRem = mGameObject.getList(ParseKeys.GUESSES_REMAINING)
                .toArray(new Integer[0]);
        guessesRem[indexInArray] = 10 - mCurrentNumCorrectSpot;

        mGameObject.put(ParseKeys.GUESSES_REMAINING, Arrays.asList(guessesRem));
        try {
            mGameObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
