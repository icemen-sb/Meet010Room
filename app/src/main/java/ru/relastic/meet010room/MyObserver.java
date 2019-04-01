package ru.relastic.meet010room;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class MyObserver extends ContentObserver {
    public static final int MESSAGE_WHAT_REDRAW_UI = 1000;
    private final Handler mHandler;

    public MyObserver(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (!(selfChange && !deliverSelfNotifications())) {
            Message msg = Message.obtain(mHandler, MESSAGE_WHAT_REDRAW_UI);
            mHandler.sendMessage(msg);
        }
    }
}
