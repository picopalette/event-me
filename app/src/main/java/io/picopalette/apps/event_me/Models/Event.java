package io.picopalette.apps.event_me.Models;


import android.net.Uri;

import java.io.Serializable;
import java.util.HashMap;

import io.picopalette.apps.event_me.Utils.Constants;

public class Event implements Serializable {

    private String id, name, type;
    private Location place;
    private Constants.EventStatus status;
    private Boolean isPrivate;
    private DateAndTime dateAndTime;
    private HashMap<String, Constants.UserStatus> participants;
    private Uri uri;

    public Event() {
    }

    public Event(String name, String type, Location place, DateAndTime dateAndTime, Boolean isPrivate, String id, Constants.EventStatus status, HashMap<String, Constants.UserStatus> participants, Uri downloaduri) {
        this.uri = downloaduri;
        this.name = name;
        this.type = type;
        this.place = place;
        this.dateAndTime = dateAndTime;
        this.isPrivate = isPrivate;
        this.status = status;
        this.id = id;
        this.participants = participants;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location getPlace() {
        return place;
    }

    public void setPlace(Location place) {
        this.place = place;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public DateAndTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(DateAndTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Constants.EventStatus getStatus() {
        return status;
    }

    public void setStatus(Constants.EventStatus status) {
        this.status = status;
    }

    public HashMap<String, Constants.UserStatus> getParticipants() {
        return participants;
    }

    public void setParticipants(HashMap<String, Constants.UserStatus> participants) {
        this.participants = participants;
    }
}

