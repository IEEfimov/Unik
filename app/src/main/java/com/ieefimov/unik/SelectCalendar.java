package com.ieefimov.unik;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ieefimov.unik.classes.CalendarItem;
import com.ieefimov.unik.classes.ConnectorDB;
import com.ieefimov.unik.classes.Space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectCalendar extends AppCompatActivity {


    ListView calendarList;
    Activity activity;

    ConnectorDB database;
    SharedPreferences mPreferences;
    final String ATTRIBUTE_NAME = "name";

    ////////////////////////////////////////////////////


    private String[] calendars;
    private int mCurrent=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ask_calendar);

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);  // Добавляем тулбар

        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = Space.stausBarHeight;   //                  для разных устройств
        kostil.setLayoutParams(params);

        mPreferences = getSharedPreferences(Space.APP_PREFERENCE,activity.MODE_PRIVATE);

        calendarList = (ListView) findViewById(R.id.calendarList);
        calendarList.setOnItemClickListener(onItemClickListener);

        database =  new ConnectorDB(this,1);
        activity = this;
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: 26.07.2017 Адекватное меню для выбора календаря
        //getMenuInflater().inflate(R.menu.settings_backups_tool,menu);
        return true;
    }

    @Override
    public void finish() {
        Space.mainDrawer.closeDrawer(Gravity.LEFT,false);
        super.finish();
        overridePendingTransition(R.anim.show_activity,R.anim.hide_activity);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    private void getData(){
        getFiles();
        mCurrent = mPreferences.getInt(Space.PREF_CURRENT_CALENDAR,0);
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(calendars.length);
        Map<String, Object> m;

        Space.currentCalendar = calendars[mCurrent];

        for (int i = 0; i < calendars.length; i++) {
            m = new HashMap<>();
            m.put(ATTRIBUTE_NAME, calendars[i]);
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME};
        int [] to = { R.id.file};

        //mySimpleAdapter sAdapter = new mySimpleAdapter(this, data, R.layout.item_select_calendar, from, to);

        //calendarList.setAdapter(sAdapter);
        calendarList.setOnItemClickListener(onItemClickListener);

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

            finish();
//            askName askAction = new askName();
//            currentFile = files[position];
//            askAction.setActivity(activity,Space.OnCompleteListener.RENAME_CALENDAR);
//            askAction.show(getFragmentManager(),currentFile);
        }
    };




}


