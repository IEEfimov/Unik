package com.ieefimov.unik.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ieefimov.unik.R;
import com.ieefimov.unik.classes.CalendarItem;
import com.ieefimov.unik.classes.ConnectorDB;
import com.ieefimov.unik.classes.Hour;
import com.ieefimov.unik.classes.Space;
import com.ieefimov.unik.dialogs.askConfirm;
import com.ieefimov.unik.dialogs.askDigit;
import com.ieefimov.unik.dialogs.askName;
import com.ieefimov.unik.dialogs.askTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_calendarEdit extends AppCompatActivity
        implements Space.OnCompleteListener, Space.DialogTimeEdit,Space.DialogConfirm,
                Space.DialogName{

    Spinner calendarSelector;
    LinearLayout setNameLayout;
    LinearLayout differentWeekLayout;
    LinearLayout itemCountLayout;
    CheckBox differentWeekChkBx;
    TextView countView;
    TextView nameView;
    ListView timeList;

    CalendarItem[] calendarItems;
    int currentCalendar = -1;
//    String calendars[];

    Hour[] currentHours;

    Activity activity;

    ConnectorDB database;
    SharedPreferences mPreferences;

    final String ATTRIBUTE_START = "start";
    final String ATTRIBUTE_END = "end";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new ConnectorDB(this); // подключение к БД.

        setContentView(R.layout.activity_settings_calendar_edit);

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);  // Добавляем тулбар

        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = Space.stausBarHeight;   //                  для разных устройств
        kostil.setLayoutParams(params);

        calendarSelector = (Spinner) findViewById(R.id.sett_cal_spinner);
        differentWeekLayout = (LinearLayout) findViewById(R.id.diffrentWeekLayout);
        itemCountLayout = (LinearLayout) findViewById(R.id.itemCountLayout);
        setNameLayout = (LinearLayout) findViewById(R.id.nameLinear);
        differentWeekChkBx = (CheckBox) findViewById(R.id.differentWeek);
        countView = (TextView) findViewById(R.id.item_count);
        nameView = (TextView) findViewById(R.id.nameView);
        timeList = (ListView) findViewById(R.id.timeList);


        differentWeekLayout.setOnClickListener(linearOnClick);
        itemCountLayout.setOnClickListener(linearOnClick);
        setNameLayout.setOnClickListener(linearOnClick);
        calendarSelector.setOnItemSelectedListener(spinnerOnClick);
        timeList.setOnItemClickListener(onItemClickListener);

        activity = this;
        mPreferences = getSharedPreferences(Space.APP_PREFERENCE,MODE_PRIVATE);

        //////////////////////////////////////////////////
        getData();

        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_calendar_edit_tool,menu);
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
            case R.id.action_delete:
                if (calendarItems.length < 2){
                    String no = activity.getResources().getString(R.string.dialog_deleteCalendar_lastOne);
                    Toast.makeText(activity, no, Toast.LENGTH_SHORT).show();
                    break;
                }
                String title = activity.getResources().getString(R.string.dialog_deleteCalendar_title)
                        + calendarItems[currentCalendar].getName() + "\".";
                String subtitle = activity.getResources().getString(R.string.dialog_deleteCalendar_subtitle);
                askConfirm askConfirm = new askConfirm();
                askConfirm.setActivity(activity,currentCalendar);
                askConfirm.show(getFragmentManager(),title,subtitle);
                break;
            case R.id.action_crypt:
                Toast.makeText(this,"Не работает (пока)",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_share:
                String filePath = database.ShareCalendarItem(calendarItems[currentCalendar],activity);
                Intent sharing = new Intent(Intent.ACTION_SEND);
                sharing.setType("file/*");
                sharing.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+filePath));
                startActivity(Intent.createChooser(sharing,"ShareFile"));
                break;
            case R.id.action_save:
                database.SaveCalendar(calendarItems[currentCalendar],activity);
        }
        return true;
    }

    LinearLayout.OnClickListener linearOnClick = new View.OnClickListener() {
        //TODO: Обработчик элемента время
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.diffrentWeekLayout:
                    differentWeekChkBx.setChecked(!differentWeekChkBx.isChecked());
                    calendarItems[currentCalendar].setDifferentWeek(differentWeekChkBx.isChecked());
                    database.updateCalendar(calendarItems[currentCalendar]);
                    break;
                case R.id.itemCountLayout:
                    // TODO: 31.07.2017 Рефакторинг оставшихся диалогов
                    askDigit ask = new askDigit();
                    ask.setActivity(activity,Space.OnCompleteListener.EDIT_ITEM_COUNT);
                    ask.show(getFragmentManager(),(calendarItems[currentCalendar].getItemCount()+""));
                    break;
                case R.id.nameLinear:
                    askName askName = new askName();
                    askName.setActivity(activity,currentCalendar);
                    String title = activity.getResources().getString(R.string.dialog_renameCalendar_title);
                    String subStr = activity.getResources().getString(R.string.dialog_renameCalendar_subtitle);
                    askName.show(getFragmentManager(),title,subStr,calendarItems[currentCalendar].getName());
                    break;

            }
        }
    };

    Spinner.OnItemSelectedListener spinnerOnClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try{
                currentCalendar = position;
                update();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Ошибочка вышла :(",Toast.LENGTH_LONG);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void update(){
        currentHours = database.selectHour(calendarItems[currentCalendar]);
        differentWeekChkBx.setChecked(calendarItems[currentCalendar].isDifferentWeek());
        countView.setText(calendarItems[currentCalendar].getItemCount()+"");
        nameView.setText(calendarItems[currentCalendar].getName());

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(calendarItems[currentCalendar].getItemCount());
        Map<String, Object> m;

        for (int i = 0; i < calendarItems[currentCalendar].getItemCount(); i++) {
            m = new HashMap<String, Object> ();
            m.put(ATTRIBUTE_START, currentHours[i].getStart());
            m.put(ATTRIBUTE_END, currentHours[i].getEnd());
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_START, ATTRIBUTE_END,};
        int [] to = { R.id.time_start, R.id.time_end};

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item_time, from, to);

        timeList.setAdapter(sAdapter);
        timeList.setOnItemClickListener(onItemClickListener);

    }

    private void getData(){

        calendarItems = database.selectCalendar(-1);
        String[] calendars = new String[calendarItems.length];
        for (int i=0;i<calendars.length;i++){
            calendars[i] = calendarItems[i].getName();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.item_spinner,calendars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarSelector.setAdapter(spinnerAdapter);

        if (currentCalendar == -1) {
            int current = mPreferences.getInt(Space.PREF_EDITED_CALENDAR,-1);
            if (current == -1) current = mPreferences.getInt(Space.PREF_CURRENT_CALENDAR,0);
            else {
                SharedPreferences.Editor mEdit = mPreferences.edit();
                mEdit.putInt(Space.PREF_EDITED_CALENDAR,-1);
                mEdit.apply();
            }

            calendarSelector.setSelection(current);
            currentCalendar = current;
        }
        else {
            boolean flag = false;
            for (int i = 0; i < calendarItems.length; i++) {
                if (calendarItems[currentCalendar].getId() == calendarItems[i].getId()) {
                    calendarItems[currentCalendar] = calendarItems[i];
                    calendarSelector.setSelection(i);
                    flag = true;
                    break;
                }
            }
            if (!flag){
                calendarItems[currentCalendar] = calendarItems[0];
                SharedPreferences.Editor mEdit = mPreferences.edit();
                mEdit.putInt(Space.PREF_CURRENT_CALENDAR,0);
                mEdit.apply();
            }
        }
    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String start = currentHours[position].getStart();
            String end = currentHours[position].getEnd();

            askTime askTime = new askTime();
            askTime.setActivity(activity,position);
            askTime.show(getFragmentManager(),start,end);
        }
    };



    @Override
    public void editItemCount(int count) {
        calendarItems[currentCalendar].setItemCount(count);
        countView.setText(count+"");
        database.updateCalendar(calendarItems[currentCalendar]);
        update();
    }

    @Override
    public void editTime(int position, String start, String end) {
        Hour hour = currentHours[position];
        hour.setStart(start);
        hour.setEnd(end);
        if (hour.getId() > -1) database.updateHour(hour);
        else database.insertHour(hour);
        update();
    }

    @Override
    public void confirm(int position, boolean result) {
        if (result){
            database.deleteCalendar(calendarItems[position]);
            getData();
            update();
        }

    }

    @Override
    public void getName(int position, String result) {
        CalendarItem temp = new CalendarItem(calendarItems[position]);
        temp.setName(result);
        database.updateCalendar(temp);
        getData();
        update();
    }
}
