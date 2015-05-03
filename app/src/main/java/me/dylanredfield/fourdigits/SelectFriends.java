package me.dylanredfield.fourdigits;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SelectFriends extends ActionBarActivity {
    private Typeface mFont;
    private ListView mFriendsList;
    private ArrayList<String> mTestList;
    private FriendsAdapter mFriendsAdapter;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);
        if(mContext == null) {
            mContext = this;
        }
        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mTestList = new ArrayList<>();
        mTestList.add("fuck");
        mTestList.add("shit");
        mTestList.add("cunt");
        mFriendsList = (ListView) findViewById(R.id.list);
        mFriendsAdapter = new FriendsAdapter(mTestList);
        mFriendsList.setAdapter(mFriendsAdapter);

    }

    public class FriendsAdapter extends BaseAdapter {
        private ImageView thumbnail;
        private TextView name;
        private Button action;
        private TextView invitedText;
        private ArrayList testList;
        private Bitmap bitmapDraw;
        private TextView seperator;

        public FriendsAdapter(ArrayList list) {
            testList = list;

        }

        @Override
        public int getCount() {
            return mTestList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTestList.get(position);
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
            seperator = (TextView)convertView.findViewById(R.id.seperator);
            seperator.setTypeface(mFont);
            bitmapDraw = BitmapFactory.decodeResource(getResources(), R.drawable.sil);
            action.setTypeface(mFont);
            name.setTypeface(mFont);
            thumbnail.setImageBitmap(ImageHelper
                    .getRoundedCornerBitmap(bitmapDraw, Color.WHITE, 10, 5, mContext));
            if(position == 0 && seperator != null) {
                seperator.setVisibility(View.VISIBLE);
                seperator.setText("Favorites");
            } else if(position == 2 && seperator != null) {
                seperator.setVisibility(View.VISIBLE);
                seperator.setText("Friends");
            } else {
                seperator.setVisibility(View.GONE);
            }


            return convertView;
        }
    }
}
