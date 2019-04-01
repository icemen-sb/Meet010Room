package ru.relastic.meet010room;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class MyFileManager implements Runnable{
    public static final int RUN_TYPE_READ = 0;
    public static final int RUN_TYPE_WRITE = 1;
    public static final int RUN_TYPE_DELETE = 2;
    public static final String FILENAME_SUFFIX = ".note";
    public static final String LOADING_STATE_SATB = "... loading file ...";


    protected final Context mContext;
    protected final FileListeners mFileListeners;
    protected final String mId;
    protected final String mData;
    protected final int typeProcess;

    protected boolean state = false;


    MyFileManager(Context context, FileListeners listeners, String id, int type, String data){
        mContext = context;
        mFileListeners = listeners;
        mId = id;
        typeProcess = type;
        mData = data;
    }
    public void startWorkedThread(){
        if (!state) {
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        mWorkedThread();
    }

    abstract protected void mWorkedThread();


    public static MyFileManager getInstance(Context context, FileListeners listeners,
                                            String id, int type, String data){

        MyFileManager retVal = null;
        String remoteFileManagerClassName = "MyFileManagerRemote";
        String localFileManagerClassName = "MyFileManagerLocal";
        String FileManagerClassName;
        String daoType = context.getString(R.string.dao_type);

        switch (daoType) {
            case "DBManagerContentProvider":
                FileManagerClassName = remoteFileManagerClassName;
                break;
            default:
                FileManagerClassName = localFileManagerClassName;
        }

        try {
            Class<?> target = Class.forName(context.getPackageName()+"." + FileManagerClassName);
            Constructor<?> constructor = target.getConstructor(Context.class, FileListeners.class,
                    String.class, int.class, String.class);
            retVal = (MyFileManager)constructor.newInstance(context,listeners,id, type, data);
        } catch (ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return retVal;
    }
    public static void cleanTrashLocalDir(Context context){
        //<...>
    }

    public interface FileListeners {
        public void readed(String text);
        public void writed(boolean completed);
        public void deleted(boolean completed);
    }
}
