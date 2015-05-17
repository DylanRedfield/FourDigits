package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
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

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {


    private ParseUser mCurrentUser;
    private ListView mListView;
    private GamesAdapter mAdapter;
    private ParseQuery<ParseObject> mGamesQuery;
    private ArrayList<ParseObject> mGameList;
    private ArrayList<ParseObject> mTheirTurnList;
    private ArrayList<ParseObject> mGameOverList;
    private ArrayList<ParseObject> mYourTurnList;
    private ArrayList<ParseObject> mInvites;
    private Typeface mFont;
    private TextView mUsernameText;
    private Button mNewGame;
    private ArrayList<ParseObject> mFullList;
    private TextView mRecord;
    private TextView mEmptyText;
    private ParseObject mSelectedObject;

    private boolean firstQuery = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        mInvites = new ArrayList<>();

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

        try {
            mCurrentUser = ParseUser.getCurrentUser().fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setUserInfo();

    }

    public void setUserInfo() {
        if (mCurrentUser != null) {
            if (ParseAnonymousUtils.isLinked(mCurrentUser)) {
                mUsernameText.setText("Not Logged In");
            } else {
                mUsernameText.setText(mCurrentUser.getUsername());
            }

            mRecord.setText("" + mCurrentUser.getInt(Keys.TOTAL_WINS_KEY) + "-" +
                    mCurrentUser.getInt(Keys.TOTAL_LOSSES_KEY));
        }
    }

    public void queryParse() {

        mGamesQuery = ParseQuery.getQuery(Keys.GAME_KEY);
        mGamesQuery.whereEqualTo(Keys.PLAYERS_KEY, mCurrentUser);
        mGamesQuery.include(Keys.GAME_TYPE_KEY);
        mGamesQuery.orderByDescending(Keys.UPDATED_AT_KEY);

        if (mCurrentUser != null) {
            mGamesQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (firstQuery) {
                        mGameOverList.clear();
                        mTheirTurnList.clear();
                        mYourTurnList.clear();
                        mGameList.clear();
                        firstQuery = false;
                        for (ParseObject p : parseObjects) {
                            mGameList.add(p);
                        }

                        for (int i = 0; i < mGameList.size(); i++) {

                            if (mGameList.get(i).getBoolean(Keys.IS_OVER_KEY)
                                    && mGameOverList.size() < 10) {
                                mGameOverList.add(mGameList.get(i));
                            } else {
                                String[] tempArray = new String[1];
                                tempArray = mGameList.get(i)
                                        .getList(Keys.USERS_TURN_KEY).toArray(tempArray);
                                if (contains(tempArray)) {
                                    mYourTurnList.add(mGameList.get(i));
                                } else {
                                    mTheirTurnList.add(mGameList.get(i));
                                }

                            }
                        }
                    }

                    queryParseForInvites();

                }

            });
        }


    }

    public boolean contains(String[] tempArray) {
        for (String s : tempArray) {
            if (s != null && s.equals(mCurrentUser.getObjectId())) {
                return true;
            }
        }
        return false;
    }

    public void queryParseForInvites() {
        ParseQuery<ParseObject> inviteQuery = ParseQuery.getQuery(Keys.INVITE_KEY);
        inviteQuery.whereEqualTo(Keys.INVITED_USERS_KEY, mCurrentUser);
        inviteQuery.whereNotEqualTo(Keys.ACCEPTED_USERS_KEY, mCurrentUser);
        inviteQuery.include(Keys.GAME_TYPE_KEY);
        inviteQuery.orderByDescending(Keys.UPDATED_AT_KEY);
        inviteQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                mInvites.clear();

                for (ParseObject p : parseObjects) {
                    mInvites.add(p);
                }
                mAdapter = new GamesAdapter();
                mListView.setAdapter(mAdapter);
            }
        });
    }

    public void setListeners() {
        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        MenuItem signUp = menu.findItem(R.id.sign_up);
        MenuItem signIn = menu.findItem(R.id.sign_in);
        MenuItem addFriend = menu.findItem(R.id.add_friend);
        MenuItem logOut = menu.findItem(R.id.log_out);

        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            signUp.setVisible(true);
            signIn.setVisible(true);
            addFriend.setVisible(false);
            logOut.setVisible(false);
        } else {
            signUp.setVisible(false);
            signIn.setVisible(false);
            addFriend.setVisible(true);
            logOut.setVisible(true);
        }
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
            case R.id.log_out:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFriend() {
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
                userQuery.whereEqualTo(Keys.USERNAME_KEY, inputUsername);

                try {
                    if (userQuery.count() > 0 && !(userQuery.getFirst().getObjectId()
                            .equals(mCurrentUser.getObjectId()))) {
                        ParseRelation myFriends = ParseUser.getCurrentUser()
                                .getRelation(Keys.FRIENDS_KEY);
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
                                                    (Keys.NUM_FRIENDS_KEY);
                                            mCurrentUser.saveInBackground();

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
        try {
            mCurrentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setUserInfo();
        queryParse();
    }

    public class GamesAdapter extends BaseAdapter {
        private TextView separator;
        private TextView name;
        private TextView gameType;
        private Button action;
        private int indexInArray;
        private TextView info;

        public GamesAdapter() {
            createFullList();
        }

        public void createFullList() {
            mFullList = new ArrayList<ParseObject>();

            for (ParseObject p : mInvites) {
                mFullList.add(p);
            }
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

            if (mInvites.size() > 0 && position == 0) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("Invites");
            } else if (mYourTurnList.size() > 0 && position == mInvites.size()) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("Your Turn");
            } else if (mTheirTurnList.size() > 0 && position ==
                    mInvites.size() + mYourTurnList.size()) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("Their Turn");
            } else if (mGameOverList.size() > 0 && position == mInvites.size() +
                    mYourTurnList.size() + mTheirTurnList.size()) {
                separator.setVisibility(View.VISIBLE);
                separator.setText("Game Over");
            } else {
                separator.setVisibility(View.GONE);
            }

            if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                name.setText("You");
            } else {
                name.setText(mFullList.get(position).getString(Keys.PLAYER_STRINGS_KEY));
            }
            String type;

            type = mFullList.get(position).getParseObject(Keys.GAME_TYPE_KEY)
                    .getString("type");

            if (type.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                type = "vs";
            } else if (type.equals(Keys.GAME_TYPE_SINGLE_STRING)) {
                type = "single";
            } else {
                type = "collaborative";
            }
            gameType.setText(type);


            if (position < mInvites.size()) {
                action.setText("accept");
                action.getBackground().setColorFilter(getResources().getColor(R.color.orange),
                        PorterDuff.Mode.DARKEN);

                action.setVisibility(View.VISIBLE);
            } else if (position < mInvites.size() + mYourTurnList.size()) {
                String[] list = mFullList.get(position).getList(Keys.USERS_TURN_KEY)
                        .toArray(new String[0]);

                for (int i = 0; i < list.length; i++) {
                    if (list[i].equals(mCurrentUser.getObjectId())) {
                        indexInArray = i;
                    }
                }
                action.setText("Play");
                info.setText("" + mFullList.get(position).getList("guessesRemaining")
                        .toArray(new Integer[0])[indexInArray] + " left");

                action.setVisibility(View.VISIBLE);
                action.getBackground().setColorFilter(getResources()
                        .getColor(R.color.button_white), PorterDuff.Mode.SRC_OVER);
            } else if (position < mInvites.size() + mYourTurnList.size()
                    + mTheirTurnList.size()) {
                action.setText("nudge");
                action.getBackground().setColorFilter(getResources().getColor(R.color.orange),
                        PorterDuff.Mode.SRC_OVER);

                action.setVisibility(View.VISIBLE);
            } else if (position < mInvites.size() + mYourTurnList.size() + mTheirTurnList.size()
                    + mGameOverList.size()) {

                action.setVisibility(View.GONE);
                if (mFullList.get(position).getList(Keys.WINNERS_KEY) != null) {
                    String[] winnerList = mFullList.get(position).getList(Keys.WINNERS_KEY)
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


                /*RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) info.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                info.setLayoutParams(params); //causes layout update */
            }

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((Button) v).getText().toString().equals("accept")) {


                        callCloudCode(position);

                    } else {
                        Intent i = new Intent(getApplicationContext(), GameActivity.class);
                        i.putExtra(Keys.OBJECT_ID_STRING,
                                mFullList.get(position).getObjectId());
                        startActivity(i);
                    }
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((Button) v).getText().toString().equals("accept")) {
                        HashMap<String, Object> params = new HashMap<String, Object>();

                        params.put("inviteId", mFullList.get(position).getObjectId());
                        params.put("gameType", mFullList.get(position)
                                .get(Keys.GAME_TYPE_KEY));

                        ParseCloud.callFunctionInBackground
                                ("canWeStart", params, new FunctionCallback<Object[]>() {

                                    @Override
                                    public void done(Object[] objects, ParseException e) {
                                        boolean canStart = (Boolean) objects[0];

                                        if (canStart) {
                                            //queryParse();
                                            mSelectedObject = (ParseObject) objects[1];
                                            Intent i = new Intent(getApplicationContext(),
                                                    GameActivity.class);
                                            i.putExtra(Keys.OBJECT_ID_STRING,
                                                    mSelectedObject.getObjectId());
                                            startActivity(i);
                                        }
                                    }
                                });
                    } else {

                        Intent i = new Intent(getApplicationContext(), GameActivity.class);
                        i.putExtra(Keys.OBJECT_ID_STRING, mFullList.get(position)
                                .getObjectId());
                        startActivity(i);
                    }
                }
            });


            return convertView;
        }

    }

    public void callCloudCode(int position) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("inviteId", mFullList.get(position).getObjectId());
        params.put("gameType", ((ParseObject) mFullList.get(position)
                .get(Keys.GAME_TYPE_KEY)).getString("type"));
        ParseCloud.callFunctionInBackground
                ("canWeStart", params, new FunctionCallback<ArrayList>() {

                    @Override
                    public void done(ArrayList objects, ParseException e) {
                        if (e == null) {
                            boolean canStart;
                            canStart = (Boolean) objects.get(0);

                            if (canStart) {
                                //queryParse();
                                mSelectedObject = (ParseObject) objects.get(1);
                                Intent i = new Intent(getApplicationContext(),
                                        GameActivity.class);
                                i.putExtra(Keys.OBJECT_ID_STRING,
                                        mSelectedObject.getObjectId());
                                startActivity(i);
                            } else {
                                queryParse();
                                AlertDialog.Builder builder = new
                                        AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Other Players need to accept")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new
                                                DialogInterface.OnClickListener() {
                                                    public void onClick
                                                            (DialogInterface dialog, int id) {
                                                        //do things
                                                    }
                                                });
                                builder.setTitle("Accepted!");
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
