package com.nbank.study;

import android.support.multidex.MultiDex;

import ico.ico.ico.BaseApplication;

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
