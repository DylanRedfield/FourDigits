package me.dylanredfield.fourdigits;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO make gameobject using static method
        setContentView(R.layout.activity_select_friends);
        if (mContext == null) {
            mContext = this;
        }
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
                    action.getBackground().setColorFilter(R.color.orange, PorterDuff.Mode.LIGHTEN);
                    action.setText("V");
                }
            });

            return convertView;
        }
    }
}
