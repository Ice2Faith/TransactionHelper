package com.ugex.savelar.transactionhelper.Application;

import android.app.Application;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,"a49f1fe68daff2986f0315067899ae1c");
    }
}
