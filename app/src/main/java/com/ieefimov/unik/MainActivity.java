package com.ieefimov.unik;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ieefimov.unik.Classes.Space;

public class MainActivity extends AppCompatActivity {

   // Context context;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //context = this;

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        FrameLayout kostil = (FrameLayout) findViewById(R.id.kostil_top);
        ViewGroup.LayoutParams params = kostil.getLayoutParams(); //    Задается правильный отступ для тулбара
        params.height = getStatusBarHeight();   //                  для разных устройств
        kostil.setLayoutParams(params);
        setSupportActionBar(tool);

        Button settingsBtn = (Button) findViewById(R.id.nav_settingsBtn);
        settingsBtn.setOnClickListener(onClickListener);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        Space.mainDrawer = drawerLayout;
        Space.stausBarHeight = getStatusBarHeight();

        Intent test = new Intent(getApplicationContext(),Activity_settings_calendarEdit.class);
        startActivity(test);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.nav_settingsBtn:
                    Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.show_activity,R.anim.hide_activity);
                    startActivity(intent,options.toBundle());
                    break;

            }
        }
    };
}
