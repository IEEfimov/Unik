package com.ieefimov.unik.Classes;

import java.sql.Date;

/**
 * Created by IEEfimov on 02.08.2017.
 */

public class HomeWork {
    private long id;
    private long subject;
    private long calendar;
    private Date date;
    private String string;


    public HomeWork() {
        id = -1;
        subject = -1;
        calendar = -1;
        date = Date.valueOf("2000-01-01");
        string = "";
    }

    public HomeWork(long id, long subject, long calendar, Date date, String string) {
        this.id = id;
        this.subject = subject;
        this.calendar = calendar;
        this.date = date;
        this.string = string;
    }

    public HomeWork(HomeWork copy) {
        this.id = copy.id;
        this.subject = copy.subject;
        this.calendar = copy.calendar;
        this.date = copy.date;
        this.string = copy.string;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSubject() {
        return subject;
    }

    public void setSubject(long subject) {
        this.subject = subject;
    }

    public long getCalendar() {
        return calendar;
    }

    public void setCalendar(long calendar) {
        this.calendar = calendar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
