package me.dylanredfield.fourdigits;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Activity mActivityContext;
    private RelativeLayout mLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getStuff();

        addToList();

        setDefaults();

        localTextViews();

        queryParse();

        if (mGameObject.getBoolean(Keys.IS_OVER_KEY)) {
            for (Button b : mInputList) {
                b.setClickable(false);
            }
        }
    }

    public void getStuff() {


        mCurrentUser = ParseUser.getCurrentUser();

        mActivityContext = this;
        while (mActivityContext.getParent() != null) {
            mActivityContext = mActivityContext.getParent();
        }

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
        ActionBar actionBar = getSupportActionBar();
        String type = "";
        if (mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
            type = "Vs";
        } else if (mGameType.equals(Keys.GAME_TYPE_SINGLE_STRING)) {
            type = "Single";
        } else {
            type = "Group";
        }
        actionBar.setTitle(type + " Game");

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

        mButtonList.get(0).getViewTreeObserver().addOnGlobalLayoutListener
                (new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        for (Button b : mButtonList) {
                            b.setHeight(mButtonList.get(0).getWidth());
                        }
                        for (Button b : mInputList) {
                            b.setHeight(mButtonList.get(0).getWidth());
                            b.setWidth(mButtonList.get(0).getWidth());
                        }

                    }
                });
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
        mLayout = (RelativeLayout) findViewById(R.id.layout);
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener
                (new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mCheck.setHeight(mLayout.getHeight());

                    }
                });
    }

    public void inputOnClick(View v) {
        if (((Button) v).getText().toString().equals("X")) {
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
            isChecked = true;
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

        //setUIToWait(true);
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
                ParseQuery pushQuery = ParseInstallation.getQuery();
                ParseQuery userQuery = mGameObject.getRelation(Keys.WHOSE_TURN_KEY).getQuery();
                pushQuery.whereMatchesQuery(Keys.USER_KEY, userQuery);
                String input = mCurrentUser.getUsername() + " just sumbitted a guess in your" +
                        " group game. Now it is your turn!";

                JSONObject data = null;
                try {
                    data = new JSONObject("{\"alert\": \"" + input + "\"" +
                            ",\"badge\": \"Increment\",\"pushType\": \"yourTurn\"" +
                            ",\"gameId\": \"" + mGameObject.getObjectId() + "\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ParsePush push = new ParsePush();
                push.setData(data);
                push.setQuery(pushQuery);
                push.sendInBackground();

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
                setUIToWait(false);

                if (mNumCorrectSpot == 4) {
                    ifWinGame();

                } else if (mCurrentNumCorrectSpot < 9
                        && mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                    ifDidNotLose();
                } else if (mCurrentNumCorrectSpot >= 9 &&
                        mGameType.equals(Keys.GAME_TYPE_SINGLE_STRING)) {

                    ParseUser.getCurrentUser().increment(Keys.TOTAL_LOSSES_KEY);
                    endGame("loss");
                } else if (mCurrentNumCorrectSpot >= 9 &&
                        mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                    mVsGame.put(Keys.IS_OVER_KEY, true);
                    mVsGame.put(Keys.GUESSES_REMAINING, 0);
                    mVsGame.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ParseRelation relation = mGameObject.getRelation(Keys.WHOSE_TURN_KEY);
                            relation.remove(mCurrentUser);

                            List list = mGameObject.getList(Keys.USERS_TURN_KEY);
                            List guessesRem = mGameObject.getList(Keys.GUESSES_REMAINING);

                            int index = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if ((list.get(i).equals(mCurrentUser.getObjectId()))) {
                                    index = i;
                                }
                            }

                            list.remove(index);
                            guessesRem.remove(index);

                            mGameObject.put(Keys.USERS_TURN_KEY, list);
                            mGameObject.put(Keys.GUESSES_REMAINING, guessesRem);
                            mGameObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    params.clear();
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
                                                                    .setMessage("Waiting for other players\n" +
                                                                            "You will be notified when the game is over")
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("OK", new DialogInterface.
                                                                            OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            finish();
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
                            });
                        }
                    });
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
                                                    viewResults();
                                                    finish();
                                                } else {
                                                    viewResults();
                                                    finish();
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

    public void viewResults() {
        Intent i = new Intent(getApplicationContext(), ResultsActivity.class);
        i.putExtra(Keys.OBJECT_ID_STRING, mGameObject.getObjectId());
        startActivity(i);
        finish();
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
            endGame("win");
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

        //setUIToWait(false);
    }

    public void whoFirstEndGame() {
        try {
            mVsGame.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mVsGame.put(Keys.IS_OVER_KEY, true);
        mVsGame.put(Keys.GUESSES_REMAINING, 9 - mCurrentNumCorrectSpot);
        try {
            mVsGame.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        params.clear();
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
                                        .setMessage("Waiting for other players\n" +
                                                "You will be notified when the game is over")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.
                                                OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                finish();
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
                    public void done(final ArrayList<ParseObject> a,
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
                                            for (ParseObject p : a) {
                                                ParseUser user = (ParseUser) p.get("player");
                                                if (user.getObjectId().equals(mCurrentUser
                                                        .getObjectId())) {
                                                    makeAlertTie();
                                                }
                                            }

                                        }
                                    });
                        } else {
                            ParseObject winningGuess = a.get(0);
                            final ParseUser user = (ParseUser) winningGuess.get(Keys.PLAYER_KEY);

                            if (user.getObjectId().equals(mCurrentUser.getObjectId())) {
                                final HashMap<String, String> params2
                                        = new HashMap<String, String>();
                                params2.put("winnerId", user.getObjectId());
                                params2.put("gameId", mGameObject.getObjectId());

                                ParseCloud.callFunctionInBackground("editPlayerWins", params2,
                                        new FunctionCallback<Object>() {
                                            @Override
                                            public void done(Object o, ParseException e) {
                                                viewResults();

                                            }
                                        });
                                viewResults();
                            } else {
                                final HashMap<String, String> params2
                                        = new HashMap<String, String>();
                                params2.put("winnerId", user.getObjectId());
                                params2.put("gameId", mGameObject.getObjectId());

                                ParseCloud.callFunctionInBackground("editPlayerWins", params2,
                                        new FunctionCallback<Object>() {
                                            @Override
                                            public void done(Object o, ParseException e) {
                                                //editPlayerWinsPush(user);
                                                viewResults();


                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void makeAlertTie() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(
                GameActivity.this);
        builder.setMessage("You guessed the number with the same ammount of " +
                "guesses as someone else!")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick
                                    (DialogInterface dialog, int id) {
                                viewResults();
                                finish();
                            }
                        });
        builder.setTitle("It's a tie!");
        AlertDialog alert = builder.create();
        alert.show();*/
        viewResults();
    }

    public void editPlayerWinsPush(ParseUser user) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo(Keys.USER_KEY, user);
        String input = "You guessed the number with the least amount of guesses in your game " +
                "with " + mGameObject.getString(Keys.PLAYER_STRINGS_KEY);

        JSONObject data = null;
        try {
            data = new JSONObject("{\"alert\": \"" + input + "\"" +
                    ",\"badge\": \"Increment\",\"pushType\": \"gameOver\"" +
                    ",\"gameId\": \"" + mGameObject.getObjectId() + "\"}");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParsePush push = new ParsePush();
        push.setData(data);
        push.setQuery(pushQuery);
        push.sendInBackground();
    }

    public void endGame(String result) {
        mGameObject.put(Keys.IS_OVER_KEY, true);
        try {
            mGameObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String code = "";
        for (String s : mAnswerArray) {
            code += s;
        }
        for (Button b : mInputList) {
            b.setClickable(false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(
                GameActivity.this);
        if (result.equals("loss")) {
            builder.setMessage("The Correct code was " + code)
                    .setTitle("Game Over");
        } else {
            builder.setMessage("Correct!")
                    .setTitle("You won!");
        }
        builder.setNegativeButton("Go Home", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNeutralButton("View board", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                for (Button b : mInputList) {
                    b.setClickable(false);
                }
            }
        });
        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SelectGameActivity.makeParseObject("Single", GameActivity.this, mActivityContext);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);

        if (mGameType != null && mGameType.equals(Keys.GAME_TYPE_SINGLE_STRING)) {
            return false;
        } else {
            return super.onCreateOptionsMenu(menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.game_info:
                if (mGameType.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
                    if (mGameObject.getBoolean(Keys.IS_OVER_KEY)) {

                        ParseRelation userRelation = mGameObject.getRelation(Keys.PLAYERS_KEY);
                        ParseQuery userQuery = userRelation.getQuery();
                        userQuery.findInBackground(new FindCallback() {
                            @Override
                            public void done(List list, ParseException e) {

                            }

                            @Override
                            public void done(Object o, Throwable throwable) {
                                ListView listView = new ListView(mActivityContext);
                                GameInfoAdapter adapter = new GameInfoAdapter
                                        ((List<ParseObject>) o);

                                listView.setAdapter(adapter);

                                final Dialog dialog = new Dialog(mActivityContext);
                                SpannableString str = new SpannableString("Players");
                                str.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 7
                                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dialog.setTitle(str);
                                dialog.setContentView(listView);
                                dialog.getWindow()
                                        .setBackgroundDrawableResource(R.color.light_purple);
                                dialog.show();
                            }
                        });
                    } else {
                        ParseQuery collabQuery = ParseQuery.getQuery("CollaborativeGuess");
                        collabQuery.whereEqualTo("Game", mGameObject);
                        collabQuery.whereEqualTo("round", mCurrentNumCorrectSpot + 1);
                        collabQuery.include("player");
                        collabQuery.orderByDescending("createdAt");
                        collabQuery.findInBackground(new FindCallback() {
                            @Override
                            public void done(List list, ParseException e) {

                            }

                            @Override
                            public void done(Object o, Throwable throwable) {
                                ListView listView = new ListView(mActivityContext);
                                GameInfoAdapter adapter = new GameInfoAdapter
                                        ((List<ParseObject>) o);

                                listView.setAdapter(adapter);

                                final Dialog dialog = new Dialog(mActivityContext);
                                SpannableString str = new SpannableString("Players");
                                str.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 7
                                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dialog.setTitle(str);
                                dialog.setContentView(listView);
                                dialog.getWindow()
                                        .setBackgroundDrawableResource(R.color.light_purple);
                                dialog.show();
                            }
                        });
                    }
                } else if (mGameType.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                    Log.d("queryTest", "test1");
                    if (mGameObject.getBoolean(Keys.IS_OVER_KEY)) {
                        ParseQuery gameQuery = ParseQuery.getQuery("VsGame");
                        gameQuery.whereEqualTo("Game", mGameObject);
                        gameQuery.include("player");
                        gameQuery.orderByDescending("createdAt");
                        gameQuery.findInBackground(new FindCallback() {
                            @Override
                            public void done(List list, ParseException e) {

                            }

                            @Override
                            public void done(Object o, Throwable throwable) {
                                ListView listView = new ListView(mActivityContext);
                                GameInfoAdapter adapter = new GameInfoAdapter
                                        ((List<ParseObject>) o);

                                listView.setAdapter(adapter);

                                final Dialog dialog = new Dialog(mActivityContext);
                                SpannableString str = new SpannableString("Players");
                                str.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 7
                                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dialog.setTitle(str);
                                dialog.setContentView(listView);
                                dialog.getWindow()
                                        .setBackgroundDrawableResource(R.color.light_purple);
                                dialog.show();
                            }
                        });
                    } else {
                        Log.d("queryTest", "test2");
                        ParseQuery gameQuery = ParseQuery.getQuery("VsGame");
                        gameQuery.whereEqualTo("Game", mGameObject);
                        gameQuery.include("player");
                        gameQuery.orderByDescending("createdAt");
                        gameQuery.findInBackground(new FindCallback() {
                            @Override
                            public void done(List list, ParseException e) {

                                Log.d("queryTest", "test3");
                            }

                            @Override
                            public void done(Object o, Throwable throwable) {
                                Log.d("queryTest", "test4");
                                ListView listView = new ListView(mActivityContext);
                                GameInfoAdapter adapter = new GameInfoAdapter
                                        ((List<ParseObject>) o);

                                listView.setAdapter(adapter);

                                final Dialog dialog = new Dialog(mActivityContext);
                                SpannableString str = new SpannableString("Players");
                                str.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 7
                                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dialog.setTitle(str);
                                dialog.getWindow()
                                        .setBackgroundDrawableResource(R.color.light_purple);
                                dialog.setContentView(listView);
                                dialog.show();
                            }
                        });
                    }
                }


            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void setUIToWait(boolean wait) {

        if (wait) {
            progressDialog = new ProgressDialog(GameActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Saving");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }

    }

    public class GameInfoAdapter extends BaseAdapter {
        private TextView mName;
        private TextView mInfo;
        private ArrayList<ParseObject> mList;

        public GameInfoAdapter(List list) {
            mList = (ArrayList<ParseObject>) list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.game_info_row, null);
            }
            mName = (TextView) convertView.findViewById(R.id.name);
            mInfo = (TextView) convertView.findViewById(R.id.info);


            if (mGameType.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
                if (mGameObject.getBoolean(Keys.IS_OVER_KEY)) {
                    mName.setVisibility(View.VISIBLE);
                    mName.setText(mList.get(position).getString(Keys.USERNAME_KEY));
                    mInfo.setVisibility(View.GONE);
                } else {
                    mName.setVisibility(View.VISIBLE);
                    mName.setText(((ParseObject) mList.get(position)
                            .get(Keys.PLAYER_KEY)).getString(Keys.USERNAME_KEY));
                    mInfo.setText(mList.get(position).getList(Keys.GUESS_KEY).toString());
                    mInfo.setVisibility(View.VISIBLE);
                }
            } else {
                if (mGameObject.getBoolean(Keys.IS_OVER_KEY)) {
                    mName.setVisibility(View.VISIBLE);
                    mName.setText(((ParseObject) mList.get(position)
                            .get(Keys.PLAYER_KEY)).getString(Keys.USERNAME_KEY));
                    mInfo.setVisibility(View.VISIBLE);
                    if (mList.get(position).getBoolean(Keys.IS_OVER_KEY)) {
                        mInfo.setText("Guessed in " + (10 - mList.get(position)
                                .getInt(Keys.GUESSES_REMAINING) + " guesses"));
                    } else {
                        mInfo.setText(mList.get(position).getInt(Keys.GUESSES_REMAINING) +
                                " guesses remaining");
                    }
                } else {
                    mName.setVisibility(View.VISIBLE);
                    mName.setText(((ParseObject) mList.get(position)
                            .get(Keys.PLAYER_KEY)).getString(Keys.USERNAME_KEY));
                    mInfo.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        int indexInArray = 0;

    }

}
