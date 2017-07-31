package com.ieefimov.unik.Settings;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ieefimov.unik.Classes.CalendarItem;
import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.Item;
import com.ieefimov.unik.Classes.SaveItem;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Classes.mySimpleAdapter;
import com.ieefimov.unik.Dialogs.askConfirm;
import com.ieefimov.unik.Dialogs.askName;
import com.ieefimov.unik.Dialogs.choiceAction;
import com.ieefimov.unik.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ieefimov.unik.Classes.Space.currentCalendar;

public class Activity_calendarList extends AppCompatActivity implements
        Space.DialogChoiceAction,Space.DialogConfirm {

    ListView calendarList;
    Activity activity;

    ConnectorDB database;
    SharedPreferences mPreferences;
    final String ATTRIBUTE_NAME = "name";

    // TODO: 30.07.2017 Сделать кнопку "Добавить календарь" плавающим плюсеком


    ////////////////////////////////////////////////////

    private CalendarItem[] calendars;
    private String[] mLongActions;
    private int mCurrent=0;

    mySimpleAdapter sAdapter;


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

        database = new ConnectorDB(this,1);
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

        sAdapter = new mySimpleAdapter(activity, data, R.layout.item_calendar_list, from, to);
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
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
            startActivity(intent,options.toBundle());
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
//        mLongActions[0] = "Редактировать рассписание";
//        mLongActions[1] = "Настройки календаря";
//        mLongActions[2] = "Сделать резервную копию";
//        mLongActions[3] = "Поделится";
//        mLongActions[4] = "Удалить";
//

        Intent intent;
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);

        int selected = mPreferences.getInt(Space.PREF_EDITED_CALENDAR,-2);
        CalendarItem currentCalendar = calendars[selected];

        switch (result){
            case 0: // Редактировать рассписание
                intent = new Intent(getApplicationContext(),Activity_itemsEdit.class);
                startActivity(intent,options.toBundle());
                break;
            case 1: // Настройки календаря
                intent = new Intent(getApplicationContext(),Activity_calendarEdit.class);
                startActivity(intent,options.toBundle());
                break;
            case 2: // Сделать резевную копию
                try {
                    String dir = getApplicationInfo().dataDir;
                    String name = currentCalendar.getName()+".iee";
                    SaveItem saveData = database.getSaveData(currentCalendar);
                    FileOutputStream fileOut = new FileOutputStream(dir+"/"+name);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(saveData);
                    out.close();
                    fileOut.close();
                    Toast.makeText(activity, "Создана копия: \n\r "+name, Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3: // Поделится
                // TODO: 28.07.2017 Функция "Поделится"
                Toast.makeText(activity, "Еще не работает", Toast.LENGTH_SHORT).show();
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
}
