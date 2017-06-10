package io.picopalette.apps.event_me.Utils;

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
