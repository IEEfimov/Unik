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

    Space.DialogConfirm mListener;
    int position;

    Button cancel;
    Button ok;

    Switch aSwitch;

    TextView title,subtitle;
    String titleStr,subStr;

    //////////////////////////////////////////

    Activity activity;

    public askConfirm() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity,int position){
        this.mListener = (Space.DialogConfirm) activity;
        this.position = position;
        this.activity = activity;
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
        View view = inflater.inflate(R.layout.dialog_ask_confirm,container,false);
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

    public void show(FragmentManager manager,String title,String subtitle) {
        titleStr = title;
        subStr = subtitle;
//        titleStr = activity.getResources().getString(R.string.dialog_deleteCalendar_title) + tag + "\".";
//        subStr = activity.getResources().getString(R.string.dialog_deleteCalendar_subtitle);
        super.show(manager, "tag");
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
                            mListener.confirm(position,true);
                            dismiss();
                        }
                       // dismiss();
                    }catch (Exception e){
                        Log.e("ERROR",e.getMessage());
                        dismiss();
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
