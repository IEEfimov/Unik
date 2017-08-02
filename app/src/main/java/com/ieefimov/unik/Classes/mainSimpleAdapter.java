package com.ieefimov.unik.Classes;

import android.content.Context;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by IEEfimov on 26.07.2017.
 */

public class mainSimpleAdapter extends SimpleAdapter {

    public mainSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public void setViewText(TextView v, String text) {
        super.setViewText(v,text);
    }
}