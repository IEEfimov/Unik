package com.ieefimov.unik.classes;

/**
 * Created by IEEfimov on 02.08.2017.
 */

public class Subject {
    private long id;
    private long calendar;
    private String name;

    public Subject() {
        id = -1;
        calendar = -1;
        name = "";
    }

    public Subject(long id, long calendar, String name) {
        this.id = id;
        this.calendar = calendar;
        this.name = name;
    }

    public Subject(Subject copy) {
        this.id = copy.id;
        this.calendar = copy.calendar;
        this.name = copy.name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
