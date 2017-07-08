package io.picopalette.apps.event_me.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 677;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private final String TAG = "SignInActivity";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ActionBar actionBar = getSupportActionBar();
        sharedPreferences = getApplicationContext().getSharedPreferences("event_me", Context.MODE_PRIVATE);
        if(actionBar!=null)
            actionBar.hide();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.api_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent launchNextActivity;
            launchNextActivity = new Intent(getApplicationContext(), MainActivity.class);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launchNextActivity);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(getApplicationContext(),result.getStatus().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference userRef = mDatabase.child(Constants.users).child(EncodeString(user.getEmail()));
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()) {
                                        Log.d(TAG, getString(R.string.new_user));
                                        User newUser = new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                                        mDatabase.child(Constants.users).child(EncodeString(user.getEmail())).setValue(newUser);
                                        mDatabase.child(Constants.people).child(EncodeString(user.getEmail())).setValue(user.getDisplayName());
                                    }
                                    mDatabase.child(Constants.users).child(EncodeString(user.getEmail())).child("fcmtoken").setValue(sharedPreferences.getString("refreshtoken", null));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_SHORT).show();
                            Intent launchNextActivity;
                            launchNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(launchNextActivity);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private static String  EncodeString(String email) {
        return email.replace(".",Constants.dot);
    }

    @Override
    public void onClick(View v) {
        if(v == signInButton){
            signIn();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
