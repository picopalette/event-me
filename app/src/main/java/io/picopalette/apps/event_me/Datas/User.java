package io.picopalette.apps.event_me.Datas;

public class User {

    private String uId;
    private String displayName;
    private String email;
    private String dpUrl;

    public User() {
    }

    public User(String uId, String displayName, String email, String dpUrl) {
        this.uId = uId;
        this.displayName = displayName;
        this.email = email;
        this.dpUrl = dpUrl;
    }
}
