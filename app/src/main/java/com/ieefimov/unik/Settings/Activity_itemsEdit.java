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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ieefimov.unik.Classes.CalendarItem;
import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.Hour;
import com.ieefimov.unik.Classes.Item;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Dialogs.askAction;
import com.ieefimov.unik.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_itemsEdit extends AppCompatActivity implements Space.itemsEditListener{

    Spinner calendarSelector;
    ToggleButton daySelect[];
    ToggleButton weekSelect[];
    LinearLayout weekLayout;
    ListView itemList;


    CalendarItem[] calendarItems;
    String calendars[];

    //////////////////////

    CalendarItem currentCalendar;
    Hour[] currentHours;
    Item[] currentItems;
    ConnectorDB database;



    Activity activity;

    //////////////////////////

    final String ATTRIBUTE_START = "start";
    final String ATTRIBUTE_END = "end";
    final String ATTRIBUTE_ACTION = "action";
    final String ATTRIBUTE_ROOM = "room";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_items_edit);

        database = new ConnectorDB(this,1); // подключение к БД.

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);  // Добавляем тулбар

        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = Space.stausBarHeight;   //                  для разных устройств
        kostil.setLayoutParams(params);

        calendarSelector = (Spinner) findViewById(R.id.sett_cal_spinner);
        itemList = (ListView) findViewById(R.id.itemList);


        daySelect = new ToggleButton[7];
        daySelect[0] = (ToggleButton) findViewById(R.id.w1);
        daySelect[1] = (ToggleButton) findViewById(R.id.w2);
        daySelect[2] = (ToggleButton) findViewById(R.id.w3);
        daySelect[3] = (ToggleButton) findViewById(R.id.w4);
        daySelect[4] = (ToggleButton) findViewById(R.id.w5);
        daySelect[5] = (ToggleButton) findViewById(R.id.w6);
        daySelect[6] = (ToggleButton) findViewById(R.id.w7);
        daySelect[0].setChecked(true);

        weekSelect = new ToggleButton[2];
        weekSelect[0] = (ToggleButton) findViewById(R.id.week1);
        weekSelect[1] = (ToggleButton) findViewById(R.id.week2);
        weekSelect[0].setChecked(true);

        weekLayout = (LinearLayout) findViewById(R.id.weekLayout);
        calendarSelector = (Spinner) findViewById(R.id.sett_cal_spinner);


        calendarSelector.setOnItemSelectedListener(spinnerOnClick);

        for (int i=0;i<daySelect.length;i++) {
            daySelect[i].setOnClickListener(onClickListenerToggle);
        }
        for (int i=0;i<weekSelect.length;i++) {
            weekSelect[i].setOnClickListener(onClickListenerWeek);
        }

        activity = this;

        getData();
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_items_edit_tool,menu);
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

    private void update(){
        int day = 0;
        int week = 0;
        for (int i = 0; i <daySelect.length ; i++) {
            if (daySelect[i].isChecked()){
                day = i;
                break;
            }
        }
        for (int i = 0; i <weekSelect.length ; i++) {
            if (weekSelect[i].isChecked()){
                week = i;
                break;
            }
        }
        if (!currentCalendar.isDifferentWeek()){
            weekSelect[0].setChecked(true);
            weekSelect[1].setChecked(false);
            weekLayout.setVisibility(View.GONE);
        }
        else weekLayout.setVisibility(View.VISIBLE);

        ////////////////////////////////////////////////

        currentHours = database.selectHour(currentCalendar);
        //================
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

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item_action, from, to);

        itemList.setAdapter(sAdapter);
        itemList.setOnItemClickListener(onItemClickListener);

    }

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

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            askAction askAction = new askAction();
            askAction.setActivity(activity);
            askAction.show(getFragmentManager(),currentItems[position]);
        }
    };

    ToggleButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    };

    ToggleButton.OnClickListener onClickListenerToggle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (ToggleButton aDaySelect : daySelect) {
                aDaySelect.setChecked(false);
            }
            ToggleButton btn = (ToggleButton) v;
            btn.setChecked(true);
            update();
        }
    };

    ToggleButton.OnClickListener onClickListenerWeek = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (ToggleButton aWeekSelect : weekSelect) {
                aWeekSelect.setChecked(false);
            }
            ToggleButton btn = (ToggleButton) v;
            btn.setChecked(true);
            update();
        }
    };


    @Override
    public void editItem(Item item) {
        if (item.getHour() == -1){

        }
        if (item.getId()!= -1) database.updateItem(item);
        else database.insertItem(item);
        update();
    }
}
