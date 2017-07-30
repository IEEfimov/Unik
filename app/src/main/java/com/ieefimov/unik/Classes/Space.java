package com.ieefimov.unik.Classes;

import android.support.v4.widget.DrawerLayout;

/**
 * Created by IEEfimov on 12.07.2017.
 */

public class Space {
    public static DrawerLayout mainDrawer;
    public static int stausBarHeight=0;

    public static final String FILE_EXTENSION = ".iee";
    public static final String APP_PREFERENCE = "settings.IEE";
    public static final String PREF_CURRENT_CALENDAR = "stng_1";
    public static final String PREF_EDITED_CALENDAR = "stng_2";

    public static String currentCalendar="isNull";
    public static String editedCalendar="isNull";


    public static interface OnCompleteListener{
        public final int ADD_CALENDAR = 1;
        public final int RENAME_CALENDAR = 2;
        public final int DELETE_CALENDAR = 3;
        public final int EDIT_ITEM_COUNT = 4;
        public final int EDIT_ITEM = 5;

        public abstract void addCalendar(String result);
        public abstract void renameCalendar(String result);
        //public abstract void deleteCalendar();
        public abstract void editItemCount(int count);
//        public abstract void editItemTime(Hour hour);
    }

    public static interface onChoiceAction {
        public abstract void choiceDone(int position,int result);
        public abstract void editItem(Item item);
    }

    public static interface editTimeDialog{
        public abstract void editTime(Hour hour);
    }
    public static interface DialogConfirm{
        public abstract void confirm(Hour hour);
    }


    public static interface mainDialogListener{
        public final int CHOISE_CALENDAR = 1;
        public abstract void choiseCalendar();


    }



}
