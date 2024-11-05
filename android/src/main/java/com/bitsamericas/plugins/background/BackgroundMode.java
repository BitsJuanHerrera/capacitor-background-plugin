package com.juankmiloh.plugins.background;

import android.util.Log;

public class BackgroundMode {

    public String echo(String value) {
        Log.i("Echo MSG DEL PLUGIN", value);
        return value;
    }
}
