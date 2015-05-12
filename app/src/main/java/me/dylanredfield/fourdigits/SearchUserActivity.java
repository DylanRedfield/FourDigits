package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

public class SearchUserActivity extends ActionBarActivity {

    private Button mAddFriend;
    private ListView mFriendsList;
    private FriendListAdapter mAdapter;
    private ArrayList<ParseUser> mUserList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        if(mContext == null) {
            mContext = this;
        }

        mUserList = new ArrayList<ParseUser>();
        mAddFriend = (Button) findViewById(R.id.add_friend);
        mFriendsList = (ListView) findViewById(R.id.friends_list);
        mAdapter = new FriendListAdapter(mUserList);

        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                final EditText edittext= new EditText(mContext);
                alert.setMessage("Ensure the username is correct before adding");
                alert.setTitle("New Friend");

                alert.setView(edittext);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        String YouEditTextValue = edittext.getText().toString();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });
    }

    public class FriendListAdapter extends BaseAdapter {

        private ArrayList<ParseUser> mList;
        private TextView userName;

        public FriendListAdapter(ArrayList<ParseUser> list) {
            mList = list;

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
                convertView = getLayoutInflater().inflate(R.layout.friends_list_row, null);
            }

            userName = (TextView) convertView.findViewById(R.id.user_name);
            userName.setText(mList.get(position).getUsername());

            return convertView;
        }
    }
}
