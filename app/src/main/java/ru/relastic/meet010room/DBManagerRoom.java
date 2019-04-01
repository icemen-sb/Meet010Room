package ru.relastic.meet010room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Update;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBManagerRoom extends DBManager{

    private final NotesDatabase mNotesDatabase;
    private final NotesDAO mNotesDAO;

    public DBManagerRoom(Context context) {
        super(context);
        Log.v("DBManager:","Creatind instatce of DBManagerRoom class ...");
        mNotesDatabase = Room.databaseBuilder(context, NotesDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        mNotesDAO = mNotesDatabase.getNotesDAO();
        if (mNotesDatabase!=null && mNotesDatabase!=null) {
            Log.v("DBManager:","Created instatce of DBManagerRoom class.");
        }else {
            Log.v("DBManager: ERROR","ERROR creating instatce of DBManagerRoom class.");
        }
    }

    @Override
    public ArrayList<Bundle> getData(){
        ArrayList<Bundle> data = new ArrayList<>();
        int i = 0;
        for (Note note : mNotesDAO.getDataNotes()) {
            i++;
            Bundle item = note.getData();
            item.putInt(FIELD_POS,i);
            data.add(item);
        }
        return data;
    }
    @Override
    public Bundle getDataById(int id){
        return mNotesDAO.getDataById(id).getData();
    }
    @Override
    public int updateData(Bundle value) {
        int retval = value.getInt(FIELD_ID);
        if (value.getString(FIELD_NOTE).length()>LEN_BREAF_STRING) {
            value.putString(FIELD_NOTE,value.getString(FIELD_NOTE).substring(0,LEN_BREAF_STRING));
        }

        Note note = new Note(value);

        if (retval == 0) {
            long newID = mNotesDAO.insertData(note);
            retval = (int)newID;
        } else {
            mNotesDAO.updateData(note);
        }
        return retval;
    }
    @Override
    public void deleteData(Bundle value) {
        if (value.getString(FIELD_NOTE).length()>LEN_BREAF_STRING) {
            value.putString(FIELD_NOTE,value.getString(FIELD_NOTE).substring(0,LEN_BREAF_STRING));
        }
        Note note = new Note(value);
        mNotesDAO.deleteData(note);
    }

    @Entity(tableName = TABLE_NAME, indices = {@Index("id")})
    public static class Note {

        @Ignore
        private int pos=0;

        @PrimaryKey (autoGenerate = true)
        private int id;

        private String note;

        public Note(){

        }
        public Note (Bundle note) {
            pos = note.getInt(DBManager.FIELD_POS);
            int id = note.getInt(DBManager.FIELD_ID);
            if (id > 0) {
                this.id = id;
            }
            this.note = note.getString(DBManager.FIELD_NOTE);
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
        public String getNote() {
            return note;
        }
        public void setNote(String note) {
            this.note = note;
        }
        public Bundle getData(){
            Bundle retVal = new Bundle();
            retVal.putInt(DBManager.FIELD_POS,pos);
            retVal.putInt(DBManager.FIELD_ID,id);
            retVal.putString(DBManager.FIELD_NOTE,note);
            return retVal;
        }
    }

    @Dao
    public abstract static class NotesDAO {
        @Query("select * from notes")
        public abstract List<Note> getDataNotes();

        @Query("select * from notes where id = :id")
        public abstract Note getDataById(int id);

        @Update
        public abstract void updateData(Note data);

        @Insert
        public abstract long insertData(Note data);

        @Delete
        public abstract void deleteData(Note data);
    }

    @Database(entities = {Note.class}, version = VERSION_DB)
    public abstract static class NotesDatabase extends RoomDatabase {
        public abstract NotesDAO getNotesDAO();
    }
}
