package com.ieefimov.unik.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.ieefimov.unik.R;
import com.ieefimov.unik.classes.Space;

import java.util.Calendar;
import java.util.Locale;

public class askDate extends DialogFragment {

    Button cancel;
    Button ok;
    TextView dayOfWeekView, dayView, monthView;
    CalendarView calendarView;

    private Space.DialogDate mListener;
    private int position;

    Calendar date;

    public askDate() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity, int todo){
        this.mListener = (Space.DialogDate) activity;
        this.position = todo;
    }

    public void show(FragmentManager manager,Calendar date) {
        super.show(manager, "tag");
        this.date = date;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Material_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ask_date,container,false);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        ok = (Button) view.findViewById(R.id.btn_ok);

        dayOfWeekView = (TextView) view.findViewById(R.id.dayOfWeek);
        dayView = (TextView) view.findViewById(R.id.day);
        monthView = (TextView) view.findViewById(R.id.month);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(onDateChangeListener);
        calendarView.setDate(date.getTimeInMillis());

        String dayStr = date.get(Calendar.DAY_OF_MONTH) + "";
        dayView.setText(dayStr);
        String dayOfWeekStr = date.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault());
        dayOfWeekView.setText(dayOfWeekStr);
        String monthStr = date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault());
        monthView.setText(monthStr);

        cancel.setOnClickListener(btnOnClick);
        ok.setOnClickListener(btnOnClick);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    Button.OnClickListener btnOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_cancel:
                    // TODO: Заполнить обработку кнопок в диалоге
                    dismiss();
                    break;
                case R.id.btn_ok:
                    try{
                        mListener.getDate(position,date);
                        dismiss();
                    }catch (Exception e){
                        Log.e("ERROR",e.getMessage());
                    }
                    break;
            }
        }
    };

    CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            date.set(year,month,dayOfMonth);
            dayView.setText(dayOfMonth+"");
            String dayOfWeekStr = date.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault());
            dayOfWeekView.setText(dayOfWeekStr);
            String monthStr = date.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault());
            monthView.setText(monthStr);


        }
    };

}
