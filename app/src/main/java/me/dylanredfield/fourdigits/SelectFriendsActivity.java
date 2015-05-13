package me.dylanredfield.fourdigits;

import android.content.Context;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;

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
        //TODO make gameobject using static method
        setContentView(R.layout.activity_select_friends);
        if (mContext == null) {
            mContext = this;
        }

        mGameTypeString = getIntent().getStringExtra(ParseKeys.GAME_TYPE_EXTRA);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mCurrentUser = ParseUser.getCurrentUser();

        mFriendsQuery = mCurrentUser.getRelation(ParseKeys.FRIENDS_KEY).getQuery();
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
                if (mGameTypeString.equals("WhoFirst")) {
                    if (mInvitedList.size() > 0 && mInvitedList.size() < 7) {
                        //TODO make uppercase
                        String[] answerArr = SelectGameActivity.makeAnswer();

                        ParseObject inviteObject = new ParseObject("Invite");
                        inviteObject.put("fromUser", mCurrentUser);
                        ParseRelation acceptedRelation =
                                inviteObject.getRelation("acceptedUsers");
                        ParseRelation invtedRelation =
                                inviteObject.getRelation("invitedUsers");

                        ArrayList<String> userIDs = new ArrayList<String>();

                        for (ParseObject p : mInvitedList) {
                            invtedRelation.add(p);
                            userIDs.add(p.getObjectId());
                            mPlayerStrings.add(p.getString("username"));
                        }
                        acceptedRelation.add(mCurrentUser);
                        userIDs.add(mCurrentUser.getObjectId());
                        mPlayerStrings.add(mCurrentUser.getUsername());
                        ParseObject gameTypeObject =
                                ParseObject.createWithoutData
                                        ("GameType", "mSymctKi0s");
                        inviteObject.put("GameType", gameTypeObject);

                        inviteObject.put("toUsers", userIDs);
                        String str = "";
                        inviteObject.put("playerStrings", );

                    }
                }

                finish();
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
                    //TODO makeInvit
                    action.getBackground().setColorFilter(R.color.orange, PorterDuff.Mode.DARKEN);
                    action.setText("V");
                    Log.d("onClickListen", "button hit");

                    mInvitedList.add(mFriendsArrayList.get(position));
                }
            });

            return convertView;
        }
    }
}
