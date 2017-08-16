package com.ieefimov.unik;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ieefimov.unik.classes.ConnectorDB;

public class StartScreen extends AppCompatActivity {

    ConnectorDB database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        database = new ConnectorDB(this); // подключение к БД.

        Intent starting = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(starting);

        finish();
    }

}
