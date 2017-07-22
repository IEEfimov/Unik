package com.example.ieefimov.unik.Classes;

/**
 * Created by IEEfimov on 22.07.2017.
 */

public class CalendarItem {
    private int id;
    private String name;
    private int itemCount;
    private boolean differentWeek;

    public CalendarItem() {
        this.id = 0;
        this.name = "Стандартный";
        this.itemCount = 5;
        this.differentWeek = true;
    }

    public CalendarItem(int id, String name, int itemCount, boolean differentWeek) {
        this.id = id;
        this.name = name;
        this.itemCount = itemCount;
        this.differentWeek = differentWeek;
    }

    public CalendarItem(CalendarItem copy) {
        this.id = copy.id;
        this.name = copy.name;
        this.itemCount = copy.itemCount;
        this.differentWeek = copy.differentWeek;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean isDifferentWeek() {
        return differentWeek;
    }

    public void setDifferentWeek(boolean differentWeek) {
        this.differentWeek = differentWeek;
    }
}
