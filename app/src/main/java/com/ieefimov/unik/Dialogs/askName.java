package com.ieefimov.unik.Dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.R;

public class askName extends DialogFragment {

    EditText name;
    Button cancel;
    Button ok;

    TextView title,subtitle;
    String titleStr,subStr, nameFieldText;

    private Space.OnCompleteListener mListener;
    private int todo;

    public askName() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity, int todo){
        this.mListener = (Space.OnCompleteListener) activity;
        this.todo = todo;
        if (todo==Space.OnCompleteListener.RENAME_CALENDAR){
            titleStr = "Введите новое имя:";
            subStr = "Выбранное имя будет установленно для выбранного календаря";

        }
        if (todo==Space.OnCompleteListener.ADD_CALENDAR){
            titleStr = "Введите имя календаря:";
            subStr = "Будет создан новый календарь, который Вы позже сможете настроить";
            nameFieldText = "Новый календарь";
        }

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        nameFieldText = tag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ask_name,container,false);
        name = (EditText) view.findViewById(R.id.editText);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        ok = (Button) view.findViewById(R.id.btn_ok);

        title = (TextView) view.findViewById(R.id.Title);
        subtitle = (TextView) view.findViewById(R.id.Subtitle);
        name.setText(nameFieldText);

        title.setText(titleStr);
        subtitle.setText(subStr);

        cancel.setOnClickListener(btnOnClick);
        ok.setOnClickListener(btnOnClick);

        name.requestFocus();

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
                        if (todo == Space.OnCompleteListener.ADD_CALENDAR) mListener.addCalendar(name.getText().toString());
                        if (todo == Space.OnCompleteListener.RENAME_CALENDAR) mListener.renameCalendar(name.getText().toString());
                        dismiss();
                    }catch (Exception e){
                        Log.e("ERROR",e.getMessage());
                    }
                    break;
            }
        }
    };

}
