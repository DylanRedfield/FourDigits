package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
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
    private Menu mMenu;
    private ImageView mThumbnail;
    private Context mContext;


    private boolean firstQuery = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("onCreate", "test");
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        makeObjects();

        parseStuff();
        setListeners();
        firstQuery = true;
        queryParse();

        if (mMenu != null) {
            mMenu.clear();
            onCreateOptionsMenu(mMenu);
        }
    }

    public void makeObjects() {

        if (mContext == null) {
            mContext = this;
        }
        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");
        try {
            mCurrentUser = ParseUser.getCurrentUser().fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        mThumbnail = (ImageView) findViewById(R.id.thumbnail);

        mEmptyText.setText("No games");
        mListView.setEmptyView(mEmptyText);

        mUsernameText.setTypeface(mFont);
        mNewGame.setTypeface(mFont);
        mEmptyText.setTypeface(mFont);

        mAdapter = new GamesAdapter();
        mListView.setAdapter(mAdapter);


    }

    public void parseStuff() {


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
        } else {
            mUsernameText.setText("Not Logged In");
        }
        final ParseFile parseFile = (ParseFile) mCurrentUser.get("profilePicture");
        if (parseFile != null) {
            parseFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        displayImage(parseFile, mThumbnail);
                    } else {
                        Bitmap bitmapDraw = BitmapFactory.decodeResource(getResources(), R.drawable.sil);
                        mThumbnail.setImageBitmap(ImageHelper
                                .getRoundedCornerBitmap(bitmapDraw, Color.WHITE, 10, 5, mContext));
                    }
                }
            });
        } else {
            Bitmap bitmapDraw = BitmapFactory.decodeResource(getResources(), R.drawable.sil);
            mThumbnail.setImageBitmap(ImageHelper
                    .getRoundedCornerBitmap(bitmapDraw, Color.WHITE, 10, 5, mContext));
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
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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

        mUsernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ParseAnonymousUtils.isLinked(mCurrentUser)) {
                    Intent i = new Intent(getApplicationContext(), ActivityProfile.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                    startActivity(i);
                }
            }
        });
        mThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ParseAnonymousUtils.isLinked(mCurrentUser)) {
                    Intent i = new Intent(getApplicationContext(), ActivityProfile.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                    startActivity(i);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem signUp = menu.findItem(R.id.sign_up);
        MenuItem signIn = menu.findItem(R.id.sign_in);
        MenuItem addFriend = menu.findItem(R.id.add_friend);
        MenuItem logOut = menu.findItem(R.id.log_out);
        MenuItem profile = menu.findItem(R.id.profile);

        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            signUp.setVisible(true);
            signIn.setVisible(true);
            addFriend.setVisible(false);
            logOut.setVisible(false);
            profile.setVisible(false);
        } else {
            signUp.setVisible(false);
            signIn.setVisible(false);
            addFriend.setVisible(true);
            logOut.setVisible(true);
            profile.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                makeObjects();

                parseStuff();
                setListeners();
                firstQuery = true;
                queryParse();

                if (mMenu != null) {
                    mMenu.clear();
                    onCreateOptionsMenu(mMenu);
                }
                return true;
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
            case R.id.sign_in:
                Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(signInIntent);
                return true;
            case R.id.profile:
                Intent a = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(a);
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

                                        } else {
                                            AlertDialog.Builder builder = new
                                                    AlertDialog.Builder(MainActivity.this);
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
                setUserInfo();

                firstQuery = true;
                queryParse();

                mMenu.clear();
                onCreateOptionsMenu(mMenu);
            }
        });

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
                type = "group";
            }
            gameType.setText(type);


            if (position < mInvites.size()) {
                action.setText("accept");
                action.getBackground().setColorFilter(getResources().getColor(R.color.orange),
                        PorterDuff.Mode.DARKEN);

                action.setVisibility(View.VISIBLE);
                info.setVisibility(View.GONE);
            } else if (position < mInvites.size() + mYourTurnList.size()) {
                String[] list = mFullList.get(position).getList(Keys.USERS_TURN_KEY)
                        .toArray(new String[0]);

                for (int i = 0; i < list.length; i++) {
                    if (list[i].equals(mCurrentUser.getObjectId())) {
                        indexInArray = i;
                    }
                    if (type.equals("group")) {
                        indexInArray = 0;
                    }
                }
                action.setText("Play");
                info.setText("" + mFullList.get(position).getList("guessesRemaining")
                        .toArray(new Integer[0])[indexInArray] + " left");
                info.setVisibility(View.VISIBLE);

                action.setVisibility(View.VISIBLE);
                action.getBackground().setColorFilter(getResources()
                        .getColor(R.color.button_white), PorterDuff.Mode.SRC_OVER);
            } else if (position < mInvites.size() + mYourTurnList.size()
                    + mTheirTurnList.size()) {
                action.setText("nudge");
                action.getBackground().setColorFilter(getResources().getColor(R.color.orange),
                        PorterDuff.Mode.SRC_OVER);

                action.setVisibility(View.VISIBLE);

                info.setVisibility(View.GONE);
            } else if (position < mInvites.size() + mYourTurnList.size() + mTheirTurnList.size()
                    + mGameOverList.size()) {

                action.setVisibility(View.VISIBLE);
                action.setText("results");
                action.getBackground().setColorFilter(getResources()
                        .getColor(R.color.button_white), PorterDuff.Mode.LIGHTEN);

                info.setVisibility(View.VISIBLE);
                String typerino = mFullList.get(position).getParseObject(Keys.GAME_TYPE_KEY)
                        .getString("type");
                if (typerino.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
                    if (mFullList.get(position).getList(Keys.WINNERS_KEY).size() > 0) {
                        info.setText("Won");
                    } else {
                        info.setText("Lost");
                    }
                } else {
                    if (mFullList.get(position).getList(Keys.WINNERS_KEY) != null) {
                        String[] winnerList = mFullList.get(position).getList(Keys.WINNERS_KEY)
                                .toArray(new String[0]);
                        for (int i = 0; i < winnerList.length; i++) {
                            if (mCurrentUser.getObjectId().equals(winnerList[i])) {
                                if (winnerList.length > 1) {
                                    info.setText("Tie");
                                } else {
                                    info.setText("Won");
                                }
                            }
                        }
                        if (!info.getText().toString().equals("Won") &&
                                !info.getText().toString().equals("Tie")) {
                            info.setText("Lost");
                        }
                    } else {
                        info.setText("Lost");
                    }
                }


            }

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((Button) v).getText().toString().equals("accept")) {
                        callCloudCode(position);
                    } else if (((Button) v).getText().toString().equals("results")) {
                        Intent i = new Intent(getApplicationContext(), ResultsActivity.class);
                        i.putExtra(Keys.OBJECT_ID_STRING, mFullList.get(position).getObjectId());
                        startActivity(i);
                    } else if (((Button) v).getText().toString().equals("nudge")) {
                        ParseRelation players = mFullList.get(position).getRelation("players");
                        ParseQuery playerQuery = players.getQuery();
                        ParseQuery instQuery = ParseInstallation.getQuery();
                        instQuery.whereMatchesQuery("user", playerQuery);
                        instQuery.whereNotEqualTo("user", mCurrentUser);
                        String input = "You were just nudged in your game with " +
                                mFullList.get(position).getString(Keys.PLAYER_STRINGS_KEY);

                        JSONObject data = null;
                        try {
                            data = new JSONObject("{\"alert\": \"" + input + "\"" +
                                    ",\"badge\": \"Increment\",\"pushType\": \"Nudge\"" +
                                    ",\"gameId\": \"" +
                                    mFullList.get(position).getObjectId() + "\"}");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ParsePush push = new ParsePush();
                        push.setData(data);
                        push.setQuery(instQuery);
                        push.sendInBackground();


                        //TODO Get to the end who first loss

                    } else {
                        Intent i = new Intent(getApplicationContext(), GameActivity.class);
                        i.putExtra(Keys.OBJECT_ID_STRING, mFullList.get(position).getObjectId());
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

    private void displayImage(ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {

                @Override
                public void done(byte[] data, ParseException e) {

                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
                                data.length);

                        if (bmp != null) {
                            mThumbnail.setImageBitmap(ImageHelper
                                    .getRoundedCornerBitmap(bmp, Color.WHITE, 10, 5, mContext));
                        }
                    } else {
                        Log.e("paser after downloade", " null");
                    }

                }
            });
        } else {

            Log.e("parse file", " null");


        }

    }

}
