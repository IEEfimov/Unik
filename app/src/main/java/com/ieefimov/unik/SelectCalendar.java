package com.ieefimov.unik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ieefimov.unik.Classes.CalendarItem;
import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.Space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCalendar extends AppCompatActivity {


    ListView calendarList;
    Activity activity;

    ConnectorDB database;
    SharedPreferences mPreferences;
    final String ATTRIBUTE_NAME = "name";
    final String ATTRIBUTE_CHECk = "checked";

    ////////////////////////////////////////////////////

    private String[] calendars;
    private int mCurrent=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_calendar);

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

    public void finish() {
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

        mySimpleAdapter sAdapter = new mySimpleAdapter(this, data, R.layout.item_select_calendar, from, to);

        calendarList.setAdapter(sAdapter);
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

class mySimpleAdapter extends SimpleAdapter{

    public mySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public void setViewText(TextView v, String text) {
        super.setViewText(v, text);
        if (text.equals(Space.currentCalendar)) {
            v.setBackgroundColor(Color.argb(155,34,34,34));
            v.setTextColor(Color.argb(255,64,255,179));
        }

    }
}
