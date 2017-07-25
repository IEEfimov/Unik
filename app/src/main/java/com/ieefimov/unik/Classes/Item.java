package com.ieefimov.unik.Classes;

/**
 * Created by IEEfimov on 12.07.2017.
 */

public class Item {
    private long id;
    private long hour;
    private int day;
    private int week;
    private long calendar;
    private String name;
    private String room;

    public Item(){
        id = -1;
        hour = -1;
        day = -1;
        week = -1;
        calendar = -1;
        name = "";
        room = "";
    }

    public Item(long id,long hour, int day, int week,long calendar,String name,String room) {
        this.id = id;
        this.hour = hour;
        this.day = day;
        this.week = week;
        this.calendar = calendar;
        this.name = name;
        this.room = room;
    }

    public Item(Item copy) {
        this.id = copy.id;
        this.hour = copy.hour;
        this.day = copy.day;
        this.week = copy.week;
        this.calendar = copy.calendar;
        this.name = copy.name;
        this.room = copy.room;
    }

    public boolean isValid(){
        if (day<0) return false;
        if (week<0) return false;
        if (calendar<0) return false;
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public long getCalendar() {
        return calendar;
    }

    public void setCalendar(long calendar) {
        this.calendar = calendar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }


}
