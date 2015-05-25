package me.dylanredfield.fourdigits;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class SignInActivity extends ActionBarActivity {

    private EditText mUserName;
    private EditText mPassWord;
    private Button mRegister;
    private TextView mAlreadyHave;
    private ParseUser mUser;
    private Typeface mFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_sign_in);

        makeObjects();
        setListeners();

    }

    public void makeObjects() {
        mUser = ParseUser.getCurrentUser();
        mFont = Typeface.createFromAsset(getAssets(), "Arista_2.ttf");

        mPassWord = (EditText) findViewById(R.id.password);
        mUserName = (EditText) findViewById(R.id.user_name);
        mRegister = (Button) findViewById(R.id.register);
        mAlreadyHave = (TextView) findViewById(R.id.already_text);

        defualtViewValues();
    }

    public void defualtViewValues() {
        mRegister.setTypeface(mFont);
        mAlreadyHave.setTypeface(mFont);


        mUserName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void setListeners() {
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidInputs();
            }
        });
        mAlreadyHave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void checkValidInputs() {


        if (!mUserName.getText().toString().equals("")) {

            if (!mUserName.getText().toString().trim().contains(" ")) {

                if (!mPassWord.getText().toString().equals("")) {
                    logIn();
                } else {
                    makeError("Enter a Password", "password cannot be blank");
                }
            } else {
                makeError("Invalid username", "username must be one word");
            }
        } else {
            makeError("Enter a username", "username cannot be blank");
        }
    }


    public void logIn() {
        try {
            mUser.logIn(mUserName.getText().toString().trim(),
                    mPassWord.getText().toString().trim());
            finish();
        } catch (ParseException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    SignInActivity.this);
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
                SignInActivity.this);
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
