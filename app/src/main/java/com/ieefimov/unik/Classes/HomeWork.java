package com.ieefimov.unik.classes;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IEEfimov on 02.08.2017.
 */

public class HomeWork {
    private long id;
    private long calendar;
    private String subject;
    private Calendar dateOfShow;
    private String myText;
    private String groupText;
    private String strDate;


    public HomeWork() {
        id = -1;
        calendar = -1;
        subject = "";
        dateOfShow = parseDate("01.01.2017");
        strDate = "";
        myText = "";
        groupText = "";
    }

    public HomeWork(long id, String subject, long calendar, Calendar dateOfShow, String myText, String groupText) {
        this.id = id;
        this.subject = subject;
        this.calendar = calendar;
        this.dateOfShow = dateOfShow;
        this.myText = myText;
        this.groupText = groupText;
    }

    public HomeWork(HomeWork copy) {
        this.id = copy.id;
        this.subject = copy.subject;
        this.calendar = copy.calendar;
        this.dateOfShow = copy.dateOfShow;
        this.myText = copy.myText;
        this.groupText = copy.groupText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
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
        strDate = calendarToString(dateOfShow);
    }

    public String getMyText() {
        return myText;
    }

    public void setMyText(String myText) {
        this.myText = myText;
    }

    public String getGroupText() {
        return groupText;
    }

    public void setGroupText(String groupText) {
        this.groupText = groupText;
    }


    public boolean isValid(){
        if (calendar < 0) return false;
        if (subject.equals("")) return false;
        return true;
    }


    public static Calendar parseDate(String date){
        Calendar result= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            result.setTime(sdf.parse(date));
        }catch (Exception e){
            Log.e("ERRRRRRRRRROOOOR","Ошибка парсинга");
            e.printStackTrace();
        }

        return result;
    }

    public static String calendarToString(Calendar calendar){
        String result = "";
        if (calendar.get(Calendar.DATE)<10) result += "0";
        result += calendar.get(Calendar.DATE)+".";
        if (calendar.get(Calendar.MONTH)<10) result += "0";
        result += (calendar.get(Calendar.MONTH)+1)+".";
        result += calendar.get(Calendar.YEAR);

        return result;
    }
}
