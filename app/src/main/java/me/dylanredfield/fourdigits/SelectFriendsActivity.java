package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectFriendsActivity extends ActionBarActivity {
    private Typeface mFont;
    private ListView mFriendsList;
    private FriendsAdapter mFriendsAdapter;
    private Context mContext;
    private ParseUser mCurrentUser;
    private ParseQuery mFriendsQuery;
    private ArrayList<ParseObject> mFriendsArrayList;
    private ArrayList<ParseObject> mInvitedList;
    private String mGameTypeString;
    private ArrayList<String> mPlayerStrings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);
        if (mContext == null) {
            mContext = this;
        }

        mGameTypeString = getIntent().getStringExtra(Keys.GAME_TYPE_EXTRA);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mCurrentUser = ParseUser.getCurrentUser();

        mFriendsQuery = mCurrentUser.getRelation(Keys.FRIENDS_KEY).getQuery();
        mFriendsQuery.orderByAscending("username");

        try {
            mFriendsArrayList = (ArrayList<ParseObject>) mFriendsQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mFriendsList = (ListView) findViewById(R.id.list);
        mFriendsAdapter = new FriendsAdapter();
        mFriendsList.setAdapter(mFriendsAdapter);

        mInvitedList = new ArrayList<>();
        mPlayerStrings = new ArrayList<String>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_friends_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.complete:
                if (mGameTypeString.equals(Keys.GAME_TYPE_WHO_FIRST_STRING)) {
                    if (mInvitedList.size() > 0 && mInvitedList.size() < 7) {
                        String[] answerArr = SelectGameActivity.makeAnswer();

                        ParseObject inviteObject = new ParseObject("Invite");
                        inviteObject.put(Keys.FROM_USER_KEY, mCurrentUser);
                        ParseRelation acceptedRelation =
                                inviteObject.getRelation(Keys.ACCEPTED_USERS_KEY);
                        ParseRelation invtedRelation =
                                inviteObject.getRelation(Keys.INVITED_USERS_KEY);

                        ArrayList<String> userIDs = new ArrayList<String>();

                        for (ParseObject p : mInvitedList) {
                            invtedRelation.add(p);
                            userIDs.add(p.getObjectId());
                            mPlayerStrings.add(p.getString(Keys.USERNAME_KEY));
                        }
                        acceptedRelation.add(mCurrentUser);
                        userIDs.add(mCurrentUser.getObjectId());
                        mPlayerStrings.add(mCurrentUser.getUsername());
                        ParseObject gameTypeObject =
                                ParseObject.createWithoutData
                                        (Keys.GAME_TYPE_KEY, Keys.GAME_TYPE_WHO_FIRST);
                        inviteObject.put(Keys.GAME_TYPE_KEY, gameTypeObject);

                        inviteObject.put(Keys.TO_USERS_KEY, userIDs);

                        String str = "";

                        for (int i = 0; i < mPlayerStrings.size(); i++) {
                            if (i < mPlayerStrings.size() - 1) {
                                str += mPlayerStrings.get(i) + ", ";
                            } else {
                                str += mPlayerStrings.get(i);
                            }
                        }

                        inviteObject.put(Keys.PLAYER_STRINGS_KEY, str);
                        inviteObject.put(Keys.CODE_KEY, Arrays.asList(answerArr));
                        inviteObject.put(Keys.NUM_SENT_KEY, userIDs.size());
                        inviteObject.put(Keys.NUM_ACCEPTED_KEY, 1);

                        inviteObject.put(Keys.ACC_ARRAY_KEY,
                                Arrays.asList(new String[]{mCurrentUser.getObjectId()}));
                        try {
                            inviteObject.save();
                            finish();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        ParseQuery pushQuery = ParseInstallation.getQuery();
                        pushQuery.whereContainedIn("user", mInvitedList);
                        String input = mCurrentUser.getUsername() + " has invited you to play " +
                                "a game";

                        JSONObject data = null;
                        try {
                            data = new JSONObject("{\"alert\": \"" + input + "\"" +
                                    ",\"badge\": \"Increment\",\"pushType\": \"Invite\"}");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ParsePush push = new ParsePush();
                        push.setData(data);
                        push.setQuery(pushQuery);
                        push.sendInBackground();


                    } else if (mInvitedList.size() >= 7) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SelectFriendsActivity.this);
                        builder.setMessage("You can only invite up to 6 friends!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        builder.setTitle("Whoops!");
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SelectFriendsActivity.this);
                        builder.setMessage("Need to invite atleast one friend!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        builder.setTitle("Whoops!");
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                if (mGameTypeString.equals(Keys.GAME_TYPE_COLLAB_STRING)) {
                    if (mInvitedList.size() > 1 && mInvitedList.size() < 7) {
                        String[] answerArr = SelectGameActivity.makeAnswer();

                        ParseObject inviteObject = new ParseObject("Invite");
                        inviteObject.put(Keys.FROM_USER_KEY, mCurrentUser);
                        ParseRelation acceptedRelation =
                                inviteObject.getRelation(Keys.ACCEPTED_USERS_KEY);
                        ParseRelation invtedRelation =
                                inviteObject.getRelation(Keys.INVITED_USERS_KEY);

                        ArrayList<String> userIDs = new ArrayList<String>();

                        for (ParseObject p : mInvitedList) {
                            invtedRelation.add(p);
                            userIDs.add(p.getObjectId());
                            mPlayerStrings.add(p.getString(Keys.USERNAME_KEY));
                        }
                        acceptedRelation.add(mCurrentUser);
                        userIDs.add(mCurrentUser.getObjectId());
                        mPlayerStrings.add(mCurrentUser.getUsername());
                        ParseObject gameTypeObject =
                                ParseObject.createWithoutData
                                        (Keys.GAME_TYPE_KEY, Keys.GAME_TYPE_COLLAB);
                        inviteObject.put(Keys.GAME_TYPE_KEY, gameTypeObject);

                        inviteObject.put(Keys.TO_USERS_KEY, userIDs);

                        String str = "";

                        for (int i = 0; i < mPlayerStrings.size(); i++) {
                            if (i < mPlayerStrings.size() - 1) {
                                str += mPlayerStrings.get(i) + ", ";
                            } else {
                                str += mPlayerStrings.get(i);
                            }
                        }

                        inviteObject.put(Keys.PLAYER_STRINGS_KEY, str);
                        inviteObject.put(Keys.CODE_KEY, Arrays.asList(answerArr));
                        inviteObject.put(Keys.NUM_SENT_KEY, userIDs.size());
                        inviteObject.put(Keys.NUM_ACCEPTED_KEY, 1);

                        inviteObject.put(Keys.ACC_ARRAY_KEY,
                                Arrays.asList(new String[]{mCurrentUser.getObjectId()}));
                        try {
                            inviteObject.save();
                            finish();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        ParseQuery pushQuery = ParseInstallation.getQuery();
                        pushQuery.whereContainedIn("user", mInvitedList);
                        String input = mCurrentUser.getUsername() + " has invited you to play " +
                                "a game";

                        JSONObject data = null;
                        try {
                            data = new JSONObject("{\"alert\": \"" + input + "\"" +
                                    ",\"badge\": \"Increment\",\"pushType\": \"Invite\"}");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ParsePush push = new ParsePush();
                        push.setData(data);
                        push.setQuery(pushQuery);
                        push.sendInBackground();
                    } else if (mInvitedList.size() >= 7) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SelectFriendsActivity.this);
                        builder.setMessage("You can only invite up to 6 friends!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        builder.setTitle("Whoops!");
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                SelectFriendsActivity.this);
                        builder.setMessage("Need to invite atleast two friends!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        builder.setTitle("Whoops!");
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class FriendsAdapter extends BaseAdapter {
        private ImageView thumbnail;
        private TextView name;
        private Button action;
        private TextView invitedText;
        private Bitmap bitmapDraw;

        public FriendsAdapter() {

        }

        @Override
        public int getCount() {
            return mFriendsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFriendsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.select_friends_row, null);
            }
            thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            name = (TextView) convertView.findViewById(R.id.name);
            action = (Button) convertView.findViewById(R.id.state_image);
            invitedText = (TextView) convertView.findViewById(R.id.invited_gone);
            bitmapDraw = BitmapFactory.decodeResource(getResources(), R.drawable.sil);
            action.setTypeface(mFont);
            name.setTypeface(mFont);
            thumbnail.setImageBitmap(ImageHelper
                    .getRoundedCornerBitmap(bitmapDraw, Color.WHITE, 10, 5, mContext));
            thumbnail.setVisibility(View.GONE);

            name.setText(mFriendsArrayList.get(position).getString("username"));

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((Button) v).getText().toString().equalsIgnoreCase("challenge")) {
                        ((Button) v).setText("Invited");
                        v.getBackground().setColorFilter(getResources()
                                .getColor(R.color.orange), PorterDuff.Mode.SRC_OVER);
                        mInvitedList.add(mFriendsArrayList.get(position));
                    } else {
                        ((Button) v).setText("Challenge");
                        Log.d("removeTest", "remove");
                        v.getBackground().setColorFilter(getResources()
                                .getColor(R.color.button_white), PorterDuff.Mode.SRC_OVER);
                        int index = mInvitedList.indexOf(mFriendsArrayList.get(position));
                        mInvitedList.remove(index);
                    }
                }
            });

            return convertView;
        }
    }
}
