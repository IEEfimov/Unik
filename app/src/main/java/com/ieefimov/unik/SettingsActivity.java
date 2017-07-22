package com.ieefimov.unik;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ieefimov.unik.Classes.Space;

public class SettingsActivity extends AppCompatActivity {

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

        LinearLayout calendarListBtn = (LinearLayout) findViewById(R.id.settings_calendarListBtn);
        LinearLayout itemListBtn = (LinearLayout) findViewById(R.id.settings_ItemListBtn);

        calendarListBtn.setOnClickListener(onClickListener);
        itemListBtn.setOnClickListener(onClickListener);

    }


    @Override
    public void finish() {
        Space.mainDrawer.closeDrawer(Gravity.LEFT,false);
        super.finish();
        overridePendingTransition(R.anim.show_activity,R.anim.hide_activity);

    }

    public LinearLayout.OnClickListener onClickListener = new LinearLayout.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent;
            ActivityOptions options =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);

            switch (v.getId()){
                case R.id.settings_calendarListBtn:
                    intent = new Intent(getApplicationContext(),Activity_settings_calendarEdit.class);
                    startActivity(intent,options.toBundle());
                    break;
                case R.id.settings_ItemListBtn:
                    intent = new Intent(getApplicationContext(),Activity_settings_itemsEdit.class);
                    startActivity(intent,options.toBundle());
                    break;
            }
        }
    };
}
