package ru.relastic.meet010room;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class DBManagerContentProvider extends DBManager {
    private final Uri content_uri;

    public DBManagerContentProvider(Context context) {
        super(context);
        content_uri = Uri.parse("content://" +
                context.getResources().getString(R.string.content_provider_authorities_remote) +
                "/" + TABLE_NAME);
        Log.v("DBManager:","Created instatce of DBManagerContentProvider class.");
    }

    @Override
    public ArrayList<Bundle> getData() {
        return parseCursor(mContext.getContentResolver().query(content_uri,null, null,
                null, null));
    }

    @Override
    public Bundle getDataById(int id) {
        Bundle retVal = null;
        Cursor cursor = mContext.getContentResolver().query
                (Uri.withAppendedPath(content_uri,String.valueOf(id)),null, null,
                        null, null);
        retVal = new Bundle();
        retVal.putInt(FIELD_POS,1);
        cursor.moveToFirst();
        retVal.putInt(FIELD_ID,cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
        retVal.putString(FIELD_NOTE,cursor.getString(cursor.getColumnIndex(FIELD_NOTE)));
        return retVal;
    }

    @Override
    public int updateData(Bundle value) {
        int retVal = value.getInt(DBManager.FIELD_ID);
        if (value.getString(FIELD_NOTE).length()>LEN_BREAF_STRING) {
            value.putString(FIELD_NOTE,value.getString(FIELD_NOTE).substring(0,LEN_BREAF_STRING));
        }
        ContentValues contentValues= new ContentValues();
        contentValues.put(DBManager.FIELD_POS,(long)value.getInt(DBManager.FIELD_POS));
        contentValues.put(DBManager.FIELD_ID,(long)value.getInt(DBManager.FIELD_ID));
        contentValues.put(DBManager.FIELD_NOTE,value.getString(DBManager.FIELD_NOTE));

        if (retVal<1) {
            retVal = Integer.valueOf(mContext.getContentResolver().insert(content_uri,contentValues)
                    .getLastPathSegment());
        }else {
            mContext.getContentResolver().update(content_uri,contentValues,null,null);
        }
        return retVal;
    }

    @Override
    public void deleteData(Bundle value) {
        int id = value.getInt(DBManager.FIELD_ID);
        if (id >0) {
            mContext.getContentResolver().delete
                    (Uri.withAppendedPath(content_uri,String.valueOf(id)),null,null);
        }
    }

    private ArrayList<Bundle> parseCursor(Cursor cursor) {
        ArrayList<Bundle> data = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            int i = 1;
            Bundle bundle= new Bundle();
            bundle.putInt(FIELD_POS,i);
            bundle.putInt(FIELD_ID,cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            bundle.putString(FIELD_NOTE,cursor.getString(cursor.getColumnIndex(FIELD_NOTE)));
            data.add(bundle);
            while (!cursor.isLast()) {
                cursor.moveToNext();
                i++;
                bundle= new Bundle();
                bundle.putInt(FIELD_POS,i);
                bundle.putInt(FIELD_ID,cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
                bundle.putString(FIELD_NOTE,cursor.getString(cursor.getColumnIndex(FIELD_NOTE)));
                data.add(bundle);
            }
        }
        return data;
    }
}
