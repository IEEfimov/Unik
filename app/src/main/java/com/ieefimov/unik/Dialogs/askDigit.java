package com.ieefimov.unik.Dialogs;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.ieefimov.unik.R;

import java.lang.reflect.Field;

public class askDigit extends DialogFragment {

    NumberPicker num;
    Button cancel;
    Button ok;

    private OnFragmentInteractionListener mListener;

    public askDigit() {
        // Required empty public constructor
    }


    public static askName newInstance(String param1, String param2) {
        askName fragment = new askName();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        num.setMaxValue(20);
        num.setMinValue(1);
        num.setValue(4);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
