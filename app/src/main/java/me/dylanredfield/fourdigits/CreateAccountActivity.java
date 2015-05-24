package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;

public class CreateAccountActivity extends ActionBarActivity {

    private EditText mFirstName;
    private EditText mUserName;
    private EditText mPassWord;
    private Button mRegister;
    private TextView mAlreadyHave;
    private ParseUser mUser;
    private Typeface mFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        makeObjects();
        setListeners();

    }

    public void makeObjects() {
        mUser = ParseUser.getCurrentUser();
        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mFirstName = (EditText) findViewById(R.id.first_name);
        mPassWord = (EditText) findViewById(R.id.password);
        mUserName = (EditText) findViewById(R.id.user_name);
        mRegister = (Button) findViewById(R.id.register);
        mAlreadyHave = (TextView) findViewById(R.id.already_text);

        defualtViewValues();
    }

    public void defualtViewValues() {
        mRegister.setTypeface(mFont);
        mAlreadyHave.setTypeface(mFont);

        mFirstName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mUserName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void setListeners() {
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidInputs();
            }
        });
    }

    public void checkValidInputs() {
        if (!mFirstName.getText().toString().equals("")) {

            if (!mFirstName.getText().toString().trim().contains(" ")) {

                if (!mUserName.getText().toString().equals("")) {

                    if (!mUserName.getText().toString().trim().contains(" ")) {
                        if (mUserName.getText().toString().trim().length() < 16) {
                            if (!mPassWord.getText().toString().equals("")) {
                                makeUser();
                            } else {
                                makeError("Enter a Password", "password cannot be blank");
                            }
                        } else {
                            makeError("Invalid username",
                                    "username must be less than 16 characters");
                        }
                    } else {
                        makeError("Invalid username", "username must be one word");
                    }
                } else {
                    makeError("Enter a username", "username cannot be blank");
                }
            } else {
                makeError("Invalid Name", "Name must be one word");
            }
        } else {
            makeError("Enter a name", "Name cannot be blank");
        }
    }

    public void makeUser() {
        mUser.setUsername(mUserName.getText().toString().trim().toLowerCase());
        mUser.setPassword(mPassWord.getText().toString().trim());
        mUser.put(Keys.FIRST_NAME_KEY, mFirstName.getText().toString().trim());

        try {
            mUser.signUp();
            finish();
        } catch (ParseException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    CreateAccountActivity.this);
            builder.setMessage(e.getMessage())
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            builder.setTitle("Whoops");
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void makeError(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CreateAccountActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        builder.setTitle(title);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
