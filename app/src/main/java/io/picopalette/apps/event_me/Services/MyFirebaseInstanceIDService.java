package io.picopalette.apps.event_me.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private DatabaseReference userRef;
    private FirebaseUser user;
    private SharedPreferences sharedPreferences;
    private String refreshedToken;


    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("reftoken", refreshedToken);
        sharedPreferences = getApplicationContext().getSharedPreferences("event_me", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("refreshtoken",refreshedToken);
        editor.apply();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child(Constants.users).child(Utilities.encodeEmail(user.getEmail()));
            userRef.child("fcmtoken").setValue(refreshedToken);
        }

        // Get updated InstanceID token.

    }

}
