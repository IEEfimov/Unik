package com.ieefimov.unik;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ieefimov.unik.Classes.CalendarItem;
import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.Hour;
import com.ieefimov.unik.Classes.Item;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Dialogs.askCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Space.DialogChoiceCalendar {

   // Context context;
    Activity activity;

    DrawerLayout drawerLayout;
    ListView mainList;
    Button today,tomorrow;

    Button choiseCalendarBtn;

    final String ATTRIBUTE_START = "start";
    final String ATTRIBUTE_END = "end";
    final String ATTRIBUTE_ACTION = "action";
    final String ATTRIBUTE_ROOM = "room";


    CalendarItem currentCalendar;
    Hour[] currentHours;
    Item[] currentItems;
    ConnectorDB database;

    //////////////////////////////////////

    CalendarView calendarView;
    Calendar calendar;
    //int day,week;

    //int Day,Week,DayOfMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //context = this;

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = getStatusBarHeight();   //                  для разных устройств
        kostil.setLayoutParams(params);
        setSupportActionBar(tool);

        mainList = (ListView) findViewById(R.id.main_list);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        today = (Button) findViewById(R.id.today);
        tomorrow = (Button) findViewById(R.id.tomorrow);



        Button settingsBtn = (Button) findViewById(R.id.nav_settingsBtn);
        choiseCalendarBtn = (Button) findViewById(R.id.nav_choiseCalendar);

        settingsBtn.setOnClickListener(onNavClickListener);
        choiseCalendarBtn.setOnClickListener(onNavClickListener);

        today.setOnClickListener(onClickListener);
        tomorrow.setOnClickListener(onClickListener);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        Space.mainDrawer = drawerLayout;
        Space.stausBarHeight = getStatusBarHeight();

        activity = this;

        database = new ConnectorDB(this,1); // подключение к БД.
        getData();
        calendarView.setOnDateChangeListener(onDateChangeListener);

        calendar = Calendar.getInstance();


//        day = (calendar.get(Calendar.DAY_OF_WEEK)+5)%7;
//        week = (calendar.get(Calendar.WEEK_OF_YEAR)%2);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return true;
    }

    private void getData(){

        SharedPreferences mPreferences;
        mPreferences = getSharedPreferences(Space.APP_PREFERENCE,MODE_PRIVATE);
        int current = mPreferences.getInt(Space.PREF_CURRENT_CALENDAR,0);

        CalendarItem[] items = database.selectCalendar(-1);
        if (current >= items.length) current = 0;
        currentCalendar = items[current];

        calendar = Calendar.getInstance();

        update();
    }

    private void update(){

        currentHours = database.selectHour(currentCalendar);
        //================

        int day = (calendar.get(Calendar.DAY_OF_WEEK)+5)%7;
        int week = (calendar.get(Calendar.WEEK_OF_YEAR)%2);

        currentItems = new Item[currentCalendar.getItemCount()];
        Item[] tempItems = database.selectItems(day,week,currentCalendar.getId());

        for (int i = 0; i < tempItems.length; i++) {
            for (int j = 0; j < currentHours.length;j++){
                if (tempItems[i].getHour()==currentHours[j].getId())
                    currentItems[currentHours[j].getNum()] = tempItems[i];
            }
        }
        for (int i=0;i<currentItems.length;i++){
            if (currentItems[i] == null){
                currentItems[i] = new Item();
                currentItems[i].setDay(day);
                currentItems[i].setCalendar(currentCalendar.getId());
                currentItems[i].setHour(currentHours[i].getId());
                currentItems[i].setWeek(week);
                currentItems[i].setName(getResources().getString(R.string.dialog_editItem_defaultName));
                currentItems[i].setRoom(getResources().getString(R.string.dialog_editItem_defaultRoom));
            }
        }

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(currentCalendar.getItemCount());
        Map<String, Object> m;

        for (int i = 0; i < currentCalendar.getItemCount(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_START, currentHours[i].getStart());
            m.put(ATTRIBUTE_END, currentHours[i].getEnd());
            m.put(ATTRIBUTE_ACTION, currentItems[i].getName());
            m.put(ATTRIBUTE_ROOM, currentItems[i].getRoom());
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_START, ATTRIBUTE_END,ATTRIBUTE_ACTION,ATTRIBUTE_ROOM};
        int [] to = { R.id.timeStart, R.id.timeEnd,R.id.actionText,R.id.actionRoom};

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item_action_main, from, to);

        mainList.setAdapter(sAdapter);
        mainList.setOnItemClickListener(onItemClickListener);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            calendar.set(year,month,dayOfMonth);
            update();
        }
    };

    public Button.OnClickListener onNavClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.nav_settingsBtn:
                    intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        ActivityOptions options =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                        startActivity(intent,options.toBundle());
                    }
                    else startActivity(intent);

                    break;
                case (R.id.nav_choiseCalendar):
                    askCalendar ask = new askCalendar();
                    ask.setActivity(activity,0);
                    ask.show(getFragmentManager(),"1");
                    break;

            }

        }
    };

    public Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            calendar = Calendar.getInstance();
            switch (v.getId()){
                case (R.id.today):
                    calendarView.setDate(calendar.getTime().getTime());
                    break;
                case (R.id.tomorrow):
                    calendar.add(Calendar.DATE,1);
                    calendarView.setDate(calendar.getTime().getTime());
                    break;
            }
            update();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        getData();

    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };


    @Override
    public void choiseCalendar() {
        drawerLayout.closeDrawer(Gravity.LEFT,false);
        getData();
    }

    @Override
    public void retCalendar() {

    }
}
