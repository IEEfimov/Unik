package com.ieefimov.unik.dialogs;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ieefimov.unik.R;
import com.ieefimov.unik.classes.Space;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ieefimov.unik.R.id.file;


public class openFile extends DialogFragment {

    Activity activity;

    private ListView listView;
    private TextView title;
    private Button ok,cancel;

    private String currentPath = Environment.getExternalStorageDirectory().getPath();
    private List<File> files = new ArrayList<File>();

    private FilenameFilter filenameFilter;
    private int selectedIndex = -2;
    private Drawable folderIcon;
    private Drawable fileIcon;
    private String accessDeniedMessage;
    private boolean isOnlyFoldersFilter;

    private String ATTRIBUTE_NAME = "name";
    private String ATTRIBUTE_SUB = "sub";
    private String ATTRIBUTE_IMG = "img";


    private Space.DialogOpenFile mListener;

    public openFile() {
        // Required empty public constructor
    }

    public void setActivity(Activity activity){
        this.mListener = (Space.DialogOpenFile) activity ;
        this.activity = activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_open_file,container,false);

        LinearLayout main = (LinearLayout) view.findViewById(R.id.main);
        main.setMinimumHeight(getLinearLayoutMinHeight(activity));
        main.setMinimumWidth(getLinearLayoutMinWidth(activity));

        listView = (ListView) view.findViewById(R.id.fileList);
        title = (TextView) view.findViewById(R.id.title);
        ok = (Button) view.findViewById(R.id.btnOk);
        cancel = (Button) view.findViewById(R.id.btnCancel);

        listView.setOnItemClickListener(onItemClickListener);
        ok.setOnClickListener(btnOnClick);
        cancel.setOnClickListener(btnOnClick);
        loadAdapter(currentPath);
        return view;
    }



    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final FileAdapter adapter = (FileAdapter) parent.getAdapter();
            if (position == 0){
                File file = new File(currentPath);
                File parentDirectory = file.getParentFile();
                if (currentPath.equals(Environment.getExternalStorageDirectory().getPath()))
                    return;
                if (parentDirectory != null) {
                    currentPath = parentDirectory.getPath();
                    loadAdapter(currentPath);
                }
                return;
            }
            int index = position-1;
            File file = files.get(index);

            if (file.isDirectory()) {
                currentPath = file.getPath();
                loadAdapter(currentPath);
            } else {
                if (index != selectedIndex){
                    selectedIndex = index;
                    listView.getAdapter().getItem(index);
                }
                else
                    selectedIndex = -2;
                adapter.notifyDataSetChanged();
            }
        }
    };

    Button.OnClickListener btnOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnCancel:
                    dismiss();
                    break;
                case R.id.btnOk:
                    if (selectedIndex >= 0){
                        mListener.open(files.get(selectedIndex));
                    }
                    break;
            }
        }
    };

    private void loadAdapter(String path){
        try {
            List<File> fileList = getFiles(path);
            files.clear();
            selectedIndex = -2;
            files.addAll(fileList);

            int count = files.size()-1;
            for (int i = count; i >= 0; i--) {
                if (files.get(i).isHidden()){
                    files.remove(i);
                    continue;
                }
                if (files.get(i).isDirectory()) continue;
                else {
                    if (!Space.compareExtension(files.get(i).getName())) files.remove(i);
                }
            } // массив имен атрибутов, из которых будут читаться данные

            ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(files.size());
            Map<String, Object> m;

            m = new HashMap<>();
            m.put(ATTRIBUTE_NAME, "..");
            m.put(ATTRIBUTE_SUB,activity.getResources().getString(R.string.openFileDialog_backSubtitle));
            m.put(ATTRIBUTE_IMG,R.drawable.ic_directions_white_24dp);

            data.add(m);

            for (int i = 0; i < files.size(); i++) {
                m = new HashMap<>();
                m.put(ATTRIBUTE_NAME, files.get(i).getName());

                if (files.get(i).isDirectory()){
                    m.put(ATTRIBUTE_IMG,R.drawable.ic_folder_open_white_24dp);
                    m.put(ATTRIBUTE_SUB,activity.getResources().getString(R.string.openFileDialog_folder));
                }
                else {
                    if (Space.compareExtension(files.get(i).getName())){
                        m.put(ATTRIBUTE_IMG,R.drawable.ic_insert_drive_file_white_24dp);
                        m.put(ATTRIBUTE_SUB,"бекап");
                    }
                    else m.put(ATTRIBUTE_IMG,0);
                }
                data.add(m);
            } // массив имен атрибутов, из которых будут читаться данные
            String[] from = { ATTRIBUTE_NAME,ATTRIBUTE_SUB,ATTRIBUTE_IMG};
            int [] to = { file,R.id.subFile,R.id.img};

            FileAdapter sAdapter = new FileAdapter(activity, data, R.layout.item_open_file_adapter, from, to);
            listView.setAdapter(sAdapter);
            changeTitle();
        } catch (NullPointerException e) {
            String message = activity.getResources().getString(android.R.string.unknownName);
            if (!accessDeniedMessage.equals(""))
                message = accessDeniedMessage;
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeTitle() {
        String titleText = currentPath;
        int screenWidth = getScreenSize(activity).x;
        int maxWidth = (int) (screenWidth * 0.99);
        if (getTextWidth(titleText, title.getPaint()) > maxWidth) {
            while (getTextWidth("..." + titleText, title.getPaint()) > maxWidth) {
                int start = titleText.indexOf("/", 2);
                if (start > 0)
                    titleText = titleText.substring(start);
                else
                    titleText = titleText.substring(2);
            }
            title.setText("..." + titleText);
        } else {
            title.setText(titleText);
        }
    }

    private List<File> getFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] list = directory.listFiles(filenameFilter);
        if(list == null)
            list = new File[]{};
        List<File> fileList = Arrays.asList(list);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                if (file.isDirectory() && file2.isFile())
                    return -1;
                else if (file.isFile() && file2.isDirectory())
                    return 1;
                else
                    return file.getPath().compareTo(file2.getPath());
            }
        });

        return fileList;

    }

    private static Display getDefaultDisplay(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    private static Point getScreenSize(Context context) {
        Point screeSize = new Point();
        getDefaultDisplay(context).getSize(screeSize);
        return screeSize;
    }

    private static int getLinearLayoutMinHeight(Context context) {
        return getScreenSize(context).y;
    }

    private static int getLinearLayoutMinWidth(Context context) {
        return getScreenSize(context).x;
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width() + 80;
    }

    private class FileAdapter extends SimpleAdapter {

        public FileAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            if (selectedIndex == position-1)
                view.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_dark));
            else
                view.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
            return view;
        }

        private void setDrawable(TextView view, Drawable drawable) {
            if (view != null) {
                if (drawable != null) {
                    drawable.setBounds(0, 0, 60, 60);
                    view.setCompoundDrawables(drawable, null, null, null);
                } else {
                    view.setCompoundDrawables(null, null, null, null);
                }
            }
        }

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }
    }


}
