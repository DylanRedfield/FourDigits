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

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private ParseObject mVsGame;
    private HashMap<String, Object> params;
    private HashMap<String, Object> params2;

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
            mGameObject = ParseObject.createWithoutData(Keys.GAME_KEY, getIntent()
                    .getStringExtra(Keys.OBJECT_ID_STRING)).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mAnswerArray = new String[4];
        mAnswerArray = mGameObject.getList(Keys.CODE_KEY).toArray(mAnswerArray);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mButtonList = new ArrayList<Button>();
        mInputList = new ArrayList<Button>();
        mTextLabels = new ArrayList<TextView>();
        mTextSpots = new ArrayList<TextView>();
        mTextNums = new ArrayList<TextView>();

        try {
            mGameType = mGameObject.getParseObject(Keys.GAME_TYPE_KEY)
                    .fetchIfNeeded().getString("type");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
            ParseQuery vsQuery = ParseQuery.getQuery("VsGame");
            vsQuery.whereEqualTo(Keys.GAME_KEY, mGameObject);
            vsQuery.whereEqualTo(Keys.PLAYER_KEY, mCurrentUser);

            try {
                mVsGame = vsQuery.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        params = new HashMap<String, Object>();
        params2 = new HashMap<String, Object>();
    }

    public void queryParse() {

        mGamesQuery = ParseQuery.getQuery("Guess");
        mGamesQuery.whereEqualTo(Keys.GAME_KEY, mGameObject);

        if (mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)
                || mGameType.equals(Keys.GAME_TYPE_SINGLE_STRING)) {
            mGamesQuery.whereEqualTo(Keys.PLAYER_KEY, mCurrentUser);
            //TODO popup with guess numbers
        }
        mGamesQuery.orderByAscending(Keys.ROUND_KEY);


        try {
            ArrayList<ParseObject> guessList = (ArrayList<ParseObject>) mGamesQuery.find();

            for (int i = 0; i < guessList.size(); i++) {
                String[] tempGuess = new String[4];
                tempGuess = guessList.get(i).getList(Keys.GUESS_KEY).toArray(tempGuess);
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
                        guessList.get(i).getInt(Keys.CORRECT_NUM_KEY));
                mTextSpots.get(i).setText("" +
                        guessList.get(i).getInt(Keys.CORRECT_SPOT_KEY));

                if (i == guessList.size() - 1) {
                    mCurrentNumCorrectSpot = guessList.get(i).getInt(Keys.ROUND_KEY);
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
                    inputOnClick(v);
                }
            });

        }
        mInputList.get(9).setClickable(false);

        for (TextView t : mTextLabels) {
            t.setTypeface(mFont);
        }

        mCheck = (Button) findViewById(R.id.enter);
        mCheck.setTypeface(mFont);

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOnClick();

            }
        });
    }

    public void inputOnClick(View v) {
        if (((Button) v).getText().toString().equals("0")) {
            deleteInput();
        } else if (mPreviousButtonInt % 4 == 3 && !isChecked) {
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
            mInputList.get(9).setClickable(true);
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

    public void enableAll() {
        for (Button b : mInputList) {
            b.setClickable(true);
        }
        mInputList.get(9).setClickable(false);
        isChecked = true;
    }

    public void disableAll() {
        for (Button b : mInputList) {
            b.setClickable(false);
        }
        mInputList.get(9).setClickable(true);
    }

    //TODO check, enter, goback, input

    public void deleteInput() {
        mCurrentButtonInt--;
        mPreviousButtonInt--;
        mInputList.get(Integer.parseInt(mButtonList.get(mCurrentButtonInt).getText()
                .toString()) - 1).setClickable(true);
        mButtonList.get(mCurrentButtonInt).setText("");
        mButtonList.get(mCurrentButtonInt).getBackground().setColorFilter(
                getResources().getColor(R.color.dark_purple), PorterDuff.Mode.DARKEN);
        if (mCurrentButtonInt % 4 == 0) {
            mInputList.get(9).setClickable(false);
        }

    }

    public void checkOnClick() {
        try {
            mGameObject.fetch();
            if (mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                mVsGame.fetch();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (mGameType.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
            Log.d("gameType", mGameType);
            if (mPreviousButtonInt % 4 == 3 && !isChecked) {
                mInputList.get(9).setClickable(false);
                mGameObject.getRelation(Keys.WHOSE_TURN_KEY).remove(mCurrentUser);
                List tempArray = mGameObject.getList(Keys.USERS_TURN_KEY);

                for (int i = 0; i < tempArray.size(); i++) {
                    if (tempArray.get(i).equals(mCurrentUser.getObjectId())) {
                        tempArray.remove(i);
                    }
                }

                mGameObject.put(Keys.USERS_TURN_KEY, tempArray);
                try {
                    mGameObject.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //TODO push notification idk for what

                ParseObject guess = new ParseObject(Keys.COLLAB_GUESS);
                guess.put(Keys.PLAYER_KEY, mCurrentUser);
                guess.addAll(Keys.GUESS_KEY, Arrays.asList(makeGuessArray()));
                guess.put(Keys.GAME_KEY, mGameObject);
                guess.put(Keys.ROUND_KEY, mCurrentNumCorrectSpot + 1);
                try {
                    guess.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                collabCloudCode();
            }


        } else {
            if (mPreviousButtonInt % 4 == 3 && !isChecked) {
                String[] tempArray = incrementPoints();

                if (mNumCorrectSpot == 4) {
                    ifWinGame();

                } else if (mCurrentNumCorrectSpot < 9
                        && mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                    if (mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                        ifDidNotLose();
                    }
                } else if (mCurrentNumCorrectSpot >= 9 &&
                        mGameType.equals(Keys.GAME_TYPE_SINGLE_STRING)) {
                    ParseUser.getCurrentUser().increment(Keys.TOTAL_LOSSES_KEY);
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

                ParseObject guess = new ParseObject("Guess");
                guess.put(Keys.CORRECT_NUM_KEY, mNumCorrect);
                guess.put(Keys.CORRECT_SPOT_KEY, mNumCorrectSpot);
                guess.addAll(Keys.GUESS_KEY, Arrays.asList(tempArray));
                guess.put(Keys.ROUND_KEY, mCurrentNumCorrectSpot + 1);
                guess.put(Keys.GAME_KEY, mGameObject);
                guess.put(Keys.PLAYER_KEY, ParseUser.getCurrentUser());
                guess.saveInBackground();
                if (mGameType.equals(Keys.GAME_TYPE_SINGLE_STRING)) {
                    mGameObject.put(Keys.GUESSES_REMAINING,
                            Arrays.asList(new Integer[]{9 - mCurrentNumCorrectSpot}));
                    try {
                        mGameObject.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                mNumCorrectSpot = 0;
                mNumCorrect = 0;
                mCurrentNumCorrectSpot++;
                isChecked = true;

                enableAll();

            }
        }

    }

    public String[] incrementPoints() {
        String[] tempArray = makeGuessArray();

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
        if (!mGameType.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
            mTextSpots.get(mCurrentNumCorrectSpot).setText("" + mNumCorrectSpot);
        }

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

        if (!mGameType.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
            mTextNums.get(mCurrentNumCorrectSpot).setText("" + mNumCorrect);
        }
        return tempArray;
    }

    public String[] makeGuessArray() {
        String[] tempArray = new String[]
                {
                        mButtonList.get(mCurrentButtonInt - 4).getText().toString(),
                        mButtonList.get(mCurrentButtonInt - 3).getText().toString(),
                        mButtonList.get(mCurrentButtonInt - 2).getText().toString(),
                        mButtonList.get(mCurrentButtonInt - 1).getText().toString(),
                };
        return tempArray;
    }

    public void collabCloudCode() {
        final HashMap<String, Object> collabParams = new HashMap<>();
        collabParams.put("gameId", mGameObject.getObjectId());
        collabParams.put("roundNum", mCurrentNumCorrectSpot + 1);
        collabParams.put("numPlayers", mGameObject.getInt("numPlayers"));
        ParseCloud.callFunctionInBackground("hasEveryoneGuessedC", collabParams,
                new FunctionCallback<Object>() {
                    @Override
                    public void done(Object o, ParseException e) {
                        if ((Boolean) o) {
                            HashMap<String, Object> rankGuessesParams =
                                    new HashMap<String, Object>();
                            rankGuessesParams.put("gameId", mGameObject.getObjectId());
                            rankGuessesParams.put("roundNum", mCurrentNumCorrectSpot + 1);

                            ParseCloud.callFunctionInBackground("rankGuesses", rankGuessesParams,
                                    new FunctionCallback<ArrayList>() {
                                        @Override
                                        public void done(ArrayList o, ParseException e) {
                                            if ((Boolean) o.get(0)) {
                                                if (mGameObject.getList("winners").size() > 0) {
                                                    AlertDialog.Builder builder =
                                                            new AlertDialog.Builder(
                                                                    GameActivity.this);
                                                    builder.setMessage("You Won")
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK",
                                                                    new DialogInterface.
                                                                            OnClickListener() {
                                                                        public void onClick
                                                                                (DialogInterface
                                                                                         dialog,
                                                                                 int id) {
                                                                            finish();
                                                                        }
                                                                    });
                                                    builder.setTitle("Nice!");
                                                    AlertDialog alert = builder.create();
                                                    alert.show();
                                                } else {
                                                    AlertDialog.Builder builder =
                                                            new AlertDialog.Builder(
                                                                    GameActivity.this);
                                                    builder.setMessage("You lost")
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK",
                                                                    new DialogInterface.
                                                                            OnClickListener() {
                                                                        public void onClick
                                                                                (DialogInterface
                                                                                         dialog,
                                                                                 int id) {
                                                                            finish();
                                                                        }
                                                                    });
                                                    builder.setTitle("Game over");
                                                    AlertDialog alert = builder.create();
                                                    alert.show();
                                                }
                                                List guessRem = mGameObject
                                                        .getList(Keys.GUESSES_REMAINING);
                                                guessRem.set(0, 10 - mCurrentNumCorrectSpot);
                                                mGameObject.put(Keys.GUESSES_REMAINING,
                                                        guessRem);
                                                mGameObject.put(Keys.IS_OVER_KEY, true);
                                                try {
                                                    mGameObject.save();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                                //TODO reloadui
                                            } else {
                                                List guessRem = mGameObject
                                                        .getList(Keys.GUESSES_REMAINING);
                                                guessRem.set(0, 9 - mCurrentNumCorrectSpot);
                                                Log.d("GuessEnter", "log");
                                                mGameObject.put(Keys.GUESSES_REMAINING,
                                                        guessRem);
                                                try {
                                                    mGameObject.save();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }

                                                String player = (String) o.get(1);
                                                collabAlertDialog(player);
                                                updateUiCollab();
                                                //TODO update ui
                                                //TODO allow user to enter new guess

                                            }
                                        }
                                    });
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    GameActivity.this);
                            builder.setMessage("Other players need to submit their guess")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick
                                                        (DialogInterface dialog, int id) {
                                                    finish();
                                                }
                                            });
                            builder.setTitle("Whoops!");
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                    }
                });
    }

    public void collabAlertDialog(String x) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                GameActivity.this);
        builder.setMessage(x + "'s guess was selected")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setTitle("Guess chosen");
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateUiCollab() {
        queryParse();
        enableAll();
    }

    public void ifWinGame() {
        for (Button b : mInputList) {
            b.setClickable(false);
        }
        ParseRelation relation = mGameObject.getRelation("whoseTurn");
        relation.remove(mCurrentUser);

        ArrayList<String> list = (ArrayList<String>)
                mGameObject.get(Keys.USERS_TURN_KEY);
        ArrayList<Integer> guessesLeft = (ArrayList<Integer>)
                mGameObject.get(Keys.GUESSES_REMAINING);

        int index = 0;

        for (int i = 0; i < list.size(); i++) {
            if (mCurrentUser.getObjectId().equals(list.get(i))) {
                index = i;
                list.remove(i);
                guessesLeft.remove(i);
            }
        }

        mGameObject.put(Keys.USERS_TURN_KEY, Arrays.asList(list.toArray()));
        mGameObject.put(Keys.GUESSES_REMAINING,
                Arrays.asList(guessesLeft.toArray()));
        try {
            mGameObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
            whoFirstEndGame();
        }

        if (mGameType.equals(Keys.GAME_TYPE_SINGLE_STRING)) {
            mGameObject.addAll(Keys.WINNERS_KEY,
                    Arrays.asList(new String[]{
                            ParseUser.getCurrentUser().getObjectId()}));
            ParseUser.getCurrentUser().increment(Keys.TOTAL_WINS_KEY);
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
    }

    public void ifDidNotLose() {
        try {
            mVsGame.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mVsGame.put(Keys.GUESSES_REMAINING, 9 - mCurrentNumCorrectSpot);
        ParseRelation relation = mGameObject.getRelation("whoseTurn");
        relation.remove(mCurrentUser);

        ArrayList<String> list = (ArrayList<String>)
                mGameObject.get(Keys.USERS_TURN_KEY);
        ArrayList<Integer> guessesLeft = (ArrayList<Integer>)
                mGameObject.get(Keys.GUESSES_REMAINING);

        int index = 0;

        for (int i = 0; i < list.size(); i++) {
            if (mCurrentUser.getObjectId().equals(list.get(i))) {
                index = i;
                guessesLeft.set(i, 9 - mCurrentNumCorrectSpot);
            }
        }

        mGameObject.put(Keys.USERS_TURN_KEY,
                Arrays.asList(list.toArray()));
        mGameObject.put(Keys.GUESSES_REMAINING,
                Arrays.asList(guessesLeft.toArray()));
        try {
            mGameObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void whoFirstEndGame() {
        try {
            mVsGame.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mVsGame.put(Keys.IS_OVER_KEY, true);
        mVsGame.put(Keys.GUESSES_REMAINING, 10 - mCurrentNumCorrectSpot);
        try {
            mVsGame.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        params.put("gameId", mGameObject.getObjectId());

        ParseCloud.callFunctionInBackground
                ("isGameOver", params, new FunctionCallback<Boolean>() {
                    @Override
                    public void done(Boolean b, ParseException e) {
                        if (e == null) {

                            if (b.booleanValue()) {
                                mGameObject.put(Keys.IS_OVER_KEY, true);
                                try {
                                    mGameObject.save();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                checkWinnerFirst();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        GameActivity.this);
                                builder
                                        .setMessage("Waiting for other players" +
                                                "You will be notified when the game is over")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.
                                                OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                            }
                                        });
                                builder.setTitle("Correct!");
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        } else {

                            Log.d("addFriend", "failed");
                        }
                    }
                });

    }

    public void checkWinnerFirst() {
        ParseCloud.callFunctionInBackground("checkWinnerVs", params,
                new FunctionCallback<ArrayList<ParseObject>>() {
                    @Override
                    public void done(ArrayList<ParseObject> a,
                                     ParseException e) {
                        if (a.size() > 1) {
                            ArrayList<String> temp =
                                    new ArrayList<>();
                            for (ParseObject p : a) {
                                ParseUser user = (ParseUser) p.
                                        get(Keys.PLAYER_KEY);
                                temp.add(user.getObjectId());

                            }


                            params2.put("winnerIds", temp);
                            params2.put("gameId", mGameObject.getObjectId());

                            ParseCloud.callFunctionInBackground("editPlayerTies", params2,
                                    new FunctionCallback<Object>() {
                                        @Override
                                        public void done(Object o, ParseException e) {

                                        }
                                    });
                            for (ParseObject p : a) {
                                //TODO push notification
                            }
                        } else {
                            ParseObject winningGuess = a.get(0);
                            ParseUser user = (ParseUser) winningGuess.get(Keys.PLAYER_KEY);

                            if (user.getObjectId().equals(mCurrentUser.getObjectId())) {
                                final HashMap<String, String> params2
                                        = new HashMap<String, String>();
                                params2.put("winnerId", user.getObjectId());
                                params2.put("gameId", mGameObject.getObjectId());

                                ParseCloud.callFunctionInBackground("editPlayerWins", params2,
                                        new FunctionCallback<Object>() {
                                            @Override
                                            public void done(Object o, ParseException e) {

                                            }
                                        });
                                //TODO update ui with who won
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        GameActivity.this);
                                builder.setMessage("You won!")
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick
                                                            (DialogInterface dialog, int id) {
                                                        //do things
                                                    }
                                                });
                                builder.setTitle("Correct!");
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                final HashMap<String, String> params2
                                        = new HashMap<String, String>();
                                params2.put("winnerId", user.getObjectId());
                                params2.put("gameId", mGameObject.getObjectId());

                                ParseCloud.callFunctionInBackground("editPlayerWins", params2,
                                        new FunctionCallback<Object>() {
                                            @Override
                                            public void done(Object o, ParseException e) {
                                                //TODO send push fucking notifacton
                                                //TODO update the shitty fucking ui

                                            }
                                        });
                            }
                        }
                    }
                });
    }


    public void endGame() {
        mGameObject.put(Keys.IS_OVER_KEY, true);
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

    }
}
