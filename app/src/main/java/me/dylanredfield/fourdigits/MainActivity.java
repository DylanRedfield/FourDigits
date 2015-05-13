package me.dylanredfield.fourdigits;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    private boolean isFirstTime;
    private ParseUser mCurrentUser;
    private ParseInstallation mInstallation;
    private ListView mListView;
    private GamesAdapter mAdapter;
    private ParseQuery<ParseObject> mGamesQuery;
    private ArrayList<ParseObject> mGameList;
    private ArrayList<ParseObject> mTheirTurnList;
    private ArrayList<ParseObject> mGameOverList;
    private ArrayList<ParseObject> mYourTurnList;
    private Typeface mFont;
    private TextView mUsernameText;
    private Button mNewGame;
    private ArrayList<ParseObject> mFullList;
    private TextView mRecord;
    private TextView mEmptyText;

    private boolean firstQuery = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResumeTest", "test");

        makeObjects();

        parseStuff();
        setListeners();
        firstQuery = true;
        queryParse();
    }

    public void makeObjects() {
        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mGameList = new ArrayList<>();
        mYourTurnList = new ArrayList<>();
        mTheirTurnList = new ArrayList<>();
        mGameOverList = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.list);
        mUsernameText = (TextView) findViewById(R.id.name);
        mRecord = (TextView) findViewById(R.id.record);
        mNewGame = (Button) findViewById(R.id.new_game);
        mEmptyText = (TextView) findViewById(R.id.empty_list);

        mEmptyText.setText("No games");
        mListView.setEmptyView(mEmptyText);

        mUsernameText.setTypeface(mFont);
        mNewGame.setTypeface(mFont);
        mEmptyText.setTypeface(mFont);

        mAdapter = new GamesAdapter();
        mListView.setAdapter(mAdapter);

    }

    public void parseStuff() {

        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser != null) {
            if (ParseAnonymousUtils.isLinked(mCurrentUser)) {
                mUsernameText.setText("Not Logged In");
            } else {
                mUsernameText.setText(mCurrentUser.getUsername());
            }

            //TODO record only works after winning a game
            mRecord.setText("" + mCurrentUser.getInt(ParseKeys.TOTAL_WINS_KEY) + "-" +
                    mCurrentUser.getInt(ParseKeys.TOTAL_LOSSES_KEY));
        }
    }

    public void queryParse() {

        Log.d("listTest", "queryParse");
        mGamesQuery = ParseQuery.getQuery("Game");
        mGamesQuery.whereEqualTo(ParseKeys.PLAYERS_KEY, mCurrentUser);
        mGamesQuery.include(ParseKeys.GAME_TYPE_KEY);
        mGamesQuery.orderByDescending("updatedAt");

        if (mCurrentUser != null) {
            mGamesQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    Log.d("listTest", "done");
                    if (firstQuery) {
                        mGameOverList.clear();
                        mTheirTurnList.clear();
                        mYourTurnList.clear();
                        mGameList.clear();
                        Log.d("listTest", "first");
                        firstQuery = false;
                        for (ParseObject p : parseObjects) {
                            mGameList.add(p);
                        }

                        for (int i = 0; i < mGameList.size(); i++) {

                            if (mGameList.get(i).getBoolean(ParseKeys.IS_OVER_KEY)
                                    && mGameOverList.size() < 10) {
                                mGameOverList.add(mGameList.get(i));
                            } else {
                                String[] tempArray = new String[1];
                                tempArray = mGameList.get(i)
                                        .getList(ParseKeys.USERS_TURN_KEY).toArray(tempArray);

                                for (String s : tempArray) {
                                    if (s.equals(mCurrentUser.getObjectId())) {
                                        mYourTurnList.add(mGameList.get(i));
                                    } else {
                                        if (mTheirTurnList.size() < 10) {
                                            mTheirTurnList.add(mGameList.get(i));
                                        }
                                    }
                                }
                            }
                        }

                        mAdapter = new GamesAdapter();
                        mListView.setAdapter(mAdapter);
                    }

                }
            });
        }


    }

    public void setListeners() {
        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("buttonTest", "shit");
                Intent i = new Intent(getApplicationContext(), SelectGameActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add_friend:
                addFriend();
                return true;
            case R.id.sign_up:
                Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFriend() {
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

            final EditText edittext = new EditText(MainActivity.this);
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
                    userQuery.whereEqualTo("username", inputUsername);

                    try {
                        if (userQuery.count() > 0 && !(userQuery.getFirst().getObjectId()
                                .equals(mCurrentUser.getObjectId()))) {
                            ParseRelation myFriends = ParseUser.getCurrentUser()
                                    .getRelation(ParseKeys.FRIENDS_KEY);
                            myFriends.add(userQuery.getFirst());
                            mCurrentUser.save();
                            HashMap<String, Object> params = new HashMap<String, Object>();

                            params.put("friendId", userQuery.getFirst().getObjectId());
                            params.put("myName", ParseUser.getCurrentUser().getUsername());

                            ParseCloud.callFunctionInBackground
                                    ("addFriend", params, new FunctionCallback<String>() {
                                        @Override
                                        public void done(String s, ParseException e) {
                                            if (e == null) {
                                                mCurrentUser.increment
                                                        (ParseKeys.NUM_FRIENDS_KEY);
                                                mCurrentUser.saveInBackground();
                                                Log.d("addFriend", "added");

                                            } else {

                                                Log.d("addFriend", "failed");
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
    }

    public class GamesAdapter extends BaseAdapter {
        private ArrayList<ParseObject> mTheirTurn;
        private ArrayList<ParseObject> mGameOver;
        private ArrayList<ParseObject> mYourTurn;

        private TextView separator;
        private TextView name;
        private TextView gameType;
        private Button action;
        private int indexInArray;
        private TextView info;

        public GamesAdapter(/*ArrayList<ParseObject> theirTurnList,
                            ArrayList<ParseObject> gameOverList,
                            ArrayList<ParseObject> yourTurnList*/) {
            /*mTheirTurn = theirTurnList;
            mGameOver = gameOverList;
            mYourTurn = yourTurnList;*/
            createFullList();
        }

        public void createFullList() {
            mFullList = new ArrayList<ParseObject>();

            for (ParseObject p : mYourTurnList) {
                mFullList.add(p);
            }
            for (ParseObject p : mTheirTurnList) {
                mFullList.add(p);
            }
            for (ParseObject p : mGameOverList) {
                mFullList.add(p);
            }

        }

        @Override
        public int getCount() {
            return mFullList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFullList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.games_row, null);
            }

            separator = (TextView) convertView.findViewById(R.id.separator);
            name = (TextView) convertView.findViewById(R.id.name);
            gameType = (TextView) convertView.findViewById(R.id.game_type);
            action = (Button) convertView.findViewById(R.id.action);
            info = (TextView) convertView.findViewById(R.id.info);

            separator.setTypeface(mFont);
            name.setTypeface(mFont);
            gameType.setTypeface(mFont);
            action.setTypeface(mFont);
            info.setTypeface(mFont);

            if (mYourTurnList.size() > 0 && position == 0) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("Your Turn");
                Log.d("listTest", "divider");
            } else if (mTheirTurnList.size() > 0 && position == mYourTurnList.size()) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("Their Turn");
            } else if (mGameOverList.size() > 0 && position == mYourTurnList.size()
                    + mTheirTurnList.size()) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("Game Over");
            } else {
                separator.setVisibility(View.GONE);
            }

            if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                name.setText("You");
            } else {
                name.setText(mFullList.get(position).getString(ParseKeys.PLAYER_STRINGS_KEY));
            }
            String type = "";

            type = mFullList.get(position).getParseObject(ParseKeys.GAME_TYPE_KEY)
                    .getString("type");

            if (type.equals("WhoFirst") || type.equals("Single")) {
                type = "vs";
            } else {
                type = "collaborative";
            }
            gameType.setText(type);
            String[] list = mFullList.get(position).getList(ParseKeys.USERS_TURN_KEY)
                    .toArray(new String[0]);

            for (int i = 0; i < list.length; i++) {
                if (list[i].equals(mCurrentUser.getObjectId())) {
                    indexInArray = i;
                }
            }
            info.setText("" + mFullList.get(position).getList("guessesRemaining")
                    .toArray(new Integer[1])[indexInArray].intValue() + " left");

            if (position < mYourTurnList.size()) {
                action.setText("Play");
            } else if (position < mYourTurnList.size() + mTheirTurnList.size()) {
                action.setVisibility(View.GONE);
            } else if (position < mYourTurnList.size() + mTheirTurnList.size()
                    + mGameOverList.size()) {
                action.setVisibility(View.GONE);

                if (mFullList.get(position).getList(ParseKeys.WINNERS_KEY) != null) {
                    String[] winnerList = mFullList.get(position).getList(ParseKeys.WINNERS_KEY)
                            .toArray(new String[0]);
                    for (int i = 0; i < winnerList.length; i++) {
                        if (mCurrentUser.getObjectId().equals(winnerList[i])) {
                            info.setText("You won");
                        }
                    }
                    if (!info.getText().toString().equals("You won")) {
                        info.setText("You lost");
                    }
                } else {

                    info.setText("You lost");
                }


                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) info.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                info.setLayoutParams(params); //causes layout update
            }

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), GameActivity.class);
                    i.putExtra(ParseKeys.OBJECT_ID_STRING, mFullList.get(position).getObjectId());
                    startActivity(i);
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), GameActivity.class);
                    i.putExtra(ParseKeys.OBJECT_ID_STRING, mFullList.get(position).getObjectId());
                    startActivity(i);
                }
            });


            return convertView;
        }
    }

}
