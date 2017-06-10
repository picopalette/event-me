package io.picopalette.apps.event_me.Utils;

public class Constants {
    public static String events = "events";
    public static String users = "users";
    public static CharSequence dot = "(dot)";
    public static String people= "people";

    public enum EventStatus {
        UPCOMING,
        ONGOING,
        LIVE,
        ENDED
    }

    public enum UserStatus {
        INVITED,
        GOING,
        NOT_GOING,
        OWNER
    }
}
