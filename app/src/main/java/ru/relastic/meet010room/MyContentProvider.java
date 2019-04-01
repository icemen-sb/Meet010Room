package ru.relastic.meet010room;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyContentProvider extends ContentProvider {
    //private static final String AUTHORITY = "ru.relastic.meet010room.provider";
    private static final String AUTHORITY = MyContentProvider.class.getPackage().getName() + ".provider";

    private static final String FILE_SOURCE_PATH = "files";
    private static final String FILE_SOURCE_PATH_ID = FILE_SOURCE_PATH + "/#";
    private static final String TABLE_SOURCE_PATH = DBManager.TABLE_NAME;
    private static final String TABLE_SOURCE_PATH_ID = TABLE_SOURCE_PATH + "/#";

    private static final Uri content_uri = Uri.parse("content://" + AUTHORITY + "/" + TABLE_SOURCE_PATH);

    public static final int REQUEST_FILE_SOURCE = 1;
    public static final int REQUEST_FILE_SOURCE_ID = 2;
    public static final int REQUEST_NOTE_TABLE = 3;
    public static final int REQUEST_NOTE_TABLE_ID = 4;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY,FILE_SOURCE_PATH,REQUEST_FILE_SOURCE);
        uriMatcher.addURI(AUTHORITY,FILE_SOURCE_PATH_ID,REQUEST_FILE_SOURCE_ID);
        uriMatcher.addURI(AUTHORITY,TABLE_SOURCE_PATH,REQUEST_NOTE_TABLE);
        uriMatcher.addURI(AUTHORITY,TABLE_SOURCE_PATH_ID,REQUEST_NOTE_TABLE_ID);
    }

    private DBManagerSQLite mLocalDatabase;
    private static MyObserver mMyObserver = null;

    @Override
    public boolean onCreate() {
        mLocalDatabase = new DBManagerSQLite(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retVal;

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case REQUEST_NOTE_TABLE:
                retVal = mLocalDatabase.getDataCursor(0);
                break;
            case REQUEST_NOTE_TABLE_ID:
                retVal = mLocalDatabase.getDataCursor(Integer.valueOf(uri.getLastPathSegment()));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri.toString());
        }
        retVal.setNotificationUri(getContext().getContentResolver(),content_uri);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Bundle data = new Bundle();
        data.putInt(DBManager.FIELD_POS,values.getAsInteger(DBManager.FIELD_POS));
        data.putInt(DBManager.FIELD_ID,values.getAsInteger(DBManager.FIELD_ID));
        data.putString(DBManager.FIELD_NOTE,values.getAsString(DBManager.FIELD_NOTE));
        mLocalDatabase.updateData(data);
        getContext().getContentResolver().notifyChange(content_uri,mMyObserver);
        return 1;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        ParcelFileDescriptor retVal = null;
        int uriType = uriMatcher.match(uri);
        int id = Integer.valueOf(uri.getLastPathSegment());
        if ((uriType != REQUEST_FILE_SOURCE_ID) && (id < 1)) {
                throw new IllegalArgumentException("Unknown URI: "+uri.toString());
        }

        File mFileDir = getContext().getFilesDir();
        String fileName = String.valueOf(id) + MyFileManager.FILENAME_SUFFIX;
        File file = new File(mFileDir, fileName);
        int modeInt;
        switch (mode) {
            case "r":
                modeInt = ParcelFileDescriptor.MODE_READ_ONLY;
                break;
            case "w":
                modeInt = ParcelFileDescriptor.MODE_WRITE_ONLY;
                break;
            case "rw":
                modeInt = ParcelFileDescriptor.MODE_READ_WRITE;
                break;
            default:
                modeInt = ParcelFileDescriptor.MODE_READ_WRITE;
        }

        try {
            if (!file.exists()) {file.createNewFile();}
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.
                    open(file,modeInt);
            retVal = parcelFileDescriptor;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retVal;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Bundle data = new Bundle();
        data.putInt(DBManager.FIELD_POS,values.getAsInteger(DBManager.FIELD_POS));
        data.putInt(DBManager.FIELD_ID,values.getAsInteger(DBManager.FIELD_ID));
        data.putString(DBManager.FIELD_NOTE,values.getAsString(DBManager.FIELD_NOTE));
        int newId = mLocalDatabase.updateData(data);
        Uri resultUri = ContentUris.withAppendedId(content_uri,(long)newId);
        getContext().getContentResolver().notifyChange(content_uri,mMyObserver);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int retVal = 0;
        int uriType = uriMatcher.match(uri);
        int id;
        switch (uriType) {
            case REQUEST_NOTE_TABLE_ID:
                id = Integer.valueOf(uri.getLastPathSegment());
                Bundle data = new Bundle();
                data.putInt(DBManager.FIELD_POS,0);
                data.putInt(DBManager.FIELD_ID,id);
                data.putString(DBManager.FIELD_NOTE,"");
                mLocalDatabase.deleteData(data);
                getContext().getContentResolver().notifyChange(content_uri,mMyObserver);
                retVal = 1;
                break;
            case REQUEST_FILE_SOURCE_ID:
                id = Integer.valueOf(uri.getLastPathSegment());
                if (id>0) {
                    File mFileDir = getContext().getFilesDir();
                    String fileName = String.valueOf(id) + MyFileManager.FILENAME_SUFFIX;
                    File file = new File(mFileDir, fileName);
                    if (file.delete()) {retVal = 1;}
                    getContext().getContentResolver().notifyChange(content_uri,mMyObserver);
                    break;
                }
            default:
                throw new IllegalArgumentException("Unknown URI or action not support");
        }
        return retVal;
    }
}
