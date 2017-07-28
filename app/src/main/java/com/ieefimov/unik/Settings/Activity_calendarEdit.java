package com.ieefimov.unik.Settings;

import android.app.Activity;
import android.content.SharedPreferences;
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

import com.ieefimov.unik.Classes.CalendarItem;
import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.Hour;
import com.ieefimov.unik.Classes.SaveItem;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Dialogs.askConfirm;
import com.ieefimov.unik.Dialogs.askDigit;
import com.ieefimov.unik.Dialogs.askName;
import com.ieefimov.unik.Dialogs.askTime;
import com.ieefimov.unik.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_calendarEdit extends AppCompatActivity implements Space.OnCompleteListener {

    Spinner calendarSelector;
    LinearLayout differentWeekLayout;
    LinearLayout itemCountLayout;
    LinearLayout setNameLayout;
    CheckBox differentWeekChkBx;
    TextView countView;
    TextView nameView;
    ListView timeList;

    ////////////////

    CalendarItem[] calendarItems;
    String calendars[];

    //////////////////////

    CalendarItem currentCalendar;
    Hour[] currentHours;
    ConnectorDB database;

    Activity activity;


    final String ATTRIBUTE_START = "start";
    final String ATTRIBUTE_END = "end";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new ConnectorDB(this,1); // подключение к БД.

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
        askName askName = new askName();
        askConfirm askConfirm = new askConfirm();
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.action_delete:
                if (calendarItems.length < 2){
                    Toast.makeText(activity, "Нельзя удалить единственный календарь", Toast.LENGTH_SHORT).show();
                    break;
                }
                askConfirm.setActivity(this,Space.OnCompleteListener.DELETE_CALENDAR);
                askConfirm.show(getFragmentManager(),currentCalendar.getName());

                break;
            case R.id.action_crypt:
                Toast.makeText(this,"Обновите до PRO",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_save:
                saveCurrent();
        }
        return true;
    }

    private boolean saveCurrent(){
        try {
            String dir = getApplicationInfo().dataDir;
            String name = currentCalendar.getId()+"_"+currentCalendar.getName()+".iee";
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
        return true;
    }



    LinearLayout.OnClickListener linearOnClick = new View.OnClickListener() {
        //TODO: Обработчик элемента время
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.diffrentWeekLayout:
                    differentWeekChkBx.setChecked(!differentWeekChkBx.isChecked());
                    currentCalendar.setDifferentWeek(differentWeekChkBx.isChecked());
                    database.updateCalendar(currentCalendar);
                    break;
                case R.id.itemCountLayout:
                    askDigit ask = new askDigit();
                    ask.setActivity(activity,Space.OnCompleteListener.EDIT_ITEM_COUNT);
                    ask.show(getFragmentManager(),(currentCalendar.getItemCount()+""));
                    break;
                case R.id.nameLinear:
                    askName askName = new askName();
                    askName.setActivity(activity,Space.OnCompleteListener.RENAME_CALENDAR);
                    askName.show(getFragmentManager(),currentCalendar.getName());
                    break;

            }
        }
    };

    Spinner.OnItemSelectedListener spinnerOnClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try{
                currentCalendar = calendarItems[position];
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
        currentHours = database.selectHour(currentCalendar);
        differentWeekChkBx.setChecked(currentCalendar.isDifferentWeek());
        countView.setText(currentCalendar.getItemCount()+"");
        nameView.setText(currentCalendar.getName());

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(currentCalendar.getItemCount());
        Map<String, Object> m;

        for (int i = 0; i < currentCalendar.getItemCount(); i++) {
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
        calendars = new String[calendarItems.length];
        for (int i=0;i<calendars.length;i++){
            calendars[i] = calendarItems[i].getName();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.item_spinner,calendars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarSelector.setAdapter(spinnerAdapter);

        if (currentCalendar == null) {
            SharedPreferences mPreferences;
            mPreferences = getSharedPreferences(Space.APP_PREFERENCE,MODE_PRIVATE);
            int current = mPreferences.getInt(Space.PREF_CURRENT_CALENDAR,0);
            calendarSelector.setSelection(current);
            currentCalendar = calendarItems[current];
        }
        else {
            boolean flag = false;
            for (int i = 0; i < calendarItems.length; i++) {
                if (currentCalendar.getId() == calendarItems[i].getId()) {
                    currentCalendar = calendarItems[i];
                    calendarSelector.setSelection(i);
                    flag = true;
                    break;
                }
            }
            if (!flag) currentCalendar = calendarItems[0];

        }

    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            askTime askTime = new askTime();
            askTime.setActivity(activity,Space.OnCompleteListener.EDIT_ITEM);
            askTime.show(getFragmentManager(),currentHours[position]);
        }
    };



    @Override
    public void addCalendar(String result) {
        CalendarItem temp = new CalendarItem(0,result,3,false);
        long newId = database.insertCalendar(temp);
        temp.setId(newId);
        currentCalendar = temp;
        getData();
        update();
    }

    @Override
    public void renameCalendar(String result) {
        CalendarItem temp = new CalendarItem(currentCalendar);
        temp.setName(result);
        database.updateCalendar(temp);
        getData();
        update();
    }

    @Override
    public void deleteCalendar() {
        database.deleteCalendar(currentCalendar);
        getData();
        update();
    }

    @Override
    public void editItemCount(int count) {
        currentCalendar.setItemCount(count);
        countView.setText(count+"");
        database.updateCalendar(currentCalendar);
        update();
    }

    @Override
    public void editItemTime(Hour hour) {
        if (hour.getId() > -1) database.updateHour(hour);
        else database.insertHour(hour);
        update();
    }

}
