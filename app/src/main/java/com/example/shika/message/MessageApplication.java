package com.example.shika.message;

import android.app.Application;

import com.example.shika.message.ui.MainActivity;
import com.example.shika.message.utils.UserConstant;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by shika on 7/30/2015.
 */
public class MessageApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "2IagwXrQSmS28cI5dNRmzZl1kSGxoG4kbltMwMpx", "Qd9LwZ64pQrAFKMdzkCezPJ1x8hFcxaKPXTjFgiV");



        //PushService.setDefaultPushCallback(this, MainActivity.class);
        PushService.setDefaultPushCallback(this, MainActivity.class,
                R.drawable.ic_stat_ic_launcher);
        ParseInstallation.getCurrentInstallation().saveInBackground();




    }


    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(UserConstant.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}
