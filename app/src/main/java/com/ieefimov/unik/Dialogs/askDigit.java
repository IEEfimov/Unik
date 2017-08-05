package com.ieefimov.unik.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.ieefimov.unik.R;
import com.ieefimov.unik.classes.Space;

import java.lang.reflect.Field;

public class askDigit extends DialogFragment {

    NumberPicker num;

    Button cancel;
    Button ok;

    TextView title,subtitle;
    String titleStr,subStr, nameFieldText;

    Space.OnCompleteListener mListener;
    int todo;

    public askDigit() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity, int todo){
        this.mListener = (Space.OnCompleteListener) activity;
        this.todo = todo;
        if (todo==Space.OnCompleteListener.EDIT_ITEM_COUNT){
            titleStr = activity.getResources().getString(R.string.dialog_editItemCount_title);
            subStr = activity.getResources().getString(R.string.dialog_editItemCount_subtitle);

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
        View view = inflater.inflate(R.layout.dialog_ask_digit,container,false);
        num = (NumberPicker) view.findViewById(R.id.numberPicker);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        ok = (Button) view.findViewById(R.id.btn_ok);
        title = (TextView) view.findViewById(R.id.Title);
        subtitle = (TextView) view.findViewById(R.id.Subtitle);

        title.setText(titleStr);
        subtitle.setText(subStr);

        num.setMaxValue(20);
        num.setMinValue(1);

        try {
            int number = Integer.parseInt(nameFieldText);
            num.setValue(number);
        }catch (Exception e){};


        num.setWrapSelectorWheel(false);

        cancel.setOnClickListener(btnOnClick);
        ok.setOnClickListener(btnOnClick);

        setNumberPickerTextColor(num, Color.WHITE);

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
                    mListener.editItemCount(num.getValue());
                    dismiss();
                    break;
            }
        }
    };

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColo", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColo", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColo", e);
                }
            }
        }
        return false;
    }
}
