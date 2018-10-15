package com.nbank.study;

import android.content.Context;
import android.support.multidex.MultiDex;

import ico.ico.ico.BaseApplication;

public class MyApplication extends BaseApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
