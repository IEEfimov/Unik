package com.ieefimov.unik.Classes;

import java.io.Serializable;

/**
 * Created by IEEfimov on 26.07.2017.
 */

public class SaveItem implements Serializable {

    private CalendarItem mCalendar;
    private Hour[] mHours;
    private Item[] mItems;

    public SaveItem() {
        this.mCalendar = new CalendarItem();
        this.mHours = new Hour[0];
        this.mItems = new Item[0];
    }

    public SaveItem(CalendarItem mCalendar, Hour[] mHours, Item[] mItems) {
        this.mCalendar = mCalendar;
        this.mHours = mHours;
        this.mItems = mItems;
    }

    public SaveItem(SaveItem copy) {
        this.mCalendar = copy.mCalendar;
        this.mHours = copy.mHours;
        this.mItems = copy.mItems;
    }

    public CalendarItem getCalendar() {
        return mCalendar;
    }

    public void setCalendar(CalendarItem calendar) {
        this.mCalendar = calendar;
    }

    public Hour[] getHours() {
        return mHours;
    }

    public void setHours(Hour[] hours) {
        this.mHours = hours;
    }

    public Item[] getItems() {
        return mItems;
    }

    public void setItems(Item[] items) {
        this.mItems = items;
    }
}
