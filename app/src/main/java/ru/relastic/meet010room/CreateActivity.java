package ru.relastic.meet010room;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CreateActivity extends AppCompatActivity {
    //CREATE NEW NOTES
    public static final int REQUEST_CODE_CREATE = 3000;
    private Button mButtonCancel, mButtonCommit;
    private EditText mEditText;
    MyFileManager.FileListeners mListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        initViews();
        initListeners();
        init();
    }
    private void initViews() {
        PrefActivity.MySharedPreferences pref = new PrefActivity.MySharedPreferences(this);
        mButtonCancel = findViewById(R.id.btn_create_cancel);
        mButtonCommit = findViewById(R.id.btn_create_commit);
        mEditText = findViewById(R.id.editText_cteate);
        pref.applyPrefByView(mEditText);
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
                addData();
                finish();
            }
        });
        mListeners = new MyFileManager.FileListeners(){

            @Override
            public void readed(String text) {
                //-
            }

            @Override
            public void writed(boolean completed) {
                if (completed) {
                    System.out.println("------ "+"file writed.");
                }else {
                    System.out.println("------ "+"error file writed.");
                }
            }

            @Override
            public void deleted(boolean completed) {
                if (completed) {
                    System.out.println("------ "+"file deleted.");
                }else {
                    System.out.println("------ "+"error file delete.");
                }
            }
        };
    }

    private void setFileStorage(String text, int id) {
        MyFileManager fileManager = MyFileManager.getInstance (this,mListeners, Integer.toString(id),
                MyFileManager.RUN_TYPE_WRITE,text);
        fileManager.startWorkedThread();
    }
    private void addData() {
        Bundle bundle = new Bundle();
        bundle.putInt(DBManager.FIELD_ID,0);
        String note = mEditText.getText().toString();
        bundle.putString(DBManager.FIELD_NOTE,note);
        DBManager dbm = DBManager.getInstance(CreateActivity.this);
        int id = dbm.updateData(bundle);

        setFileStorage(mEditText.getText().toString(),id);

        Intent intent = new Intent();
        intent.putExtra(MainActivity.RESULT_VALUE,true);
        setResult(CreateActivity.REQUEST_CODE_CREATE,intent);
    }

    private void init() {
        mEditText.setText("");
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, CreateActivity.class);
    }
}
