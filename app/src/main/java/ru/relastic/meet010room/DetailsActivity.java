package ru.relastic.meet010room;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DetailsActivity extends AppCompatActivity {
    //DETAILS / EDIT NOTES
    public static final int REQUEST_CODE_DETAILS = 2000;
    public static final int MESSAGE_WHAT_KEY = 2;
    private Button mButtonCancel, mButtonCommit;
    private EditText mEditText;
    private int current_id = 0;
    private Bundle data;
    MyFileManager.FileListeners mListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initViews();
        initListeners();
        init();
    }

    private void initViews() {
        //get current preferences
        //apply current preferences
        PrefActivity.MySharedPreferences pref = new PrefActivity.MySharedPreferences(this);
        mButtonCancel = findViewById(R.id.btn_delails_cancel);
        mButtonCommit = findViewById(R.id.btn_delails_commit);
        mEditText = findViewById(R.id.editTextDetails);
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
                //Обновляем БД, файл
                updateData();
                finish();
            }
        });

        mListeners = new MyFileManager.FileListeners(){

            @Override
            public void readed(String text) {
                Handler h = new Handler(getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == MESSAGE_WHAT_KEY) {
                            DetailsActivity.this.mEditText.setText(
                                    msg.getData().getString(MainActivity.MESSAGE_KEY_STRING_EXTRA));
                        }

                    }
                };
                Bundle bundle = new Bundle();
                bundle.putString(MainActivity.MESSAGE_KEY_STRING_EXTRA,text);

                Message message = Message.obtain(h,MESSAGE_WHAT_KEY);
                message.setData(bundle);
                message.sendToTarget();
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
    private void init() {
        Intent intent = getIntent();
        current_id = intent.getIntExtra(DBManager.FIELD_ID,0);
        if (!(current_id>0)) {
            Log.v("ERROR ID","ERROR IN INPUT ID, EXPECT ID > 0");
            finish();
        }
        DBManager dbm = DBManager.getInstance(this);

        data = dbm.getDataById(current_id);

        mEditText.setText(MyFileManager.LOADING_STATE_SATB);
        getFileStorage();
    }

    private void getFileStorage() {
        MyFileManager fileManager = MyFileManager.getInstance(this,mListeners, Integer.toString(current_id),
                MyFileManager.RUN_TYPE_READ,null);
        fileManager.startWorkedThread();
    }
    private void setFileStorage(String text, int id) {
        MyFileManager fileManager =MyFileManager.getInstance(this,mListeners, Integer.toString(id),
                MyFileManager.RUN_TYPE_WRITE,text);
        fileManager.startWorkedThread();
    }

    private void updateData() {
        Bundle bundle = new Bundle();
        bundle.putInt(DBManager.FIELD_POS,0);
        bundle.putInt(DBManager.FIELD_ID,current_id);
        String note = mEditText.getText().toString();
        bundle.putString(DBManager.FIELD_NOTE,note);
        DBManager dbm = DBManager.getInstance(DetailsActivity.this);
        int id = dbm.updateData(bundle);

        setFileStorage(mEditText.getText().toString(), id);

        Intent intent = new Intent();
        intent.putExtra(MainActivity.RESULT_VALUE,true);
        intent.putExtra(DBManager.FIELD_ID,current_id);
        setResult(DetailsActivity.REQUEST_CODE_DETAILS,intent);
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, DetailsActivity.class);
    }
}
