package com.all.credstore.activities;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.all.credstore.models.Credentials;
import com.all.credstore.R;
import com.all.credstore.services.DataService;
import com.all.credstore.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class AddCredentialActivity extends BaseActivity {

    @Override
    public void onBackPressed() { cancel(null); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        AutoCompleteTextView urlField = findViewById(R.id.url_field);
        urlField.setAdapter(getURLSuggestions());

        if((Boolean)getRequestAttribute(EDIT_REQUEST_ATTR_NAME, false)) {
            prepareUpdateForm();
        }
    }

    public void addCredential(View view) {
        String tag = getTextValue(R.id.tag_field);
        String url = getTextValue(R.id.url_field);
        String uname = getTextValue(R.id.username_field);
        String pwd = getTextValue(R.id.pwd_field);
        boolean restrictView = ((CheckBox)findViewById(R.id.enable_secretkey_field)).isChecked();
        String secretKey = getTextValue(R.id.secret_key_field);
        String comment = getTextValue(R.id.comments_field);

        if(Util.isEmpty(tag)) {
            setErrMsgAndFocusField(R.string.TAG_EMPTY_MSG, R.id.tag_field);
        } else if(Util.isEmpty(uname)) {
            setErrMsgAndFocusField(R.string.UNAME_EMPTY_MSG, R.id.username_field);
        } else if(Util.isEmpty(pwd)) {
            setErrMsgAndFocusField(R.string.PWD_EMPTY_MSG, R.id.pwd_field);
        } else if(restrictView && Util.isEmpty(secretKey)) {
            setErrMsgAndFocusField(R.string.SECRET_KEY_RQRD, R.id.secret_key_field);
        } else {
            Intent toPage = getViewPageByContext();

            int recordId = (Integer)getRequestAttribute(toPage, RECORD_ID_REQUEST_ATTR_NAME, 0);
            Credentials credential = new Credentials(recordId, tag, uname, pwd, url, (restrictView? secretKey : null), comment);
            getDataService().addOrUpdateEntry(credential);

            makeToast(getString((credential.getId() > 0)? R.string.CRED_UPDATED : R.string.CRED_CREATED));
            startActivity(toPage);
        }
    }

    public void enableSecretKeyField(View view) {
        CheckBox checkBox = findViewById(R.id.enable_secretkey_field);
        setVisibility(R.id.secret_key_field_row, (checkBox.isChecked()? View.VISIBLE : View.GONE));
    }

    public void cancel(View view) {
        boolean editRequest = (Boolean) getRequestAttribute(EDIT_REQUEST_ATTR_NAME, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle(getString(editRequest ? R.string.CRED_UPDATE_ACTION : R.string.CRED_CREATE_ACTION));
        builder.setMessage(getString(R.string.CONFIRM_EXIT_MSG));
        builder.setIcon(R.drawable.warning);

        builder.setPositiveButton(getString(R.string.CONFIRM), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(getViewPageByContext());
                Toast.makeText(getApplicationContext(), getString(R.string.CANCELLED), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void prepareUpdateForm() {
        setText(R.id.add_cred_heading, getString(R.string.UPDATE_CREDENTIALS_HEADING));

        Credentials credential = getDataService().getEntry(getRecordId());
        setText(R.id.tag_field, credential.getTag());
        setText(R.id.url_field, credential.getUrl());
        setText(R.id.username_field, credential.getUsername());
        setText(R.id.pwd_field, credential.getPassword());
        setText(R.id.comments_field, credential.getComment());

        boolean restrictView = false;
        int visibility = View.GONE;
        if(!Util.isEmpty(credential.getSecretKey())) {
            restrictView = true;
            visibility = View.VISIBLE;
            setText(R.id.secret_key_field, credential.getSecretKey());
        }
        setVisibility(R.id.secret_key_field_row, visibility);
        ((CheckBox)findViewById(R.id.enable_secretkey_field)).setChecked(restrictView);
    }

    private Intent getViewPageByContext() {
        int recordId = getRecordId();
        Intent toPage = null;

        if(recordId > 0) {
            toPage = new Intent(this, ViewCredentialActivity.class);
            toPage.putExtra(RECORD_ID_REQUEST_ATTR_NAME, recordId);
            toPage.putExtra(EDIT_REQUEST_ATTR_NAME, true);
        } else {
            toPage = new Intent(this, CredentialListActivity.class);
        }
        return toPage;
    }

    private void setErrMsgAndFocusField(int msgId, int editViewId) {
        setText(R.id.add_cred_error_view, getString(msgId));
        focusView(editViewId);
    }

    private ArrayAdapter<String> getURLSuggestions() {
        List<String> urls = getDataService().getDistinctURLValues();
        return new ArrayAdapter<String>(this, R.layout.dropdown, urls);
    }
}
