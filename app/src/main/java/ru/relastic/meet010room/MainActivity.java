package ru.relastic.meet010room;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //LIST OF NOTES
    public static final String PREFERENCIES_KEY = "preferencies_key";
    public static final String RESULT_VALUE = "result_value";
    public static final String MESSAGE_KEY_STRING_EXTRA = "message_key_string_extra";

    private Uri content_uri;

    private Button mButtonOpen, mButtonNew, mButtonOptions;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private LinearLayoutManager mLinearLayouManager;
    private volatile int cur_id=-1;
    public ArrayList<Bundle> mData = new ArrayList<>();

    private MyObserver myObserver = new MyObserver(new MyHandler(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content_uri = Uri.parse("content://" +
                getString(R.string.content_provider_authorities_remote) + "/" + DBManager.TABLE_NAME);

        setContentView(R.layout.activity_main);
        DBManager dbm = DBManager.getInstance(this);
        dbm.registerObserver(content_uri, true, myObserver);
        populateData();
        initViews();
        initListeners();
        init();
    }
    @Override
    protected void onStop() {
        //DBManager dbm = DBManager.getInstance(this);
        //dbm.unregisterObserver(myObserver);
        super.onStop();
    }

    private void initViews(){
        mButtonOpen = findViewById(R.id.button_open);
        mButtonNew = findViewById(R.id.button_new);
        mButtonOptions = findViewById(R.id.button_pref);
        mRecyclerView = findViewById(R.id.list);
        mLinearLayouManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayouManager);
        mAdapter = new MyAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
    }
    private void initListeners(){
        mButtonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotes(cur_id);
            }
        });
        mButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotes();
            }
        });
        mButtonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreferences();
            }
        });

    }
    private void init() {
        //init data sources
        //
    }
    private void populateData(){
        DBManager dbm = DBManager.getInstance(this);
        mData.clear();
        mData.addAll(dbm.getData());
    }
    private void updateFromBeyond() {
        populateData();
        mAdapter.notifyDataSetChanged();
        Log.v("MainActivity","UPDATED UI Meet010Room");
    }
    public void openNotes(int id){
        if (id>=0) {
            cur_id = id;
            Intent intent = DetailsActivity.getIntent(this);
            intent.putExtra(DBManager.FIELD_ID,id);
            startActivityForResult(intent,DetailsActivity.REQUEST_CODE_DETAILS);
        }
    }
    public void selectPosition(int id) {
        cur_id = id;
    }
    private void createNotes() {
        startActivityForResult(CreateActivity.getIntent(this),CreateActivity.REQUEST_CODE_CREATE);
    }
    private void openPreferences() {
        startActivity(PrefActivity.getIntent(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null && data.getBooleanExtra(RESULT_VALUE,false)) {
            populateData();
            if (requestCode == DetailsActivity.REQUEST_CODE_DETAILS) {
                //обновление recyclerView
                int position = getPositionByID(data.getIntExtra(DBManager.FIELD_ID, -1), mData);
                mAdapter.notifyItemChanged(position);
            }else if (requestCode == CreateActivity.REQUEST_CODE_CREATE) {
                //обновление recyclerView
                int position = mData.size();
                mAdapter.notifyItemInserted(position);
            }
        }
    }
    private static int getPositionByID(int id, ArrayList<Bundle> data) {
        int i=-1;
        for (Bundle bundle : data) {
            i++;
            if (bundle.getInt(DBManager.FIELD_ID)==id) {
                break;
            }
        }
        return  i;
    }

    public static class MyHandler extends Handler {
        private final  Context mContext;

        MyHandler(Context context) {
            mContext = context;
        }
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MyObserver.MESSAGE_WHAT_REDRAW_UI) {
                ((MainActivity)mContext).updateFromBeyond();
            }
        }
    }
}
