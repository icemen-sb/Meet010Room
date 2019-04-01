package ru.relastic.meet010room;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileManagerLocal extends MyFileManager {

    MyFileManagerLocal(Context context, FileListeners listeners, String id, int type, String data) {
        super(context, listeners, id, type, data);
        Log.v("MyFileManager:","Created instatce of MyFileManagerLocal class.");
    }

    @Override
    public void mWorkedThread() {
        File mFileDir = mContext.getFilesDir();
        String fileName = mId+FILENAME_SUFFIX;
        String retVal="";
        File file = new File(mFileDir,fileName);
        Boolean completed = false;
        switch (typeProcess) {
            case RUN_TYPE_READ:
                FileReader fr = null;
                try {
                    if (!file.exists()) {file.createNewFile();}
                    fr = new FileReader(file);
                    int c;
                    while((c=fr.read())!=-1){
                        retVal +=((char)c);
                    }
                } catch (IOException e) {
                    retVal = "";
                    if (fr != null) {
                        try {fr.close();} catch (IOException e1) {e1.printStackTrace();}
                    }
                    e.printStackTrace();
                }
                mFileListeners.readed(retVal);
                break;
            case RUN_TYPE_WRITE:
                FileWriter fw = null;
                try {
                    if (!file.exists()) {file.createNewFile();}
                    fw = new FileWriter(file);
                    for (char c : mData.toCharArray()) {
                        fw.write((int)c);
                    }
                    fw.close();
                    completed = true;
                }catch (IOException e) {
                    if (fw != null) {
                        try {fw.close();} catch (IOException e1) {e1.printStackTrace();}
                    }
                    e.printStackTrace();
                }
                mFileListeners.writed(completed);
                break;
            case RUN_TYPE_DELETE:
                if (file.exists()) {
                    completed = file.delete();
                }
                mFileListeners.deleted(completed);
                break;
        }
        state = true;
    }
}
