package io.picopalette.apps.event_me.Models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by ramkumar on 10/06/17.
 */

public class Location implements Serializable{
    private String name;
    private double lat;
    private double lon;

    public Location() {
    }

    public Location(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
