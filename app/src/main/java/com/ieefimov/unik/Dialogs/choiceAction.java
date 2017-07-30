package com.ieefimov.unik.Dialogs;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.Classes.mySimpleAdapter;
import com.ieefimov.unik.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class choiceAction extends DialogFragment {

    ListView actionList;
    Activity activity;


    ////////////////////////////////////////////////////

    private String[] actions;
    private final String ATTRIBUTE_NAME="name";

    TextView title;
    String titleStr;

    mySimpleAdapter sAdapter;
    int who;

    private Space.onChoiceAction mListener;

    public choiceAction() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity, String[] todo,int position){
        this.actions = todo;
        this.mListener = (Space.onChoiceAction) activity ;
        this.activity = activity;
        this.who = position;
        getData();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        titleStr = tag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ask_calendar,container,false);

        actionList = (ListView) view.findViewById(R.id.calendarList);
        actionList.setAdapter(sAdapter);
        actionList.setOnItemClickListener(onItemClickListener);
        title = (TextView) view.findViewById(R.id.dialogTitle);
        title.setText(titleStr);
        return view;
    }

    private void getData(){
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(actions.length);
        Map<String, Object> m;
        for (int i = 0; i < actions.length; i++) {
            m = new HashMap<>();
            m.put(ATTRIBUTE_NAME, actions[i]);
            data.add(m);
        } // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME};
        int [] to = { R.id.file};

        sAdapter = new mySimpleAdapter(activity, data, R.layout.item_select_calendar, from, to);
    }


    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListener.choiceDone(who,position);
            dismiss();
        }
    };


}
