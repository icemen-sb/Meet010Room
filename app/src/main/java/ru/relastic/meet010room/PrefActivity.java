package ru.relastic.meet010room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class PrefActivity extends AppCompatActivity {
    public static final String KEY_BACKGROUND_COLOR_STRING="key_background_color";
    public static final String KEY_FOREGROUND_COLOR_STRING="key_foreground_color";
    public static final String KEY_BOLD_FONT_BOOLEAN="key_bold_font";
    public static final String KEY_ITALIC_FONT_BOOLEAN="key_italic_font";
    private static String DEFAULT_BACKGROUND = "0:0:255";
    private static String DEFAULT_FOREGROUND = "0:255:0";
    private static boolean DEFAULT_FONT_BOLD = true;
    private static boolean DEFAULT_FONT_ITALIC = true;

    MySharedPreferences myPreferencies;

    private EditText mBackgound, mForeground;
    private CheckBox mBold, mItalic;
    private Button mButtonCancel, mButtonCommit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        initViews();
        initListeners();
        init();
    }

    private void initViews() {
        mBackgound = findViewById(R.id.editText_pref_background);
        mForeground = findViewById(R.id.editText_pref_foreground);
        mBold = findViewById(R.id.checkBox_FontBold);
        mItalic = findViewById(R.id.checkBox_FontItalic);
        mButtonCancel = findViewById(R.id.btn_pref_cancel);
        mButtonCommit = findViewById(R.id.btn_pref_commit);
    }

    private void initListeners() {
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mButtonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Обновляем Prefetensies
                updatePref();
                finish();
            }
        });
    }

    private void init() {
        //<RESTORE VIEWS FROM PREFERENCIES ...
        myPreferencies = new MySharedPreferences(this);
        SharedPreferences pref = myPreferencies.getSharedPreferences();
        mBackgound.setText(pref.getString(KEY_BACKGROUND_COLOR_STRING,DEFAULT_BACKGROUND));
        mForeground.setText(pref.getString(KEY_FOREGROUND_COLOR_STRING,DEFAULT_FOREGROUND));
        mBold.setChecked(pref.getBoolean(KEY_BOLD_FONT_BOOLEAN,DEFAULT_FONT_BOLD));
        mItalic.setChecked(pref.getBoolean(KEY_ITALIC_FONT_BOOLEAN,DEFAULT_FONT_ITALIC));
    }

    private void updatePref() {
        //<SAVE TO PREFERENCIES ...
        SharedPreferences.Editor prefEditor = myPreferencies.getSharedPreferences().edit();
        prefEditor.clear();
        prefEditor.putString(KEY_BACKGROUND_COLOR_STRING,mBackgound.getText().toString());
        prefEditor.putString(KEY_FOREGROUND_COLOR_STRING,mForeground.getText().toString());
        prefEditor.putBoolean(KEY_BOLD_FONT_BOOLEAN,mBold.isChecked());
        prefEditor.putBoolean(KEY_ITALIC_FONT_BOOLEAN,mItalic.isChecked());
        prefEditor.commit();
    }
    static class MySharedPreferences {
        private SharedPreferences mSharedPreferencies;
        MySharedPreferences(Context context){
            mSharedPreferencies = context.getSharedPreferences(MainActivity.PREFERENCIES_KEY,
                    Context.MODE_PRIVATE);
        }
        public SharedPreferences getSharedPreferences() {
            return mSharedPreferencies;
        }
        public void applyPrefByView(View view){
            String valueStringBackground, valueStringForeground;
            boolean valueBooleanFonfBold,valueBooleanFonfItalic;
            valueStringBackground = mSharedPreferencies.getString(KEY_BACKGROUND_COLOR_STRING,DEFAULT_BACKGROUND);
            valueStringForeground = mSharedPreferencies.getString(KEY_FOREGROUND_COLOR_STRING,DEFAULT_FOREGROUND);
            valueBooleanFonfBold = mSharedPreferencies.getBoolean(KEY_BOLD_FONT_BOOLEAN,DEFAULT_FONT_BOLD);
            valueBooleanFonfItalic = mSharedPreferencies.getBoolean(KEY_ITALIC_FONT_BOOLEAN,DEFAULT_FONT_ITALIC);
            view.setBackgroundColor(convertStringToColor(valueStringBackground));
            if (view.getClass().getName().equals("android.support.v7.widget.AppCompatEditText")) {
                EditText editText = (EditText)view;
                editText.setTextColor(convertStringToColor(valueStringForeground));
                if (valueBooleanFonfBold & valueBooleanFonfItalic) {
                    editText.setTypeface(null, Typeface.BOLD_ITALIC);
                }else if (valueBooleanFonfBold) {
                    editText.setTypeface(null, Typeface.BOLD);
                } else if (valueBooleanFonfItalic) {
                    editText.setTypeface(null, Typeface.ITALIC);
                }
            }
        }
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, PrefActivity.class);
    }
    public static int convertStringToColor(String color) {
        String s[] = color.split(":");
        int red,green,blue;
        if (s.length>=0 && Integer.valueOf(s[0])<=255) {
            red = Integer.valueOf(s[0]);
        } else {
            red = 255;
        }
        if (s.length>=1 && Integer.valueOf(s[1])<=255) {
            green = Integer.valueOf(s[1]);
        } else {
            green = 255;
        }
        if (s.length>=2 && Integer.valueOf(s[2])<=255) {
            blue = Integer.valueOf(s[2]);
        } else {
            blue = 255;
        }
        return Color.rgb(red,green,blue);
    }
}
