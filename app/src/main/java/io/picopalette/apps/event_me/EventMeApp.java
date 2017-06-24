package io.picopalette.apps.event_me;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ramkumar on 24/06/17.
 */

public class EventMeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
