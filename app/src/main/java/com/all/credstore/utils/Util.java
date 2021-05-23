package com.all.credstore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.List;

public class Util {

    public static boolean isLoginActivity;

    public static String convertListToString(List<Integer> elements) {
        StringBuilder sb = new StringBuilder();
        for(Integer element : elements) {
            sb.append(element);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String input) {
        return (input == null || input.trim().isEmpty());
    }

    public static String reverse(String input) {
        if(isEmpty(input)) {
            return input;
        }
        StringBuilder builder = new StringBuilder(input);
        builder = builder.reverse();
        return builder.toString();
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Boolean isMinimizationEnabled(Context context) {
        return getSharedPrefs(context).getBoolean(Constants.MENU_ITEM_MINMZ_CHKBOX, false);
    }

    public static void storeDataInSharedPreferences(String key, Object value, Context context) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        if(value instanceof Boolean) {
            editor.putBoolean(key, (Boolean)value);
        }
        editor.commit();
    }
}
