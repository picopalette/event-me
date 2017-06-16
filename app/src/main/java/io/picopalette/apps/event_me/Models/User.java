package io.picopalette.apps.event_me.Models;

public class User {

    private String uId;
    private String displayName;
    private String email;
    private String dpUrl;
    private String live;

    public User() {
    }

    public User(String uId, String displayName, String email, String dpUrl, String live) {
        this.uId = uId;
        this.displayName = displayName;
        this.email = email;
        this.dpUrl = dpUrl;
        this.live = live;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDpUrl() {
        return dpUrl;
    }

    public void setDpUrl(String dpUrl) {
        this.dpUrl = dpUrl;
    }
}
