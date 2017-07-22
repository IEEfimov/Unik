package com.ieefimov.unik.Classes;

/**
 * Created by IEEfimov on 12.07.2017.
 */

public class Item {
    private int hour;
    private int day;
    private int week;
    private int calendar;
    private String name;
    private String room;

    public Item(){
        hour = 0;
        day = 0;
        week = 0;
        calendar = 0;
    }

    public Item(int hour, int day, int week,int calendar,String name,String room) {
        this.hour = hour;
        this.day = day;
        this.week = week;
        this.calendar = calendar;
        this.name = name;
        this.room = room;
    }

    public Item(Item copy) {
        this.hour = copy.hour;
        this.day = copy.day;
        this.week = copy.week;
        this.calendar = copy.calendar;
        this.name = copy.name;
        this.room = copy.room;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
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

    public int getCalendar() {
        return calendar;
    }

    public void setCalendar(int calendar) {
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
