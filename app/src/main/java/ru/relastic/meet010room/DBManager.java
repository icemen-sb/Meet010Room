package ru.relastic.meet010room;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class DBManager {
    //private static final String DAO_TYPE_DEFAULT = "DBManagerSQLite";
    public static final String TABLE_NAME="notes";
    public static final String FIELD_POS="pos";
    public static final String FIELD_ID="id";
    public static final String FIELD_NOTE="note";
    public static final int LEN_BREAF_STRING = 32;

    public static final String DB_NAME = "database.db";
    public static final int VERSION_DB = 1;

    protected final Context mContext;


    public DBManager(Context context) {
        mContext = context;
    }

    public abstract ArrayList<Bundle> getData();

    public abstract Bundle getDataById(int id);

    public abstract int updateData(Bundle value);

    public abstract void deleteData(Bundle value);

    public void registerObserver(Uri uri, boolean notifyForDescendats, MyObserver observer){
        mContext.getContentResolver().registerContentObserver(uri,notifyForDescendats,observer);
        Log.v("DBManager","ContentProvider OBSERVER reg to " + uri.toString());
    }
    public void unregisterObserver(ContentObserver observer){
        mContext.getContentResolver().unregisterContentObserver(observer);
        Log.v("DBManager","ContentProvider OBSERVER UNreg");
    }

    public static  final DBManager getInstance(Context context) {
        DBManager retVal = null;
        String daoType = context.getString(R.string.dao_type);

        try {
            Class<?> target = Class.forName(context.getPackageName()+"." + daoType);
            Constructor<?> constructor = target.getConstructor(Context.class);
            retVal = (DBManager)constructor.newInstance(context);
        } catch (ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return retVal;
    }
}
