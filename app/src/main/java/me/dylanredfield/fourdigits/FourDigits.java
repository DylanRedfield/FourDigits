package me.dylanredfield.fourdigits;

import android.app.Application;

import com.parse.Parse;

public class FourDigits extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        // Parse.enableLocalDatastore(this);

        Parse.initialize(this, "slSYDPn6CjqfZbOkxJE70yTTJgRFTvHPMTLaLZNQ"
                , "rUTgHY1epDlfO6FmN4Sdo89H7COf4DmXcsTZhm9u");

    }
}
