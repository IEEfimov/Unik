package com.ieefimov.unik.classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IEEfimov on 02.08.2017.
 */

public class HomeWork {
    private long id;
    private long subject;
    private long calendar;
    private Calendar dateOfShow;
    private String string;


    public HomeWork() {
        id = -1;
        subject = -1;
        calendar = -1;
        dateOfShow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            dateOfShow.setTime(sdf.parse("01.02.2010"));
        }catch (Exception e){

        }
        string = "";
    }

    public HomeWork(long id, long subject, long calendar, Calendar dateOfShow, String string) {
        this.id = id;
        this.subject = subject;
        this.calendar = calendar;
        this.dateOfShow = dateOfShow;
        this.string = string;
    }

    public HomeWork(HomeWork copy) {
        this.id = copy.id;
        this.subject = copy.subject;
        this.calendar = copy.calendar;
        this.dateOfShow = copy.dateOfShow;
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

    public Calendar getDateOfShow() {
        return dateOfShow;
    }

    public void setDateOfShow(Calendar dateOfShow) {
        this.dateOfShow = dateOfShow;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
