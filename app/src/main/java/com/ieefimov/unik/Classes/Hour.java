package com.ieefimov.unik.Classes;

import java.io.Serializable;

/**
 * Created by IEEfimov on 24.07.2017.
 */

public class Hour implements Serializable {
    private long id;
    private String start;
    private String end;
    private long calendar;
    private int num;

    public Hour() {
        this.id = -1;
        this.start = "00:00";
        this.end = "00:00";
        this.calendar = -1;
        this.num = -1;
    }

    public Hour(String start, String end, long calendar, int num) {
        this.id = -1;
        this.start = start;
        this.end = end;
        this.calendar = calendar;
        this.num = num;
    }

    public Hour(Hour copy) {
        this.id = copy.id;
        this.start = copy.start;
        this.end = copy.end;
        this.calendar = copy.calendar;
        this.num = copy.num;
    }

    public boolean isValid(){
        if ((calendar >= 0) && (num >= 0)){
            if (isValidTime(start) && isValidTime(end)) return true;
        }
        return false;
    }

    private boolean isValidTime(String time){
        try {
            if ((time.length()==5) && (time.charAt(2)==':')){
                String temp1 = ""+time.charAt(0)+time.charAt(1);
                String temp2 = ""+time.charAt(3)+time.charAt(4);
                if ((Integer.parseInt(temp1)<24)&&(Integer.parseInt(temp2)<60)) return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public long getCalendar() {
        return calendar;
    }

    public void setCalendar(long calendar) {
        this.calendar = calendar;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
