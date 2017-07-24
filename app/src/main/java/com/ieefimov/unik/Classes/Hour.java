package com.ieefimov.unik.Classes;

/**
 * Created by IEEfimov on 24.07.2017.
 */

public class Hour {
    private long id;
    private String start;
    private String end;
    private long calendar;

    public Hour() {
        this.id = -1;
        this.start = "00:00";
        this.end = "00:00";
        this.calendar = -1;
    }

    public Hour(String start, String end, long calendar) {
        this.id = -1;
        this.start = start;
        this.end = end;
        this.calendar = calendar;
    }

    public Hour(Hour copy) {
        this.id = copy.id;
        this.start = copy.start;
        this.end = copy.end;
        this.calendar = copy.calendar;
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

    public boolean isValid(){
        if (calendar >= 0){
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
}
