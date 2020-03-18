package com.example.vfarm;

import android.content.Context;

import io.objectbox.BoxStore;

public class ObjectBox {

    private static BoxStore box;

    public static void init(Context context)
    {
        box = MyObjectBox.builder().androidContext(context.getApplicationContext()).build();
    }

    public static BoxStore get(){
        return box;
    }
}
