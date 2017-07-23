package com.ieefimov.unik.Classes;

import android.support.v4.widget.DrawerLayout;

/**
 * Created by IEEfimov on 12.07.2017.
 */

public class Space {
    public static DrawerLayout mainDrawer;
    public static int stausBarHeight=0;

    public static interface OnCompleteListener{
        public final int ADD_CALENDAR = 1;
        public final int RENAME_CALENDAR = 2;
        public final int DELETE_CALENDAR = 3;
        public final int EDIT_ITEM_COUNT = 4;
        public final int EDIT_ITEM = 5;

        public abstract void addCalendar(String result);
        public abstract void renameCalendar(String result);
        public abstract void deleteCalendar();
        public abstract void editItemCount(int count);
        public abstract void editItem(int num,String result);
    }


}
