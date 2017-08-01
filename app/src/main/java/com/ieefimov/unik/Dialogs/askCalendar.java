package com.ieefimov.unik.Dialogs;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ieefimov.unik.Classes.CalendarItem;
import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Classes.mySimpleAdapter;
import com.ieefimov.unik.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class askCalendar extends DialogFragment {

    ListView calendarList;
    Activity activity;

    ConnectorDB database;
    SharedPreferences mPreferences;
    final String ATTRIBUTE_NAME = "name";


    ////////////////////////////////////////////////////

    private String[] calendars;
    private int mCurrent=0;

    TextView title,subtitle;
    String titleStr,subStr;

    mySimpleAdapter sAdapter;

    private Space.DialogChoiceCalendar mListener;
    private int todo;

    public askCalendar() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity, int todo){
        this.todo = todo;
        this.mListener = (Space.DialogChoiceCalendar) activity ;

        mPreferences = activity.getSharedPreferences(Space.APP_PREFERENCE,activity.MODE_PRIVATE);
        database =  new ConnectorDB(activity,1);
        this.activity = activity;
        getData();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ask_calendar,container,false);

        calendarList = (ListView) view.findViewById(R.id.calendarList);
        calendarList.setAdapter(sAdapter);
        calendarList.setOnItemClickListener(onItemClickListener);
        return view;
    }

    private void getData(){
        getFiles();
        mCurrent = mPreferences.getInt(Space.PREF_CURRENT_CALENDAR,0);
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(calendars.length);
        Map<String, Object> m;

        if (calendars.length<=mCurrent) mCurrent=0;

        Space.currentCalendar = calendars[mCurrent];

        for (int i = 0; i < calendars.length; i++) {
            m = new HashMap<>();
            m.put(ATTRIBUTE_NAME, calendars[i]);
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME};
        int [] to = { R.id.file};

        sAdapter = new mySimpleAdapter(activity, data, R.layout.item_select_calendar, from, to);
    }


    private void getFiles(){
        CalendarItem result[] = database.selectCalendar(-1);
        calendars = new String[result.length];

        for (int i = 0; i < result.length; i++) {
            calendars[i] = result[i].getName();
        }
    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(Space.PREF_CURRENT_CALENDAR,position);
            editor.apply();

            mListener.choiseCalendar();

            dismiss();
//            askName askAction = new askName();
//            currentFile = files[who];
//            askAction.setActivity(activity,Space.OnCompleteListener.RENAME_CALENDAR);
//            askAction.show(getFragmentManager(),currentFile);
        }
    };


}
