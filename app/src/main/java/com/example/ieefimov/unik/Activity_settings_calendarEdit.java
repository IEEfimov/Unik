package com.example.ieefimov.unik;

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
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ieefimov.unik.Classes.Space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_settings_calendarEdit extends AppCompatActivity {

    Spinner calendarSelector;
    LinearLayout diffrentWeekLayout;
    LinearLayout itemCountLayout;
    CheckBox diffrentWeekChkBx;
    SeekBar itemCount;
    TextView countView_old;
    TextView countView;
    ListView timeList;


    int count = 3;

    String[] time_start = {"08:00","09:30","11:10"};
    String[] time_end = {"09:20","10:50","12:30"};;



    String calendars[] = {"IEEfimov","Unki","Добавить календарь..."};

    final String ATTRIBUTE_START = "start";
    final String ATTRIBUTE_END = "end";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        diffrentWeekChkBx = (CheckBox) findViewById(R.id.diffrentWeek);
        itemCount = (SeekBar) findViewById(R.id.seekBar);
        countView_old = (TextView) findViewById(R.id.seekValue);
        countView = (TextView) findViewById(R.id.item_count);
        timeList = (ListView) findViewById(R.id.timeList);


        itemCount.setOnSeekBarChangeListener(onSeekBarChangeListener);
        diffrentWeekLayout.setOnClickListener(linearOnClick);
        itemCountLayout.setOnClickListener(linearOnClick);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,calendars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarSelector.setAdapter(spinnerAdapter);
        update();

        countView.setText(count+"");

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
        }
        return true;
    }

    LinearLayout.OnClickListener linearOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.diffrentWeekLayout:
                    diffrentWeekChkBx.setChecked(!diffrentWeekChkBx.isChecked());
                    break;
                case R.id.itemCountLayout:

                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            countView_old.setText(""+(progress+1));
            update();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private void update(){
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(count);
        Map<String, Object> m;

        for (int i = 0; i < count; i++) {
            String temp1 = "";
            String temp2 = "";
            if (time_start.length > i) temp1 = time_start[i];
            if (time_end.length > i)   temp2 = time_end[i];

            m = new HashMap<String, Object> ();
            m.put(ATTRIBUTE_START, temp1);
            m.put(ATTRIBUTE_END, temp2);
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_START, ATTRIBUTE_END,};
        int [] to = { R.id.time_start, R.id.time_end};

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.time_edit_item, from, to);

        timeList.setAdapter(sAdapter);
        timeList.setOnItemClickListener(onItemClickListener);

    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EditText temp = (EditText) view;
            temp.setText("16:30");
            temp.setOnEditorActionListener(onEditorActionListener);
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
}
