package io.picopalette.apps.event_me.Datas;

public class MyEvent {

    private String eveName, eveType, place, eveDate, eveTime;
    private Boolean isPrivate;
    private String eId;

    public MyEvent(){
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
