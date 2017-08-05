package com.ieefimov.unik.settings;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ieefimov.unik.R;
import com.ieefimov.unik.classes.CalendarItem;
import com.ieefimov.unik.classes.ConnectorDB;
import com.ieefimov.unik.classes.Hour;
import com.ieefimov.unik.classes.Item;
import com.ieefimov.unik.classes.Space;
import com.ieefimov.unik.dialogs.askAction;
import com.ieefimov.unik.dialogs.askName;
import com.ieefimov.unik.dialogs.askTime;
import com.ieefimov.unik.dialogs.choiceAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_itemsEdit extends AppCompatActivity implements Space.DialogChoiceAction,
        Space.DialogName,Space.DialogTimeEdit{

    Spinner calendarSelector;
    ToggleButton daySelect[];
    ToggleButton weekSelect[];
    LinearLayout weekLayout;
    ListView itemList;


    CalendarItem[] calendarItems;
    int currentCalendar=-1;

    int choiseTodo = -1;
    //String calendars[];

    //////////////////////


    Hour[] currentHours;
    Item[] currentItems;
    ConnectorDB database;

    SharedPreferences mPreferences;


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

        mPreferences = getSharedPreferences(Space.APP_PREFERENCE,MODE_PRIVATE);

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
            case R.id.action_edit:
                SharedPreferences.Editor mEdit = mPreferences.edit();
                mEdit.putInt(Space.PREF_EDITED_CALENDAR,3);
                Intent intent = new Intent(getApplicationContext(),Activity_calendarEdit.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                    startActivity(intent,options.toBundle());
                }
                else startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        update();
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

//        if (currentCalendar == null) {
//
//            int current = mPreferences.getInt(Space.PREF_CURRENT_CALENDAR,0);
//            calendarSelector.setSelection(current);
//            currentCalendar = calendarItems[current];
//        }
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
            if (!flag) calendarItems[currentCalendar] = calendarItems[0];
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
        if (!calendarItems[currentCalendar].isDifferentWeek()){
            weekSelect[0].setChecked(true);
            weekSelect[1].setChecked(false);
            weekLayout.setVisibility(View.GONE);
        }
        else weekLayout.setVisibility(View.VISIBLE);

        ////////////////////////////////////////////////

        currentHours = database.selectHour(calendarItems[currentCalendar]);
        //================
        currentItems = new Item[calendarItems[currentCalendar].getItemCount()];
        Item[] tempItems = database.selectItems(day,week,calendarItems[currentCalendar].getId());

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
                currentItems[i].setCalendar(calendarItems[currentCalendar].getId());
                currentItems[i].setHour(currentHours[i].getId());
                currentItems[i].setWeek(week);
                currentItems[i].setName(getResources().getString(R.string.dialog_editItem_defaultName));
                currentItems[i].setRoom(getResources().getString(R.string.dialog_editItem_defaultRoom));
            }
        }

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(calendarItems[currentCalendar].getItemCount());
        Map<String, Object> m;

        for (int i = 0; i < calendarItems[currentCalendar].getItemCount(); i++) {
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
        itemList.setOnItemLongClickListener(onItemLongClickListener);

    }

    Spinner.OnItemSelectedListener spinnerOnClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try{
                calendarItems[currentCalendar] = calendarItems[position];
                SharedPreferences.Editor mEditor = mPreferences.edit();
                mEditor.putInt(Space.PREF_EDITED_CALENDAR,position);
                mEditor.apply();
                update();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Ошибочка вышла :(",Toast.LENGTH_LONG).show();
                // TODO: 30.07.2017 ! Явно заданные строки
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

    ListView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            String actions[] = new String[3];
            actions[0] = "Изменить название";
            actions[1] = "Изменить кабинет";
            actions[2] = "Редактировать время";
            // TODO: 30.07.2017 ! Явно заданные строки
            choiceAction askAction = new choiceAction();
            askAction.setActivity(activity,actions,position);
            askAction.show(getFragmentManager(),"Выберите действие:");
            return false;
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

    @Override
    public void choiceDone(int position,int result) {
        askName askName = new askName();
        switch (result){
            case 0: // изменить название
                choiseTodo = 1;
                askName.setActivity(activity,position);
                askName.show(getFragmentManager(),"Введите название:","",currentItems[position].getName());
                break;
            case 1: // изменить кабинет
                choiseTodo = 2;
                askName.setActivity(activity,position);
                askName.show(getFragmentManager(),"Введите номер кабинета:","",currentItems[position].getRoom());
                break;
            case 2: // редактировать время
                askTime ask = new askTime();
                ask.setActivity(activity,position);
                ask.show(getFragmentManager(),currentHours[position].getStart(),currentHours[position].getEnd());
                break;
        }
    }

    @Override
    public void getName(int position, String result) {
        switch (choiseTodo){
            case 1:
                currentItems[position].setName(result);
                break;
            case 2:
                currentItems[position].setRoom(result);
                break;
        }
        choiseTodo = -1;
        if (currentItems[position].getId() >= 0) database.updateItem(currentItems[position]);
        else database.insertItem(currentItems[position]);
        update();
    }

    @Override
    public void editTime(int position, String start, String end) {
        currentHours[position].setStart(start);
        currentHours[position].setEnd(end);
        database.updateHour(currentHours[position]);
        if (currentHours[position].getId() > -1) database.updateHour(currentHours[position]);
        else database.insertHour(currentHours[position]);
        update();
    }
}
