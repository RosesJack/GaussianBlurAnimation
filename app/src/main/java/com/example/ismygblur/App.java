package com.example.ismygblur;

import android.app.Application;
import android.content.Context;

/**
 * auther：wzy
 * date：2016/12/10 01 :02
 * desc:
 */

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
