package io.picopalette.apps.event_me.Datas;


import android.text.format.Time;

import java.util.Date;

/**
 * Created by holmesvinn on 3/6/17.
 */

public class MyEvent {

    public String eveName, eveType, place, eveDate, eveTime;
    public Boolean isPrivate;
    public String eId;


    public MyEvent()
    {

    }

    public MyEvent(String eveName, String eveType, String place, String eveDate, String eveTime, Boolean isPrivate,String eId) {
        this.eveName = eveName;
        this.eveType = eveType;
        this.place = place;
        this.eveDate = eveDate;
        this.eveTime = eveTime;
        this.isPrivate = isPrivate;
        this.eId = eId;
    }
}
