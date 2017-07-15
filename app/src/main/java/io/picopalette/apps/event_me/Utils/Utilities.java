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

    public static String decodeEmail(String email) {
        if(email != null)
            return email.replace(Constants.dot,".");
        else
            return email;
    }

    public static String monthFormatter(int month){
        String formattedMonth;
        switch (month)
        {
            case 1:
                formattedMonth="jan";
                break;
            case 2:
                formattedMonth="feb";
                break;
            case 3:
                formattedMonth="mar";
                break;
            case 4:
                formattedMonth="apr";
                break;
            case 5:
                formattedMonth="may";
                break;
            case 6:
                formattedMonth="jun";
                break;
            case 7:
                formattedMonth="jul";
                break;
            case 8:
                formattedMonth="aug";
                break;
            case 9:
                formattedMonth="sep";
                break;
            case 10:
                formattedMonth="oct";
                break;
            case 11:
                formattedMonth="nov";
                break;
            default:
                formattedMonth="dec";
        }
        return formattedMonth;
    }

}
