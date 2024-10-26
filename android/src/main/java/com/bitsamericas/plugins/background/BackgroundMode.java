package com.bitsamericas.plugins.background;

import android.util.Log;

public class BackgroundMode {

    public String echo(String value) {
        Log.i("Echo DESDE EL PLUGIN ----------> ", value);
        return value;
    }
}
