package com.study.bugs;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    private Utils() {

    }

    public static int loadSettings(String key, Context cx) {
        SharedPreferences p = cx.getSharedPreferences("records", 0);
        return p.getInt(key, 0);
    }


    public static void saveSettings(int val, Context cx) {

        SharedPreferences p = cx.getSharedPreferences("records", 0);
        SharedPreferences.Editor editor = p.edit();

        editor.putInt("record", val);
        editor.apply();
    }
}
