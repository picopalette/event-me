package io.picopalette.apps.event_me.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class DateAndTime implements Serializable{
    private int dayOfMonth;
    private int month;
    private int year;
    private int hourOfDay;
    private int minute;
    private int endHourOfDay;
    private int endMinute;
    private String formattedTime;
    private String formattedDate;

    public DateAndTime() {

    }

    public DateAndTime( int hourOfDay, int minute, int dayOfMonth, int month, int year) {
        this.minute = minute;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
        this.hourOfDay = hourOfDay;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getEndHourOfDay() {
        return endHourOfDay;
    }

    public void setEndHourOfDay(int endHourOfDay) {
        this.endHourOfDay = endHourOfDay;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public String getFormattedDate() {
        int mMonth = month;
        String sMonth;
        String sDay;
        mMonth++;
        if(mMonth<10)
            sMonth = "0"+mMonth;
        else
            sMonth = ""+mMonth;
        if(dayOfMonth<10)
            sDay = "0"+dayOfMonth;
        else
            sDay = ""+dayOfMonth;

        return sDay+"/"+sMonth+"/"+year;
    }

    public String getFormattedTime() {
        String mtime = "AM";
        int mHour = hourOfDay;
        int mMinute = minute;
        String sHour,sMinute;
        if(mHour >= 12) {
            mtime = "PM";
            mHour = mHour - 12;
        }
        if(mHour == 0)
            mHour = 12;

        if(mHour < 10 )
            sHour = ""+mHour;
        else
            sHour = ""+mHour;

        if(mMinute < 10 )
            sMinute = "0"+mMinute;
        else
            sMinute = ""+mMinute;

        return sHour+":"+sMinute+" "+mtime;
    }

    public String getFormattedEndTime() {
        String mtime = "AM";
        int mHour = endHourOfDay;
        int mMinute = endMinute;
        String sHour,sMinute;
        if(mHour >= 12) {
            mtime = "PM";
            mHour = mHour - 12;
        }
        if(mHour == 0)
            mHour = 12;

        if(mHour < 10 )
            sHour = ""+mHour;
        else
            sHour = ""+mHour;

        if(mMinute < 10 )
            sMinute = "0"+mMinute;
        else
            sMinute = ""+mMinute;

        return sHour+":"+sMinute+" "+mtime;
    }

    public ArrayList<String> getSplitTime(){
        ArrayList<String> mTime = new ArrayList<String>();
        String mtime = "a.m.";
        int mHour = hourOfDay;
        int mMinute = minute;
        String sHour,sMinute;
        if(mHour >= 12) {
            mtime = "p.m.";
            mHour = mHour - 12;
        }
        if(mHour == 0)
            mHour = 12;

        if(mHour < 10 )
            sHour = ""+mHour;
        else
            sHour = ""+mHour;

        if(mMinute < 10 )
            sMinute = "0"+mMinute;
        else
            sMinute = ""+mMinute;
        mTime.add(sHour);
        mTime.add(sMinute);
        mTime.add(mtime);

        return mTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
