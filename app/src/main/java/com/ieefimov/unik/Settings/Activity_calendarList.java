package com.ieefimov.unik.settings;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ieefimov.unik.R;
import com.ieefimov.unik.adapters.CalendarListSimpleAdapter;
import com.ieefimov.unik.classes.CalendarItem;
import com.ieefimov.unik.classes.ConnectorDB;
import com.ieefimov.unik.classes.Item;
import com.ieefimov.unik.classes.Space;
import com.ieefimov.unik.dialogs.askConfirm;
import com.ieefimov.unik.dialogs.askName;
import com.ieefimov.unik.dialogs.choiceAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ieefimov.unik.classes.Space.currentCalendar;

public class Activity_calendarList extends AppCompatActivity implements
        Space.DialogChoiceAction,Space.DialogConfirm, Space.DialogName {

    ListView calendarList;
    Activity activity;

    FloatingActionButton floating;

    ConnectorDB database;
    SharedPreferences mPreferences;
    final String ATTRIBUTE_NAME = "name";

    // TODO: 30.07.2017 Сделать кнопку "Добавить календарь" плавающим плюсеком


    ////////////////////////////////////////////////////

    private CalendarItem[] calendars;
    private String[] mLongActions;
    private int mCurrent=0;

    CalendarListSimpleAdapter sAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_list);

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);  // Добавляем тулбар

        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = Space.stausBarHeight;   //                  для разных устройств
        kostil.setLayoutParams(params);

        calendarList = (ListView) findViewById(R.id.calendarList);
        calendarList.setOnItemClickListener(onItemClickListener);
        calendarList.setOnItemLongClickListener(onItemLongClickListener);
        floating = (FloatingActionButton) findViewById(R.id.floating);
        floating.setOnClickListener(onFloatingClick);

        database = new ConnectorDB(this);
        activity = this;
        mPreferences = activity.getSharedPreferences(Space.APP_PREFERENCE,MODE_PRIVATE);

        mLongActions = new String[5];
        mLongActions[0] = "Редактировать рассписание";
        mLongActions[1] = "Настройки календаря";
        mLongActions[2] = "Сделать резервную копию";
        mLongActions[3] = "Поделится";
        mLongActions[4] = "Удалить";

        getData();
    }


    private void getData(){
        getCalendars();
        mCurrent = mPreferences.getInt(Space.PREF_CURRENT_CALENDAR,0);
        if (calendars.length<=mCurrent) mCurrent=0;
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(calendars.length);
        Map<String, Object> m;

        currentCalendar = calendars[mCurrent].getName();

        for (int i = 0; i < calendars.length; i++) {
            m = new HashMap<>();
            m.put(ATTRIBUTE_NAME, calendars[i].getName());
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME};
        int [] to = { R.id.file};

        sAdapter = new CalendarListSimpleAdapter(activity, data, R.layout.item_calendar_list, from, to);
        calendarList.setAdapter(sAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        askName askName = new askName();
        askConfirm askConfirm = new askConfirm();
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void getCalendars(){
        calendars = database.selectCalendar(-1);
    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(Space.PREF_EDITED_CALENDAR,position);
            editor.apply();

            Intent intent = new Intent(getApplicationContext(),Activity_itemsEdit.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                startActivity(intent,options.toBundle());
            }
            else startActivity(intent);
        }
    };

    ListView.OnItemLongClickListener onItemLongClickListener = new ListView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            choiceAction askAction = new choiceAction();
            askAction.setActivity(activity,mLongActions,position);
            askAction.show(getFragmentManager(),"Выберите действие:");

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(Space.PREF_EDITED_CALENDAR,position);
            editor.apply();

            return false;
        }
    };


    @Override
    public void choiceDone(int position,int result) {

        Intent intent;

        int selected = mPreferences.getInt(Space.PREF_EDITED_CALENDAR,-2);
        CalendarItem currentCalendar = calendars[selected];

        switch (result){
            case 0: // Редактировать рассписание
                intent = new Intent(getApplicationContext(),Activity_itemsEdit.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                    startActivity(intent,options.toBundle());
                }
                else startActivity(intent);
                break;
            case 1: // Настройки календаря
                intent = new Intent(getApplicationContext(),Activity_calendarEdit.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                    startActivity(intent,options.toBundle());
                }
                else startActivity(intent);
                break;
            case 2: // Сделать резевную копию
                    database.SaveCalendar(currentCalendar,activity);
                 break;
            case 3: // Поделится
                // TODO: 28.07.2017 Функция "Поделится"
                String filePath = database.ShareCalendarItem(currentCalendar,activity);
                Intent sharing = new Intent(Intent.ACTION_SEND);
                sharing.setType("file/*");
                sharing.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+filePath));
                //текстовая подпись sharing.putExtra(Intent.EXTRA_SUBJECT,"sharing calendar..");
                startActivity(Intent.createChooser(sharing,"ShareFile"));
                //Toast.makeText(activity, "Еще не работает", Toast.LENGTH_SHORT).show();
                break;
            case 4: // Удалить
                if (calendars.length < 2){
                    String no = activity.getResources().getString(R.string.dialog_deleteCalendar_lastOne);
                    Toast.makeText(activity, no, Toast.LENGTH_SHORT).show();
                    break;
                }
                String title = activity.getResources().getString(R.string.dialog_deleteCalendar_title)
                        + calendars[position].getName() + "\".";
                String subtitle = activity.getResources().getString(R.string.dialog_deleteCalendar_subtitle);
                askConfirm askConfirm = new askConfirm();
                askConfirm.setActivity(activity,position);
                askConfirm.show(getFragmentManager(),title,subtitle);

                break;
        }
    }

    @Override
    public void editItem(Item item) {

    }

    @Override
    public void confirm(int position, boolean result) {
        if (result){
            database.deleteCalendar(calendars[position]);
            getData();
        }
    }

    View.OnClickListener onFloatingClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String titleStr = activity.getResources().getString(R.string.dialog_addCalendar_title);
            String subStr = activity.getResources().getString(R.string.dialog_addCalendar_subtitle);
            String defaultName = activity.getResources().getString(R.string.dialog_addCalendar_defaultName);
            askName askName = new askName();
            askName.setActivity(activity,-1);
            askName.show(getFragmentManager(),titleStr,subStr,defaultName);
        }
    };

    @Override
    public void getName(int position, String result) {
        CalendarItem temp = new CalendarItem();
        temp.setName(result);
        database.insertCalendar(temp);
        getData();
    }
}
