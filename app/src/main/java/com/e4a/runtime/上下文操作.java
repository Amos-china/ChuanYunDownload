package com.e4a.runtime;

import android.content.Context;

public class 上下文操作 {
    public static Context mApplicationContext;

    private 上下文操作() {
    }


    public static void 置全局上下文(Context context) {
        mApplicationContext = context;
    }


    public static Context 取全局上下文() {
        return mApplicationContext;
    }

}
