package com.ieefimov.unik.Dialogs;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ieefimov.unik.Classes.Hour;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.R;


public class askTime extends DialogFragment {

    Button cancel;
    Button ok;

    TextView title,subtitle;
    String titleStr,subStr, nameFieldText1,nameFieldText2;

    EditText timeStart,timeEnd;

    Hour hour;

    private Space.OnCompleteListener mListener;
    private int todo;

    public askTime() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity, int todo){
        this.mListener = (Space.OnCompleteListener) activity;
        this.todo = todo;
        if (todo==Space.OnCompleteListener.RENAME_CALENDAR){
            // todo Написать нормальный текст
            titleStr = activity.getResources().getString(R.string.dialog_editTime_title);
            subStr = activity.getResources().getString(R.string.dialog_editTime_subtitle);
        }

    }


    public void show(FragmentManager manager, Hour hour) {
        super.show(manager, "askTime");
        nameFieldText1 = hour.getStart();
        nameFieldText2 = hour.getEnd();
        this.hour = hour;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ask_time,container,false);
        timeStart = (EditText) view.findViewById(R.id.startTime);
        timeEnd = (EditText) view.findViewById(R.id.endTime);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        ok = (Button) view.findViewById(R.id.btn_ok);

        title = (TextView) view.findViewById(R.id.Title);
        subtitle = (TextView) view.findViewById(R.id.Subtitle);
        timeStart.setText(nameFieldText1);
        timeEnd.setText(nameFieldText2);

        timeStart.addTextChangedListener(textWatcher);
        timeEnd.addTextChangedListener(textWatcher);
        timeStart.setSelectAllOnFocus(true);
        timeEnd.setSelectAllOnFocus(true);


        title.setText(titleStr);
        subtitle.setText(subStr);

        cancel.setOnClickListener(btnOnClick);
        ok.setOnClickListener(btnOnClick);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(timeStart,0);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        timeStart.requestFocus();

        return view;
    }

    Button.OnClickListener btnOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_cancel:
                    dismiss();
                    break;
                case R.id.btn_ok:
                    try{
                        hour.setStart(timeStart.getText().toString());
                        hour.setEnd(timeEnd.getText().toString());
                        if (todo == Space.OnCompleteListener.EDIT_ITEM) mListener.editItem(hour);
                        dismiss();
                    }catch (Exception e){
                        Log.e("ERROR",e.getMessage());
                    }
                    break;
            }
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (before==0){
                    if (s.length()==1){
                        if (Integer.parseInt(s.toString())>2){ // если первое число > 2
                            if (timeStart.hasFocus()) timeStart.setText("");
                            if (timeEnd.hasFocus()) timeEnd.setText("");
                        }
                    }
                    if (s.length()==2){
                        if (Integer.parseInt(s.toString())>=24){ // если второе число > 4
                            if (timeStart.hasFocus()) timeStart.setText("2");
                            if (timeEnd.hasFocus()) timeEnd.setText("2");
                            return;
                        }
                        s = s+":";
                        if (timeStart.hasFocus()) timeStart.setText(s);
                        if (timeEnd.hasFocus()) timeEnd.setText(s);
                    }
                    if (s.length()==3){
                        String num=""+s.charAt(2);
                        String time = "";
                        for (int i=0;i<2;i++) time += s.charAt(i);
                        if (s.charAt(2)!=':'){ // если третье число > 6
                            String add = "";
                            if (Integer.parseInt(num)<6) add = num;
                            if (timeStart.hasFocus()) timeStart.setText(time+":"+add);
                            if (timeEnd.hasFocus()) timeEnd.setText(time+":"+add);
                        }
                    }
                    if (s.length()==4){
                        String num="";
                        num+=s.charAt(3);
                        String time = "";
                        for (int i=0;i<3;i++) time += s.charAt(i);
                        if (Integer.parseInt(num)>=6){ // если третье число > 6
                            if (timeStart.hasFocus()) timeStart.setText(time);
                            if (timeEnd.hasFocus()) timeEnd.setText(time);
                        }
                    }
                    if (s.length()>=5){
                        String time = "";
                        for (int i=0;i<5;i++) time += s.charAt(i);
                        if (timeStart.hasFocus()) timeStart.setText(time);
                        if (timeEnd.hasFocus()) timeEnd.setText(time);
                    }
                }


            }catch (Exception e){

            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            subtitle.setText("1="+timeStart.getText().toString()+"\n\r2="+s.toString());
            if (timeStart.hasFocus()){
                timeStart.setSelectAllOnFocus(false);
                timeEnd.requestFocus();
                timeStart.requestFocus(View.FOCUS_BACKWARD);
                timeStart.setSelectAllOnFocus(true);
            }
            if (timeEnd.hasFocus()){
                timeEnd.setSelectAllOnFocus(false);
                timeStart.requestFocus();
                timeEnd.requestFocus(View.FOCUS_BACKWARD);
                timeEnd.setSelectAllOnFocus(true);
            }
        }
    };

    EditText.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            EditText temp = (EditText) v;
            if (temp.getText().length()==2) temp.setText("00:");
            return false;
        }
    };

}
