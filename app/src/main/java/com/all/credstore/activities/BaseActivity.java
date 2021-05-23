package com.all.credstore.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.all.credstore.R;
import com.all.credstore.services.DataService;
import com.all.credstore.services.ExportImportService;
import com.all.credstore.utils.Constants;
import com.all.credstore.utils.Util;

public class BaseActivity extends AppCompatActivity implements Constants {

    private static DataService dataService;
    protected static ExportImportService exportImportService = new ExportImportService();
    protected synchronized DataService getDataService() {
        if(dataService == null) {
            dataService = new DataService(this);
        }
        return dataService;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Util.isLoginActivity = false;
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    public void hideKeyboard(View view) {
        try {
            this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception ex) {/* Ignore */}
    }

    public void showHideText(View view) {
        if(!(view instanceof ImageView)) {
            return;
        }
        ImageView imView = (ImageView)view;
        int textFieldId = getViewIdByIdString(view.getTag().toString());
        EditText editText = findViewById(textFieldId);

        // Password visible
        if(editText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imView.setImageResource(R.drawable.eye_close);
        } else {
            // Password invisible
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imView.setImageResource(R.drawable.eye_open);
        }

        int textPosition = getTextValue(textFieldId).length();
        Selection.setSelection(editText.getText(), textPosition);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        exportImportService.onRequestPermissionsResult(requestCode, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        exportImportService.onActivityResult(requestCode, resultCode, data, this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    protected Object getRequestAttribute(Intent intent, String attribueName, Object defaultValue) {
        Bundle requestAttributes = intent.getExtras();
        if(requestAttributes != null && requestAttributes.get(attribueName) != null) {
            return requestAttributes.get(attribueName);
        }
        return defaultValue;
    }

    protected Object getRequestAttribute(String attribueName, Object defaultValue) {
        return getRequestAttribute(getIntent(), attribueName, defaultValue);
    }

    protected int getRecordId() {
        return (int)getRequestAttribute(RECORD_ID_REQUEST_ATTR_NAME, 0);
    }

    protected void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void setText(int viewId, String data) {
        setText(findViewById(viewId), data);
    }

    protected void setText(View view, String data) {
        if(view instanceof EditText) {
            ((EditText) view).setText(data);
        } else if(view instanceof TextView) {
            ((TextView) view).setText(data);
        } else if(view instanceof  AutoCompleteTextView) {
            ((AutoCompleteTextView)view).setText(data);
        } else {
            return;
        }
        view.requestFocus();
    }

    protected String getTextValue(int viewId) {
        String value = null;
        View view = findViewById(viewId);
        if(view instanceof EditText) {
            value = ((EditText) view).getText().toString();
        } else if(view instanceof TextView) {
            value = ((TextView) view).getText().toString();
        } else if(view instanceof AutoCompleteTextView) {
            value = ((AutoCompleteTextView)view).getText().toString();
        }
        return value;
    }

    protected void focusView(int viewId) {
        findViewById(viewId).requestFocus();
    }

    protected void setVisibility(int viewId, int visibility) {
        findViewById(viewId).setVisibility(visibility);
    }

    protected void setViewMessage(int viewId, String data, boolean isError) {
        TextView view = findViewById(viewId);
        view.setText(data);
        view.setTextColor(getColor(isError ? R.color.errorColor : R.color.greenColor));
        view.requestFocus();
    }

    protected int getViewIdByIdString(String idString) {
        return getResources().getIdentifier(idString, ID, getPackageName());
    }

    public synchronized void showClickEventBackground(final View view) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    view.setBackgroundColor(getColor(R.color.clickBackColor));
                    Thread.sleep(100);
                    view.setBackgroundColor(Color.TRANSPARENT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    protected int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
