package com.ieefimov.unik.Settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ieefimov.unik.Classes.ConnectorDB;
import com.ieefimov.unik.Classes.Hour;
import com.ieefimov.unik.Classes.SaveItem;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Dialogs.askName;
import com.ieefimov.unik.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_backups extends AppCompatActivity implements Space.OnCompleteListener{

    ListView backupsList;
    Activity activity;

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

        database = new ConnectorDB(this,1);
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



        dir = getApplicationInfo().dataDir;
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

    @Override
    public void addCalendar(String result) {}

    @Override
    public void renameCalendar(String result) {
        try {
            String dir = getApplicationInfo().dataDir;
            FileInputStream fileIn = new FileInputStream(dir+ "/" + currentFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            SaveItem restored = (SaveItem) in.readObject();
            restored.getCalendar().setName(result);
            database.writeSaveData(restored);
            in.close();
            fileIn.close();
            Toast.makeText(activity, "Готово", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCalendar() {}

    @Override
    public void editItemCount(int count) {}

    @Override
    public void editItemTime(Hour hour) {}
}