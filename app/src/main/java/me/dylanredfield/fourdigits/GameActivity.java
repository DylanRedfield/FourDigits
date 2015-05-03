package me.dylanredfield.fourdigits;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends ActionBarActivity {
    private ArrayList<Button> mButtonList;
    private ArrayList<Button> mInputList;
    private ArrayList<TextView> mTextLabels;
    private Typeface mFont;
    private Button mCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");
        mButtonList = new ArrayList<Button>();
        mInputList = new ArrayList<Button>();
        mTextLabels = new ArrayList<TextView>();
        addToList();

        for (Button b : mButtonList) {
            b.getBackground().setColorFilter(getResources().getColor(R.color.dark_purple),
                    PorterDuff.Mode.DARKEN);
            b.setTypeface(mFont);
        }
        for (Button b : mInputList) {
             b.getBackground().setColorFilter(getResources().getColor(R.color.button_white),
                     PorterDuff.Mode.DARKEN);
            b.setTypeface(mFont);
            b.setTextSize(18);
        }
        for(TextView t : mTextLabels) {
            t.setTypeface(mFont);
        }
        mCheck = (Button) findViewById(R.id.enter);
        mCheck.setTypeface(mFont);
        localTextViews();


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
    }
    public void localTextViews() {
        TextView round = (TextView)findViewById(R.id.round);
        TextView guess = (TextView)findViewById(R.id.guess);
        TextView correct = (TextView)findViewById(R.id.correct);
        TextView correctSpot = (TextView)findViewById(R.id.correct_spot);
        round.setTypeface(mFont);
        guess.setTypeface(mFont);
        correct.setTypeface(mFont);
        correctSpot.setTypeface(mFont);

    }

}
