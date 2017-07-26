package com.ieefimov.unik.Classes;

import android.content.Context;
import android.graphics.Color;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by IEEfimov on 26.07.2017.
 */

public class mySimpleAdapter extends SimpleAdapter {

    public mySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public void setViewText(TextView v, String text) {

        if (text.equals(Space.currentCalendar)) {
            v.setBackgroundColor(Color.argb(155,34,34,34));
            v.setTextColor(Color.argb(255,64,255,179));
        }
        v.setText(text);

    }
}