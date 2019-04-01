package ru.relastic.meet010room;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class DBManagerSQLite extends DBManager{
    private final DbHelper dbhelper;

    public DBManagerSQLite(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        if (dbhelper!=null) {
            Log.v("DBManager:","Createing instatce of DBManagerSQLite class class...");
        }else {
            Log.v("DBManager: ERROR","ERROR creating instatce of DBManagerRoom class.");
        }
        Log.v("DBManager:","Created instatce of DBManagerSQLite class.");
    }
    public ArrayList<Bundle> getData(){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<Bundle> data = new ArrayList<>();

        try {
            db = dbhelper.getReadableDatabase();
            cursor = db.query(TABLE_NAME,null,null,null,null,null,FIELD_ID);
            data.addAll(parseCursor(cursor));
        }catch (SQLException e) {
            Log.v("SQLiteException", e.getMessage());
        }finally {
            if (db != null && cursor !=null) {
                cursor.close();
                db.close();
            }
        }
        return data;
    }
    public Cursor getDataCursor(int id){
        Cursor retVal = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbhelper.getReadableDatabase();

            String selection = null;
            String[] selectionArgs = null;
            if (id>0) {
                selection = FIELD_ID+"=?";
                selectionArgs = new String[] { String.valueOf(id) };
            }
            cursor = db.query(TABLE_NAME,null,selection,selectionArgs
                    ,null,null,null);
            retVal = cursor;
        }catch (SQLException e) {
            Log.v("SQLiteException", e.getMessage());
            if (db != null && cursor != null) {
                cursor.close();
                db.close();
            }
        }finally {}
        return retVal;
    }
    public Bundle getDataById(int id){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Bundle retVal = null;
        try {
            db = dbhelper.getReadableDatabase();
            cursor = db.query(TABLE_NAME,null,FIELD_ID+"=?",
                    new String[] { String.valueOf(id) },null,null,null);
            retVal = new Bundle();
            retVal.putInt(FIELD_POS,1);
            cursor.moveToFirst();
            retVal.putInt(FIELD_ID,cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
            retVal.putString(FIELD_NOTE,cursor.getString(cursor.getColumnIndex(FIELD_NOTE)));

        }catch (SQLException e) {
            Log.v("SQLiteException", e.getMessage());
        }finally {
            if (db != null && cursor !=null) {
                cursor.close();
                db.close();
            }
        }
        return retVal;
    }
    public int updateData(Bundle value) {
        int retval = 0;
        int id = value.getInt(FIELD_ID);
        if (value.getString(FIELD_NOTE).length()>LEN_BREAF_STRING) {
            value.putString(FIELD_NOTE,value.getString(FIELD_NOTE).substring(0,LEN_BREAF_STRING));
        }
        SQLiteDatabase db = null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NOTE,value.getString(FIELD_NOTE));

        try {
            db = dbhelper.getWritableDatabase();
            db.beginTransaction();
            if (id >0) {
                //update
                db.update(TABLE_NAME, contentValues, FIELD_ID + "=?",
                        new String[]{String.valueOf(id)});
                retval = value.getInt(FIELD_ID);
            }else {
                //insert
                db.insert(TABLE_NAME, null, contentValues);
                Cursor cursor = db.query(TABLE_NAME,null,null,
                        null,null,null,FIELD_ID);
                cursor.moveToLast();
                id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            }
            db.setTransactionSuccessful();
            retval=id;
        }catch (SQLiteException e) {
            Log.v("SQLiteException",e.getMessage());
        }finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
        return retval;
    }

    @Override
    public void deleteData(Bundle value) {

        int id = value.getInt(FIELD_ID);
        SQLiteDatabase db = null;

        try {
            db = dbhelper.getWritableDatabase();
            //delete
            db.delete(TABLE_NAME, FIELD_ID + "=?", new String[]{String.valueOf(id)});
        }catch (SQLiteException e) {
            Log.v("SQLiteException",e.getMessage());
        }finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
    }

    private static ArrayList<Bundle> parseCursor(Cursor cursor) {
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

    public class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            this(context, DB_NAME, null, VERSION_DB);
        }

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            deleteTables(db);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        private void createTables(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+TABLE_NAME+" (" + FIELD_ID + " integer primary key autoincrement, " +
                    FIELD_NOTE + " text)");
        }

        private void deleteTables(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        }
    }
}
