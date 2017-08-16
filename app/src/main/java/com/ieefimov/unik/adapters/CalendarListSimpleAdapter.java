package com.ieefimov.unik.adapters;

import android.content.Context;
import android.graphics.Color;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ieefimov.unik.classes.Space;

import java.util.List;
import java.util.Map;

/**
 * Created by IEEfimov on 26.07.2017.
 */

public class CalendarListSimpleAdapter extends SimpleAdapter {

    public CalendarListSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public void setViewText(TextView v, String text) {
        if (text == Space.currentCalendar){
            v.setTextColor(Color.argb(255,64,255,179));
        }
        else {
            v.setBackgroundColor(Color.argb(0,0,0,0));
            v.setTextColor(Color.WHITE);
        }



        v.setText(text);

    }
}