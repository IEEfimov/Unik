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
import android.widget.Switch;
import android.widget.TextView;

import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.R;


public class askConfirm extends DialogFragment {

    Space.OnCompleteListener mListener;
    int todo;

    Button cancel;
    Button ok;

    Switch aSwitch;

    TextView title,subtitle;
    String titleStr,subStr;

    //////////////////////////////////////////

    public askConfirm() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity, int todo){
        this.mListener = (Space.OnCompleteListener) activity;
        this.todo = todo;
        //if (tod==Space.OnCompleteListener.DELETE_CALENDAR)
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask_confirm,container,false);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        ok = (Button) view.findViewById(R.id.btn_ok);
        aSwitch = (Switch) view.findViewById(R.id.switch1);
        title = (TextView) view.findViewById(R.id.Title);
        subtitle = (TextView) view.findViewById(R.id.Subtitle);

        title.setText(titleStr);
        subtitle.setText(subStr);

        cancel.setOnClickListener(btnOnClick);
        ok.setOnClickListener(btnOnClick);

        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        titleStr = "Удаление календаря \""+tag+"\"";
        subStr = "Восстановить календарь можно только при наличии резервной копии";
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
                        if (aSwitch.isChecked()){
                            if (todo == Space.OnCompleteListener.DELETE_CALENDAR) mListener.deleteCalendar();
                            dismiss();
                        }
                       // dismiss();
                    }catch (Exception e){
                        Log.e("ERROR",e.getMessage());
                    }
                    break;
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
