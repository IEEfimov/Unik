package com.example.ieefimov.unik.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ieefimov.unik.R;

import static android.content.ContentValues.TAG;

/**
 * Created by IEEfimov on 12.07.2017.
 */

public class ConnectorDB extends SQLiteOpenHelper {
    Context context = null;

    public ConnectorDB(Context context, int version) {
        super(context,context.getResources().getString(R.string.app_BD_name) , null, version);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"Creating table...");

        db.execSQL("create table `Main table` ("
                + "id integer primary key autoincrement,"
                + "Name text,"
                + "Room text,"
                + "Hour integer,"
                + "Day integer,"
                + "Week integer,"
                + "Calendar integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // получаем массив пар в заданный день
    public  Item[] select(int day, int week, int calendar){
        SQLiteDatabase db = getReadableDatabase();
        String selectCondition = "Day = " + day
                + "Week = " + week
                + "Calendar = " + calendar;
        Cursor reader = db.query("Main table",null,selectCondition,null,null,null,null);
            // поиск по таблице с заданным условием

        if (reader.moveToFirst()){ // если найдена хотя бы одна запись
            int count = reader.getCount();
            int nameIndex = reader.getColumnIndex("Name");
            int roomIndex = reader.getColumnIndex("Room");
            int hourIndex = reader.getColumnIndex("Hour");
            int dayIndex = reader.getColumnIndex("Day");
            int weekIndex = reader.getColumnIndex("Week");
            int calendarIndex = reader.getColumnIndex("Calendar");
            int i = 0;
            Item[] items = new Item[count];
            do {
                Item temp = new Item();
                temp.setName(reader.getString(nameIndex));
                temp.setRoom(reader.getString(roomIndex));
                temp.setHour(reader.getInt(hourIndex));
                temp.setDay(reader.getInt(dayIndex));
                temp.setWeek(reader.getInt(weekIndex));
                temp.setCalendar(reader.getInt(calendarIndex));

                items[i] = temp;
                i++;
            }
            while (reader.moveToNext());
            return  items;
        }
        else return null;
    }

    // новая запись в таблице Main
    public  boolean insert(Item item){
        SQLiteDatabase db = getReadableDatabase();
        String selectCondition = "Day = " + item.getDay()
                + "Week = " + item.getWeek()
                + "Hour = " + item.getHour()
                + "Calendar = " + item.getCalendar();
        Cursor reader = db.query("Main table",null,selectCondition,null,null,null,null);
        // если уже существует запись с данными параметрами - выход
        if (!reader.moveToFirst()){
            db = getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("Name",item.getName());
            cv.put("Room",item.getRoom());
            cv.put("Hour",item.getHour());
            cv.put("Day",item.getDay());
            cv.put("Week",item.getWeek());
            cv.put("Calendar",item.getCalendar());

            db.insert("Main table",null,cv);
            return true;
        }
        return false;
    }


}
