package com.ieefimov.unik.Dialogs;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ieefimov.unik.Classes.Item;
import com.ieefimov.unik.Classes.Space;
import com.ieefimov.unik.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class askAction extends DialogFragment {

    EditText nameEdit;
    EditText roomEdit;
    Button cancel;
    Button ok;

    TextView title,subtitle;
    String titleStr,subStr;

    ///////////////////////////////////////////////////////////
    Item currentItem;

    private Space.itemsEditListener mListener;

    public askAction() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity){
        this.mListener = (Space.itemsEditListener) activity;

            titleStr = activity.getResources().getString(R.string.dialog_editItem_title);
            //subStr = activity.getResources().getString(R.string.dialog_editTime_subtitle);

    }

    public void show(FragmentManager manager, Item item) {
        super.show(manager, "askTime");
//        nameFieldText1 = hour.getStart();
//        nameFieldText2 = hour.getEnd();
        this.currentItem = item;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ask_action,container,false);
        nameEdit = (EditText) view.findViewById(R.id.editText);
        roomEdit = (EditText) view.findViewById(R.id.editText2);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        ok = (Button) view.findViewById(R.id.btn_ok);

        title = (TextView) view.findViewById(R.id.Title);
        subtitle = (TextView) view.findViewById(R.id.Subtitle);
        nameEdit.setText(currentItem.getName());
        roomEdit.setText(currentItem.getRoom());

        nameEdit.addTextChangedListener(textWatcher);
        roomEdit.addTextChangedListener(textWatcher);
        roomEdit.setSelectAllOnFocus(true);
        nameEdit.setSelectAllOnFocus(true);


        title.setText(titleStr);
        subtitle.setText(subStr);

        cancel.setOnClickListener(btnOnClick);
        ok.setOnClickListener(btnOnClick);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(nameEdit,0);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        nameEdit.requestFocus();

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
                        currentItem.setName(nameEdit.getText().toString());
                        currentItem.setRoom(roomEdit.getText().toString());
                        mListener.editItem(currentItem);
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
                if (nameEdit.hasFocus()){
                    if (s.length()>=60) nameEdit.setText(s);
                }
                if (roomEdit.hasFocus()) {
                    if (s.length() >= 10) roomEdit.setText(s);
                }
            }catch (Exception e){

            }
        }

        @Override
        public void afterTextChanged(Editable s) {
//            subtitle.setText("1="+timeStart.getText().toString()+"\n\r2="+s.toString());
//            if (timeStart.hasFocus()){
//                timeStart.setSelectAllOnFocus(false);
//                timeEnd.requestFocus();
//                timeStart.requestFocus(View.FOCUS_BACKWARD);
//                timeStart.setSelectAllOnFocus(true);
//            }
//            if (timeEnd.hasFocus()){
//                timeEnd.setSelectAllOnFocus(false);
//                timeStart.requestFocus();
//                timeEnd.requestFocus(View.FOCUS_BACKWARD);
//                timeEnd.setSelectAllOnFocus(true);
//            }
        }
    };

}
