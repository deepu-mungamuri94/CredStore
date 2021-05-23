package com.all.credstore.activities;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.all.credstore.utils.Constants;
import com.all.credstore.utils.Util;

public class LifecycleHandler extends Application implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        return;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        try {
            if(Util.isLoginActivity || !Util.isMinimizationEnabled(this)) {
                return;
            }

            Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
            loginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginPage);
        } catch (Exception ex) {
            Log.i(Constants.LOG_TAG, "Exception in appForeground lifecycle: " + ex.getMessage());
        }
        return;
    }
}
