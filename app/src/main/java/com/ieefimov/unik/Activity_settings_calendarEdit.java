package com.ieefimov.unik;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Dialogs.askConfirm;
import com.ieefimov.unik.Dialogs.askDigit;
import com.ieefimov.unik.Dialogs.askName;
import com.ieefimov.unik.Dialogs.askTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_settings_calendarEdit extends AppCompatActivity implements Space.OnCompleteListener {

    Spinner calendarSelector;
    LinearLayout diffrentWeekLayout;
    LinearLayout itemCountLayout;
    CheckBox differentWeekChkBx;
    TextView countView_old;
    TextView countView;
    ListView timeList;

    ////////////////

    CalendarItem[] calendarItems;
    String calendars[];
    String[] time_start;
    String[] time_end;;

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
        diffrentWeekLayout = (LinearLayout) findViewById(R.id.diffrentWeekLayout);
        itemCountLayout = (LinearLayout) findViewById(R.id.itemCountLayout);
        differentWeekChkBx = (CheckBox) findViewById(R.id.diffrentWeek);
        countView_old = (TextView) findViewById(R.id.seekValue);
        countView = (TextView) findViewById(R.id.item_count);
        timeList = (ListView) findViewById(R.id.timeList);


        diffrentWeekLayout.setOnClickListener(linearOnClick);
        itemCountLayout.setOnClickListener(linearOnClick);
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
            case R.id.action_add:
                askName.setActivity(this,Space.OnCompleteListener.ADD_CALENDAR);
                askName.show(getFragmentManager(),"Новый календарь");
                break;
            case R.id.action_Rename:
                askName.setActivity(this,Space.OnCompleteListener.RENAME_CALENDAR);
                askName.show(getFragmentManager(),currentCalendar.getName());
                break;
            case R.id.action_delete:
                askConfirm.setActivity(this,Space.OnCompleteListener.DELETE_CALENDAR);
                askConfirm.show(getFragmentManager(),currentCalendar.getName());
                break;
            case R.id.action_crypt:
                Toast.makeText(this,"Обновите до PRO",Toast.LENGTH_SHORT);
                break;
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
        Hour[] tempHours = database.selectHour(currentCalendar);

        currentHours = new Hour[currentCalendar.getItemCount()];
        for (int i=0;i<currentHours.length;i++){
            if (tempHours!=null && tempHours.length>i) currentHours[i] = tempHours[i];
            else {
                currentHours[i] = new Hour();
                currentHours[i].setCalendar(currentCalendar.getId());
            }
        }

//        if (tempHours == null){
//            currentHours = new Hour[currentCalendar.getItemCount()];
//            for (int i=0;i<currentCalendar.getItemCount();i++){
//                currentHours[i] = new Hour();
//                currentHours[i].setCalendar(currentCalendar.getId());
//            }
//        }else {
//            if (tempHours.length<currentCalendar.getItemCount()){
//                currentHours = new Hour[currentCalendar.getItemCount()];
//                for (int i=0;i<currentCalendar.getItemCount();i++){
//                    currentHours[i] = new Hour();
//                    currentHours[i].setCalendar(currentCalendar.getId());
//                }
//            }
//        }

        time_start = new String[currentHours.length];
        time_end = new String[currentHours.length];

        for (int i=0;i<time_start.length;i++){
            time_start[i] = currentHours[i].getStart();
            time_end[i] = currentHours[i].getEnd();
        }

//        if (currentHours != null) {
//            time_start = new String[currentHours.length];
//            time_end = new String[currentHours.length];
//        }else {
//            time_start = new String[0];
//            time_end = new String[0];
//        }



        differentWeekChkBx.setChecked(currentCalendar.isDifferentWeek());
        countView.setText(currentCalendar.getItemCount()+"");

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(currentCalendar.getItemCount());
        Map<String, Object> m;

        for (int i = 0; i < currentCalendar.getItemCount(); i++) {
//            String temp1 = "00:00";
//            String temp2 = "00:00";
//            if (time_start.length > i) temp1 = time_start[i];
//            if (time_end.length > i)   temp2 = time_end[i];



            m = new HashMap<String, Object> ();
            m.put(ATTRIBUTE_START, time_start[i]);
            m.put(ATTRIBUTE_END, time_end[i]);
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_START, ATTRIBUTE_END,};
        int [] to = { R.id.time_start, R.id.time_end};

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.time_edit_item, from, to);

        timeList.setAdapter(sAdapter);
        timeList.setOnItemClickListener(onItemClickListener);

    }

    private void getData(){

        calendarItems = database.selectCalendar(-1);
        calendars = new String[calendarItems.length];
        for (int i=0;i<calendars.length;i++){
            calendars[i] = calendarItems[i].getName();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,calendars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarSelector.setAdapter(spinnerAdapter);

        if (currentCalendar == null) currentCalendar = calendarItems[0];
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


    EditText.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            String temp = (String) v.getText();
            if (temp.length()==2) v.setText(temp+":");
            return false;
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
    public void editItem(Hour hour) {
        if (hour.getId()!= -1) database.updateHour(hour);
        else database.insertHour(hour);
        update();
    }

    @Override
    public void addItem(Hour hour) {
        database.insertHour(hour);
        update();
    }


}
