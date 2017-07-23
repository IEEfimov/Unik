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

        public abstract void addCalendar(String result);
        public abstract void renameCalendar(String result);
        public abstract void deleteCalendar();
    }


}
