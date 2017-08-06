package com.ieefimov.unik.settings;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ieefimov.unik.R;
import com.ieefimov.unik.classes.CalendarItem;
import com.ieefimov.unik.classes.ConnectorDB;
import com.ieefimov.unik.classes.Space;
import com.ieefimov.unik.dialogs.askCalendar;
import com.ieefimov.unik.dialogs.askName;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_backups extends AppCompatActivity implements Space.OnCompleteListener,
            Space.DialogChoiceCalendar,Space.DialogName{

    ListView backupsList;
    Activity activity;
    LinearLayout addNew;

    ConnectorDB database;

    ////////////////////////////////////////////////////

    final String ATTRIBUTE_NAME = "name";
    String dir = "";

    String[] files;
    String currentFile;

    // TODO: 26.07.2017 Почитать про атрибуты файла, мб прикрутить автора, дату и пр.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backups);

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);  // Добавляем тулбар

        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = Space.stausBarHeight;   //                  для разных устройств
        kostil.setLayoutParams(params);

        backupsList = (ListView) findViewById(R.id.backupsList);
        backupsList.setOnItemClickListener(onItemClickListener);

        addNew = (LinearLayout) findViewById(R.id.addNewCalendar);
        addNew.setOnClickListener(onLinearClick);

        database = new ConnectorDB(this);
        activity = this;
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_backups_tool,menu);
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
        dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        dir += "/Android/data/com.ieefimov.unik/files";
        dir += "/saved";
        File file = new File(dir);
        if (!file.exists()){
            file.mkdirs();
        }
        files = getFiles();

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(files.length);
        Map<String, Object> m;

        for (int i = 0; i < files.length; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME, files[i]);
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME};
        int [] to = { R.id.file};

        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item_backup, from, to);

        backupsList.setAdapter(sAdapter);
        backupsList.setOnItemClickListener(onItemClickListener);

    }

    private String[] getFiles(){
        File directory = new File(dir);
        File files[] = directory.listFiles();
        String result[] = new String[files.length];
        ArrayList<String > res = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            String temp = files[i].getName();
            String extension="";
            for (int j = 0; j < temp.length(); j++) {
                extension += temp.charAt(j);
                if (temp.charAt(j)=='.') extension = ".";
            }
            if (extension.equalsIgnoreCase(Space.FILE_EXTENSION))
            res.add(files[i].getName());

        }
        return res.toArray(new String[res.size()]);
    }

    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            askName askAction = new askName();
            currentFile = files[position];
            askAction.setActivity(activity,Space.OnCompleteListener.RENAME_CALENDAR);
            askAction.show(getFragmentManager(),currentFile);
        }
    };

    LinearLayout.OnClickListener onLinearClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            askCalendar asker = new askCalendar();
            asker.setActivity(activity,0);
            asker.show(getFragmentManager(),"tag");
        }
    };



    @Override
    public void editItemCount(int count) {}


    @Override
    public void choiceCalendar() {

    }

    @Override
    public void retCalendar(int index) {

        CalendarItem current = database.selectCalendar(-1)[index];
        database.SaveCalendar(current,activity);
        getData();
    }

    @Override
    public void getName(int position, String result) {

    }
}
