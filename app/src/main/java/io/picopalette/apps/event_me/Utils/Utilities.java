package io.picopalette.apps.event_me.Utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Models.Event;

/**
 * Created by ramkumar on 10/06/17.
 */

public class Utilities {

    public static String encodeEmail(String email) {
        if(email != null)
            return email.replace(".",Constants.dot);
        else
            return email;
    }

}
