package com.ieefimov.unik.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ieefimov.unik.R;

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

    @Override // При первой установке приложения
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"Creating table...");

        // таблица основных записей
        db.execSQL("create table `Main` ("
                + "id integer primary key autoincrement,"
                + "Name text,"
                + "Room text,"
                + "Hour integer,"
                + "Day integer,"
                + "Week integer,"
                + "Calendar integer" + ");");

        // таблица настроек календаря
        db.execSQL("create table Calendars ("
                + "id integer primary key autoincrement,"
                + "Name text,"
                + "Items integer,"
                + "WeekCount integer);");

        // таблица настроек пар
        db.execSQL("create table `Items` ("
                + "id integer primary key autoincrement,"
                + "Start text,"
                + "End integer,"
                + "Calendar integer);");


        firstInserts(db);



    }

    private void firstInserts(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        CalendarItem item = new CalendarItem();
        int week = 0;
        if (item.isDifferentWeek()) week = 1;

        cv.put("Name",item.getName());
        cv.put("Items",item.getItemCount());
        cv.put("WeekCount",week);

        db.insert("Calendars",null,cv);
    }

    @Override // при несоответствии версии
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


    // Получаем календарь
    public  CalendarItem[] selectCalendar(int id){
        SQLiteDatabase db = getReadableDatabase();
        String selectCondition = "id = " + id;
        if (id==-1) selectCondition = null;
        Cursor reader = db.query("Calendars",null,selectCondition,null,null,null,null);
        // поиск по таблице с заданным условием

        if (reader.moveToFirst()){ // если найдена хотя бы одна запись
            int count = reader.getCount();
            int idIndex = reader.getColumnIndex("id");
            int nameIndex = reader.getColumnIndex("Name");
            int itemsIndex = reader.getColumnIndex("Items");
            int weekCountIndex = reader.getColumnIndex("WeekCount");

            int i = 0;
            CalendarItem[] items = new CalendarItem[count];
            do {
                CalendarItem temp = new CalendarItem();
                temp.setId(reader.getInt(idIndex));
                temp.setName(reader.getString(nameIndex));
                temp.setItemCount(reader.getInt(itemsIndex));
                if (reader.getInt(weekCountIndex) == 0) temp.setDifferentWeek(false);
                else temp.setDifferentWeek(true);

                items[i] = temp;
                i++;
            }
            while (reader.moveToNext());
            return  items;
        }
        else return null;
    }

    // новый календарь
    public  boolean insertCalendar(CalendarItem item){
        int week = 0;
        if (item.isDifferentWeek()) week = 1;
        SQLiteDatabase db = getReadableDatabase();
        String selectCondition = "(Name = ? and "
                + "Items = ? and "
                + "WeekCount = ?)";
        String[] values = {item.getName(),item.getItemCount()+"",week+""};
        Cursor reader = db.query("Calendars",null,selectCondition,values,null,null,null,null);

        // если уже существует запись с данными параметрами - выход
        if (!reader.moveToFirst()){
            db = getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("Name",item.getName());
            cv.put("Items",item.getItemCount());
            cv.put("WeekCount",week);

            db.insert("Calendars",null,cv);

            return true;
        }
        return false;
    }

    // обновить календарь
    public boolean updateCalendar(CalendarItem item){
        int week = 0;
        if (item.isDifferentWeek()) week = 1;

        String selectCondition = "id = " + item.getId();

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Name",item.getName());
        cv.put("Items",item.getItemCount());
        cv.put("WeekCount",week);

        db.update("Calendars",cv,selectCondition,null);
        return true;
    }

    // удалить календарь
    public boolean deleteCalendar(CalendarItem item){
        String selectCondition = "id = " + item.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Calendars",selectCondition,null);
        return true;
    }

}
