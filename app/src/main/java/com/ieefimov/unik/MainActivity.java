package com.ieefimov.unik;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ieefimov.unik.Classes.CalendarItem;
import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.HomeWork;
import com.ieefimov.unik.Classes.Hour;
import com.ieefimov.unik.Classes.Item;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Classes.mainSimpleAdapter;
import com.ieefimov.unik.Dialogs.askCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.ieefimov.unik.R.id.timeStart;

public class MainActivity extends AppCompatActivity implements Space.DialogChoiceCalendar {

   // Context context;
    Activity activity;

    DrawerLayout drawerLayout;
    ListView mainList;
    Button today,tomorrow;
    Toolbar tool;

    Button choiseCalendarBtn;

    LinearLayout newBG;
    LinearLayout psevdo;
    EditText editDZ;
    Button cancelDZ;
    Button okDZ;

    final String ATTRIBUTE_START = "start";
    final String ATTRIBUTE_END = "end";
    final String ATTRIBUTE_ACTION = "action";
    final String ATTRIBUTE_ROOM = "room";
    final String ATTRIBUTE_DZ = "dz";


    CalendarItem currentCalendar;
    Hour[] currentHours;
    Item[] currentItems;
    HomeWork[] currentHomeWorks;
    ConnectorDB database;

    LinearLayout curItem;
    boolean isShowed = false;


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

        tool = (Toolbar) findViewById(R.id.toolbar);
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

        newBG = (LinearLayout) findViewById(R.id.bgNew);
        psevdo = (LinearLayout) findViewById(R.id.psevdo);
        editDZ = (EditText) findViewById(R.id.editDZ);
        okDZ = (Button) findViewById(R.id.okDZ);
        cancelDZ = (Button) findViewById(R.id.cancelDZ);

        okDZ.setOnClickListener(psevdoOnClickBtn);
        cancelDZ.setOnClickListener(psevdoOnClickBtn);

        newBG.setY(10000);
        newBG.setX(10000);

//        ViewGroup.LayoutParams params1 = mainList.getLayoutParams();
//        params1.height = mainList.getHeight();
//        params1.width = mainList.getWidth();
//        newBG.setLayoutParams(params1);

        activity = this;

        database = new ConnectorDB(this,1); // подключение к БД.
        getData();
        calendarView.setOnDateChangeListener(onDateChangeListener);

        calendar = Calendar.getInstance();
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
        tool.setTitle(currentCalendar.getName());
        //================

        int day = (calendar.get(Calendar.DAY_OF_WEEK)+5)%7;
        int week = 0;
        if (currentCalendar.isDifferentWeek()) week = (calendar.get(Calendar.WEEK_OF_YEAR)%2);

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

        int[] teeemp = new int[currentCalendar.getItemCount()];
        for (int i = 0; i < teeemp.length; i++) {
            teeemp[i] = 0;
        }
        teeemp[1] = R.drawable.ic_info_outline_white_24dp;
        teeemp[2] = R.drawable.ic_autorenew_white_24dp;

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(currentCalendar.getItemCount());
        Map<String, Object> m;

        for (int i = 0; i < currentCalendar.getItemCount(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_START, currentHours[i].getStart());
            m.put(ATTRIBUTE_END, currentHours[i].getEnd());
            m.put(ATTRIBUTE_ACTION, currentItems[i].getName());
            m.put(ATTRIBUTE_ROOM, currentItems[i].getRoom());
            m.put(ATTRIBUTE_DZ,teeemp[i]);
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_START, ATTRIBUTE_END,ATTRIBUTE_ACTION,ATTRIBUTE_ROOM,ATTRIBUTE_DZ};
        int [] to = { timeStart, R.id.timeEnd,R.id.actionText,R.id.actionRoom,R.id.currentDZ};

        mainSimpleAdapter sAdapter = new mainSimpleAdapter(this, data, R.layout.item_action_main, from, to);

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

    Button.OnClickListener psevdoOnClickBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.okDZ:
                    // TODO: 02.08.2017 Сохранение ДЗ..
                    Toast.makeText(activity, "ХАХ, ти думав я працюю?! (с)", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.cancelDZ:
                    hideDialog();
                    break;
            }
        }
    };

    private void showDialog(View view,int position){
        if (!isShowed){
            isShowed = true;
            mainList.setEnabled(false);
            curItem = (LinearLayout) view;

            newBG.setY(curItem.getHeight());
            newBG.setX(0);

            Animation animo = AnimationUtils.loadAnimation(activity,R.anim.alpha_to_0);
            animo.setAnimationListener(animationOnShow);
            float animoHeight = curItem.getY();
            Animation animo2 = new TranslateAnimation(0,0,0,-animoHeight);
            animo2.setDuration(250);
            animo2.setFillAfter(true);

            curItem.startAnimation(animo2);
            newBG.startAnimation(animo);
        }
    }
    private void hideDialog(){
        if (isShowed){
            isShowed = false;

            Animation animation1 = AnimationUtils.loadAnimation(activity,R.anim.alpha_to_1);
            float translateY = curItem.getY();
            Animation animation2 = new TranslateAnimation(0,0,-translateY,0);
            animation2.setDuration(400);
            animation2.setFillAfter(true);
            animation2.setAnimationListener(animationOnHide);

            animation1.setFillAfter(true);
            curItem.startAnimation(animation2);
            newBG.startAnimation(animation1);

            newBG.setY(curItem.getHeight());
            curItem = null;
        }

    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           showDialog(view,position);
        }
    };

    Animation.AnimationListener animationOnHide = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            newBG.setX(1000);
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editDZ.getWindowToken(),0);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationOnShow = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editDZ,0);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            editDZ.requestFocus();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    };


    @Override
    public void choiceCalendar() {
        drawerLayout.closeDrawer(Gravity.LEFT,false);
        getData();
    }

    @Override
    public void retCalendar(int index) {

    }


}
