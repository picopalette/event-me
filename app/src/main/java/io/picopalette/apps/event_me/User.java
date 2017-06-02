package io.picopalette.apps.event_me;

/**
 * Created by ramkumar on 02/06/17.
 */

public class User {

    public String uId;
    public String displayName;
    public String email;
    public String dpUrl;

    public User() {
    }

    public User(String uId, String displayName, String email, String dpUrl) {
        this.uId = uId;
        this.displayName = displayName;
        this.email = email;
        this.dpUrl = dpUrl;
    }

}
