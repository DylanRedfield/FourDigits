package me.dylanredfield.fourdigits;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

public class SelectGameActivity extends ActionBarActivity {
    private TextView mCollaborativeText;
    private Button mFriendsButton;
    private TextView mVsText;
    private Button mComputer;
    private Button mSeparate;
    private Typeface mFont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mCollaborativeText = (TextView) findViewById(R.id.collaborative_text);
        mFriendsButton = (Button) findViewById(R.id.friends_button);
        mVsText = (TextView) findViewById(R.id.vs_text);
        mComputer = (Button) findViewById(R.id.computer_button);
        mSeparate = (Button) findViewById(R.id.separate_button);

        mCollaborativeText.setTypeface(mFont);
        mFriendsButton.setTypeface(mFont);
        mComputer.setTypeface(mFont);
        mVsText.setTypeface(mFont);
        mSeparate.setTypeface(mFont);

    }

}
