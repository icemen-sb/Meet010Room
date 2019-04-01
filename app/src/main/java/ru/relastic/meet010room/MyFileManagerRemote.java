package ru.relastic.meet010room;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileManagerRemote extends MyFileManager {
    private final static String FILE_SOURCE_PATH = "files";
    private final Uri target_uri;

    MyFileManagerRemote(Context context, FileListeners listeners, String id, int type, String data) {
        super(context, listeners, id, type, data);
        String authority_remote = context.getString(R.string.content_provider_authorities_remote);
        target_uri = Uri.parse("content://" + authority_remote + "/" + FILE_SOURCE_PATH);
        Log.v("MyFileManager:","Created instatce of MyFileManagerRemote class.");
    }

    @Override
    protected void mWorkedThread() {
        String retVal="";
        Boolean completed = false;
        Uri target_uri_id = Uri.withAppendedPath(target_uri,mId);
        switch (typeProcess) {
            case RUN_TYPE_READ:
                FileReader fr = null;
                try {
                    FileDescriptor fd = mContext.getContentResolver().
                            openFileDescriptor(target_uri_id, "r").getFileDescriptor();
                    fr = new FileReader(fd);
                    int count;
                    char[] buffer = new char[1024];
                    while((count=fr.read(buffer))>0){
                        retVal +=String.valueOf(buffer,0,count);
                    }
                    fr.close();
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
                    if (!(mContext.getContentResolver().
                            delete(target_uri_id,null,null)==1)) {
                        throw new IOException("error delete file with uri: "+ target_uri_id.toString());
                    }

                    FileDescriptor fd = mContext.getContentResolver().
                            openFileDescriptor(target_uri_id, "w").getFileDescriptor();
                    fw = new FileWriter(fd);
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
                completed = (mContext.getContentResolver().
                        delete(target_uri_id,null,null)==1);
                mFileListeners.deleted(completed);
                break;
        }
        state=true;
    }
}
