package me.dylanredfield.fourdigits;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseUser;

public class FourDigits extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        // Parse.enableLocalDatastore(this);

        Parse.initialize(this, "M6Be96TbN60EnXUdJL84jGiSuTeDd1yKNOfXfxtE"
                , "KscwophmWj7qgjwBd4BTrFUULsTOQxMNeK0u7ExI");

        ParseUser.enableAutomaticUser();
    }
}
