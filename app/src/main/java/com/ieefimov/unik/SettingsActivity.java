package com.ieefimov.unik;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ieefimov.unik.classes.Space;
import com.ieefimov.unik.settings.Activity_backups;
import com.ieefimov.unik.settings.Activity_calendarList;
import com.ieefimov.unik.settings.Activity_itemsEdit;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout calendarListBtn;
    LinearLayout itemListBtn;
    LinearLayout backupBtn;
    LinearLayout animationBtn;
    LinearLayout keyboardBtn;
    LinearLayout showCalendarBtn;

    CheckBox animationCheck;
    CheckBox autoKeyCheck;
    CheckBox showCalendarCheck;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);  // Добавляем тулбар

        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = Space.stausBarHeight;   //                  для разных устройств
        kostil.setLayoutParams(params);

        calendarListBtn = (LinearLayout) findViewById(R.id.settings_calendarListBtn);
        itemListBtn = (LinearLayout) findViewById(R.id.settings_ItemListBtn);
        backupBtn = (LinearLayout) findViewById(R.id.settings_backupLayout);
        animationBtn = (LinearLayout) findViewById(R.id.showAnimationLay);
        keyboardBtn = (LinearLayout) findViewById(R.id.autoKeyboardLay);
        showCalendarBtn = (LinearLayout) findViewById(R.id.showCalendarLay);

        animationCheck = (CheckBox) findViewById(R.id.animationChk);
        autoKeyCheck = (CheckBox) findViewById(R.id.autoKeyChk);
        showCalendarCheck = (CheckBox) findViewById(R.id.showCalendarChk);

        animationCheck.setOnCheckedChangeListener(onCheckedChangeListener);
        autoKeyCheck.setOnCheckedChangeListener(onCheckedChangeListener);
        showCalendarCheck.setOnCheckedChangeListener(onCheckedChangeListener);

        calendarListBtn.setOnClickListener(onClickListener);
        itemListBtn.setOnClickListener(onClickListener);
        backupBtn.setOnClickListener(onClickListener);
        animationBtn.setOnClickListener(onClickListener);
        keyboardBtn.setOnClickListener(onClickListener);
        showCalendarBtn.setOnClickListener(onClickListener);

        mPreferences = getSharedPreferences(Space.APP_PREFERENCE,MODE_PRIVATE);

        animationCheck.setChecked(mPreferences.getBoolean(Space.PREF_SETTINGS_ANIMATION,true));
        autoKeyCheck.setChecked(mPreferences.getBoolean(Space.PREF_SETTINGS_KEYBOARD,true));
        showCalendarCheck.setChecked(mPreferences.getBoolean(Space.PREF_SETTINGS_CALENDAR,true));

    }


    @Override
    public void finish() {
        Space.mainDrawer.closeDrawer(Gravity.LEFT,false);
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

    public LinearLayout.OnClickListener onClickListener = new LinearLayout.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.settings_calendarListBtn:
                    intent = new Intent(getApplicationContext(),Activity_calendarList.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        ActivityOptions options =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                        startActivity(intent,options.toBundle());
                    }
                    else startActivity(intent);
                    break;
                case R.id.settings_ItemListBtn:
                    intent = new Intent(getApplicationContext(),Activity_itemsEdit.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        ActivityOptions options =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                        startActivity(intent,options.toBundle());
                    }
                    else startActivity(intent);
                    break;
                case R.id.settings_backupLayout:
                    intent = new Intent(getApplicationContext(),Activity_backups.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        ActivityOptions options =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                        startActivity(intent,options.toBundle());
                    }
                    else startActivity(intent);
                    break;
                case R.id.showAnimationLay:
                    animationCheck.setChecked(!animationCheck.isChecked());
                    break;
                case R.id.autoKeyboardLay:
                    autoKeyCheck.setChecked(!autoKeyCheck.isChecked());
                    break;
                case R.id.showCalendarLay:
                    showCalendarCheck.setChecked(!showCalendarCheck.isChecked());
                    break;

            }
        }
    };

    CheckBox.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mEditor = mPreferences.edit();
            switch (buttonView.getId()){
                case R.id.animationChk:
                    mEditor.putBoolean(Space.PREF_SETTINGS_ANIMATION,animationCheck.isChecked());
                    break;
                case R.id.autoKeyChk:
                    mEditor.putBoolean(Space.PREF_SETTINGS_KEYBOARD,autoKeyCheck.isChecked());
                    break;
                case R.id.showCalendarChk:
                    mEditor.putBoolean(Space.PREF_SETTINGS_CALENDAR,showCalendarCheck.isChecked());
                    break;

            }
            mEditor.apply();
        }
    };
}
