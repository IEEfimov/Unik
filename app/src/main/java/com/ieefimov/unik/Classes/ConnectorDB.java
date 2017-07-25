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

    final String TABLE_ITEMS = "Main";
    final String ROW_ITEMS_NAME = "Name";
    final String ROW_ITEMS_ROOM = "Room";
    final String ROW_ITEMS_HOUR = "Hour";
    final String ROW_ITEMS_DAY = "Day";
    final String ROW_ITEMS_WEEK = "Week";
    final String ROW_ITEMS_CALENDAR = "Calendar";

    final String TABLE_CALENDARS = "Calendars";
    final String ROW_CALENDARS_NAME = "Name";
    final String ROW_CALENDARS_ITEMS_COUNT = "Items";
    final String ROW_CALENDARS_WEEKS_COUNT = "WeekCount";

    final String TABLE_HOURS = "Items";
    final String ROW_HOURS_START = "Start";
    final String ROW_HOURS_END = "End";
    final String ROW_HOURS_CALENDAR = "Calendar";

    final String TABLE_SUBJECTS = "Subjects";
    final String ROW_SUBJECTS_NAME = "Name";

    final String TABLE_HOMEWORK = "HomeWork";
    final String ROW_HOMEWORK_SUBJECT = "Subject";
    final String ROW_HOMEWORK_DATE_OF_ADD = "Date";
    final String ROW_HOMEWORK_MY_TEXT = "MyText";
    final String ROW_HOMEWORK_GROUP_TEXT = "GroupText";

    final String ROW_ID = "id";

    public ConnectorDB(Context context, int version) {
        super(context,context.getResources().getString(R.string.app_BD_name) , null, version);
        this.context = context;

    }

    @Override // При первой установке приложения
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"Creating table...");

        // таблица основных записей
        db.execSQL("create table "+ TABLE_ITEMS +" ("
                + "id integer primary key autoincrement,"
                + "Name text,"
                + "Room text,"
                + "Hour integer,"
                + "Day integer,"
                + "Week integer,"
                + "Calendar integer" + ");");

        // таблица настроек календаря
        db.execSQL("create table "+TABLE_CALENDARS+" ("
                + "id integer primary key autoincrement,"
                + "Name text,"
                + "Items integer,"
                + "WeekCount integer);");

        // таблица настроек пар
        db.execSQL("create table "+ TABLE_HOURS +" ("
                + "id integer primary key autoincrement,"
                + "Start text,"
                + "End integer,"
                + "Calendar integer);");

        // таблица предметов
        db.execSQL("create table "+ TABLE_SUBJECTS +" ("
                + "id integer primary key autoincrement,"
                +  ROW_SUBJECTS_NAME +" text);");


        db.execSQL("create table "+ TABLE_HOMEWORK +" ("
                + "id integer primary key autoincrement,"
                + ROW_HOMEWORK_SUBJECT+ " integer,"
                + ROW_HOMEWORK_DATE_OF_ADD+ " text,"
                + ROW_HOMEWORK_MY_TEXT+ " text,"
                + ROW_HOMEWORK_GROUP_TEXT+ " text);");

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

        db.insert(TABLE_CALENDARS,null,cv);
    }

    @Override // при несоответствии версии
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //=======================================================//


    public Item[] selectItems(int day, int week, long calendar){
        SQLiteDatabase db = getReadableDatabase();
//        // TODO: 25.07.2017 Сделать аргументный ввод
//        String selectCondition = "Day = " + day
//                + "Week = " + week
//                + "Calendar = " + calendar;
//        Cursor reader = db.query(TABLE_ITEMS,null,selectCondition,null,null,null,null);
        String selectCondition = "("
                +ROW_ITEMS_DAY+" = ? and "
                +ROW_ITEMS_WEEK+" = ? and "
                +ROW_ITEMS_CALENDAR+" = ?)";
        String[] values = {day+"",week+"",calendar+""};
        Cursor reader = db.query(TABLE_ITEMS,null,selectCondition,values,null,null,null,null);
        // поиск по таблице с заданным условием

        if (reader.moveToFirst()){ // если найдена хотя бы одна запись
            int count = reader.getCount();
            int idIndex = reader.getColumnIndex(ROW_ID);
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
                temp.setId(reader.getInt(idIndex));
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

    public long insertItem(Item item){
        if (!item.isValid()) return -1;
        // не записывать если данные не правильные
        SQLiteDatabase db = getReadableDatabase();
        String selectCondition = "("
                +ROW_ITEMS_DAY+" = ? and "
                +ROW_ITEMS_WEEK+" = ? and "
                +ROW_ITEMS_CALENDAR+" = ?)";
        String[] values = {item.getDay()+"",item.getWeek()+"",item.getCalendar()+""};
        Cursor reader = db.query(TABLE_ITEMS,null,selectCondition,values,null,null,null,null);
        // если уже существует запись с данными параметрами - выход
        if (!reader.moveToFirst()){
            db = getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(ROW_ITEMS_NAME,item.getName());
            cv.put(ROW_ITEMS_ROOM,item.getRoom());
            cv.put(ROW_ITEMS_HOUR,item.getHour());
            cv.put(ROW_ITEMS_DAY,item.getDay());
            cv.put(ROW_ITEMS_WEEK,item.getWeek());
            cv.put(ROW_ITEMS_CALENDAR,item.getCalendar());

            long result = db.insert(TABLE_ITEMS,null,cv);

            return result;
        }
        return -1;
    }

    public boolean updateItem(Item item){
        if (!item.isValid()) return false;
        if (item.getId()<0) return false;
        // не изменять при поврежденных данных
        SQLiteDatabase db = getWritableDatabase();

        String selectCondition = "id = " + item.getId();
        ContentValues cv = new ContentValues();

        cv.put(ROW_ITEMS_NAME,item.getName());
        cv.put(ROW_ITEMS_ROOM,item.getRoom());
        cv.put(ROW_ITEMS_HOUR,item.getHour());
        cv.put(ROW_ITEMS_DAY,item.getDay());
        cv.put(ROW_ITEMS_WEEK,item.getWeek());
        cv.put(ROW_ITEMS_CALENDAR,item.getCalendar());

        db.update(TABLE_ITEMS,cv,selectCondition,null);
        return true;
    }

    public boolean deleteItem(Item item){
        if (item.getId()<0) return false;
        String selectCondition = "id = " + item.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ITEMS,selectCondition,null);
        return true;
    }


    //=======================================================//


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

    public  long insertCalendar(CalendarItem item){
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

            long result = db.insert("Calendars",null,cv);


            return result;
        }
        return -1;
    }

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

    public boolean deleteCalendar(CalendarItem item){
        String selectCondition = "id = " + item.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Calendars",selectCondition,null);
        return true;
    }


    //=======================================================//


    public Hour[] selectHour(CalendarItem calendar){
        SQLiteDatabase db = getReadableDatabase();
        String selectCondition = ROW_HOURS_CALENDAR+" = " + calendar.getId();
        Cursor reader = db.query(TABLE_HOURS,null,selectCondition,null,null,null,null);
        // поиск по таблице с заданным условием

        if (reader.moveToFirst()){ // если найдена хотя бы одна запись
            int count = reader.getCount();
            int idIndex = reader.getColumnIndex("id");
            int startIndex = reader.getColumnIndex(ROW_HOURS_START);
            int endIndex = reader.getColumnIndex(ROW_HOURS_END);
            int calendarIndex = reader.getColumnIndex(ROW_HOURS_CALENDAR);

            int i = 0;
            Hour[] items = new Hour[count];
            do {
                Hour temp = new Hour();
                temp.setId(reader.getInt(idIndex));
                temp.setStart(reader.getString(startIndex));
                temp.setEnd(reader.getString(endIndex));
                temp.setCalendar(reader.getInt(calendarIndex));

                items[i] = temp;
                i++;
            }
            while (reader.moveToNext());
            return  items;
        }
        else return null;
    }

    public long insertHour(Hour item){
        if (!item.isValid()) return -1;
        // не записывать если данные не правильные
        SQLiteDatabase db = getReadableDatabase();
        String selectCondition = "("
                +ROW_HOURS_START+" = ? and "
                +ROW_HOURS_END+" = ? and "
                +ROW_HOURS_CALENDAR+" = ?)";
        String[] values = {item.getStart(),item.getEnd(),item.getCalendar()+""};
        Cursor reader = db.query(TABLE_HOURS,null,selectCondition,values,null,null,null,null);

        // если уже существует запись с данными параметрами - выход
        if (!reader.moveToFirst()){
            db = getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(ROW_HOURS_START,item.getStart());
            cv.put(ROW_HOURS_END,item.getEnd());
            cv.put(ROW_HOURS_CALENDAR,item.getCalendar());

            long result = db.insert(TABLE_HOURS,null,cv);


            return result;
        }
        return -1;
    }

    public boolean updateHour(Hour item){
        if (!item.isValid()) return false;
        if (item.getId()<0) return false;
        // не изменять при поврежденных данных
        SQLiteDatabase db = getWritableDatabase();

        String selectCondition = "id = " + item.getId();
        ContentValues cv = new ContentValues();

        cv.put(ROW_HOURS_START,item.getStart());
        cv.put(ROW_HOURS_END,item.getEnd());
        cv.put(ROW_HOURS_CALENDAR,item.getCalendar());

        db.update(TABLE_HOURS,cv,selectCondition,null);
        return true;
    }

    public boolean deleteHour(Hour item){
        if (item.getId()<0) return false;
        String selectCondition = "id = " + item.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_HOURS,selectCondition,null);
        return true;
    }

}
