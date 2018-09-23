package com.ieefimov.unik.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

/**
 * Created with IntelliJ IDEA.
 * User: Scogun
 * Date: 27.11.13
 * Time: 10:47
 */
public class OpenFileDialog_old extends AlertDialog.Builder {

    private String currentPath = Environment.getExternalStorageDirectory().getPath();
    private List<File> files = new ArrayList<File>();
    private TextView title;
    private ListView listView;
    private FilenameFilter filenameFilter;
    private int selectedIndex = -1;
    private OpenDialogListener listener;
    private Drawable folderIcon;
    private Drawable fileIcon;
    private String accessDeniedMessage;
    private boolean isOnlyFoldersFilter;

    private String ATTRIBUTE_NAME = "name";
    private String ATTRIBUTE_IMG = "img";

    public interface OpenDialogListener {
        public void OnSelectedFile(String fileName);
    }

    private class FileAdapter_old extends ArrayAdapter<File> {

        public FileAdapter_old(Context context, List<File> files) {
            super(context, android.R.layout.simple_list_item_1, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            if (view != null) {
                view.setText(file.getName());
                if (file.isDirectory()) {
                    setDrawable(view, folderIcon);
                } else {
                    setDrawable(view, fileIcon);
                    if (selectedIndex == position)
                        view.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
                    else
                        view.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
                }
            }
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
    }

    private class FileAdapter extends SimpleAdapter {

        public FileAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            //TextView text = (TextView) view.findViewById(R.id.file);
//            File file = getItem(position);
//            if (view != null) {
//                view.setText(file.getName());
//                if (file.isDirectory()) {
//                    setDrawable(view, folderIcon);
//                } else {
//                    setDrawable(view, fileIcon);
//                    if (selectedIndex == position)
//                        view.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
//                    else
//                        view.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
//                }
//            }

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




    public OpenFileDialog_old(Context context) {
//        super(new ContextThemeWrapper(context,R.style.AlertDialogCustom));
        super(context);
        isOnlyFoldersFilter = false;
        title = createTitle(context);
        changeTitle();
        LinearLayout linearLayout = createMainLayout(context);
        linearLayout.addView(createBackItem(context));
        listView = createListView(context);
        linearLayout.addView(listView);
        setCustomTitle(title)
                .setView(linearLayout)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedIndex > -1 && listener != null) {
                            listener.OnSelectedFile(listView.getItemAtPosition(selectedIndex).toString());
                        }
                        if (listener != null && isOnlyFoldersFilter) {
                            listener.OnSelectedFile(currentPath);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
    }

    @Override
    public AlertDialog show() {
        loadAdapter(currentPath);
        return super.show();
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

    private LinearLayout createMainLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(getLinearLayoutMinHeight(context));
        return linearLayout;
    }

    private int getItemHeight(Context context) {
        TypedValue value = new TypedValue();
        DisplayMetrics metrics = new DisplayMetrics();
        context.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeightSmall, value, true);
        getDefaultDisplay(context).getMetrics(metrics);
        return (int) TypedValue.complexToDimension(value.data, metrics);
    }

    private TextView createTextView(Context context, int style) {
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, style);
        int itemHeight = getItemHeight(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        textView.setMinHeight(itemHeight);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15, 0, 0, 0);
        return textView;
    }

    private TextView createTitle(Context context) {
        TextView textView = createTextView(context, android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle);
        return textView;
    }

    private TextView createBackItem(Context context) {
        TextView textView = createTextView(context, android.R.style.TextAppearance_DeviceDefault_Small);
        Drawable drawable = getContext().getResources().getDrawable(android.R.drawable.ic_menu_directions);
        drawable.setBounds(0, 0, 60, 60);
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File file = new File(currentPath);
                File parentDirectory = file.getParentFile();
                if (parentDirectory != null) {
                    currentPath = parentDirectory.getPath();
                    loadAdapter(currentPath);
                }
            }
        });
        return textView;
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width() + 80;
    }

    private void changeTitle() {
        String titleText = currentPath;
        int screenWidth = getScreenSize(getContext()).x;
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

    private void loadAdapter(String path){
        try {
            List<File> fileList = getFiles(path);
            files.clear();
            selectedIndex = -1;
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

            for (int i = 0; i < files.size(); i++) {
                m = new HashMap<>();
                m.put(ATTRIBUTE_NAME, files.get(i).getName());
                if (files.get(i).isDirectory()) m.put(ATTRIBUTE_IMG,R.drawable.ic_folder_open_white_24dp);
                else {
                    if (Space.compareExtension(files.get(i).getName()))
                        m.put(ATTRIBUTE_IMG,R.drawable.ic_insert_drive_file_white_24dp);
                    else m.put(ATTRIBUTE_IMG,0);
                }
                data.add(m);
            } // массив имен атрибутов, из которых будут читаться данные
            String[] from = { ATTRIBUTE_NAME,ATTRIBUTE_IMG};
            int [] to = { file,R.id.img};

            FileAdapter sAdapter = new FileAdapter(getContext(), data, R.layout.item_open_file_adapter, from, to);
            listView.setAdapter(sAdapter);
            changeTitle();
        } catch (NullPointerException e) {
            String message = getContext().getResources().getString(android.R.string.unknownName);
            if (!accessDeniedMessage.equals(""))
                message = accessDeniedMessage;
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    private ListView createListView(Context context) {
        ListView listView = new ListView(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                final FileAdapter adapter = (FileAdapter) adapterView.getAdapter();
                // TODO: 16.08.2017 ну надо что бы работало :)
                File file = files.get(index);

                //File file = adapter.getItem(index);
                if (file.isDirectory()) {
                    currentPath = file.getPath();
                    loadAdapter(currentPath);
                } else {
                    if (index != selectedIndex)
                        selectedIndex = index;
                    else
                        selectedIndex = -1;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return listView;
    }
}