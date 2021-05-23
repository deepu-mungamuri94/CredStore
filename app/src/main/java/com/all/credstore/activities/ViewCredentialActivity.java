package com.all.credstore.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;

import com.all.credstore.models.Credentials;
import com.all.credstore.R;
import com.all.credstore.utils.Util;

public class ViewCredentialActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        listView(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int recordId = (Integer)getRequestAttribute(RECORD_ID_REQUEST_ATTR_NAME, 0);
        Credentials credential = getDataService().getEntry(recordId);
        if(credential != null) {
            loadViewPage(credential);
            return;
        }

        setContentView(R.layout.activity_detail_view);
        setVisibility(R.id.view_cred_error, View.VISIBLE);
        setVisibility(R.id.details_table, View.GONE);
        setVisibility(R.id.view_cred_edit_btn, View.GONE);
        setVisibility(R.id.view_cred_delete_btn, View.GONE);
    }

    private void prepareViewPage(Credentials model) {
        setContentView(R.layout.activity_detail_view);

        ((ImageView)findViewById(R.id.detail_view_icon)).setImageResource(model.getImageResourceId());
        setText(R.id.tag_field_value, model.getTag());
        setText(R.id.url_field_value, model.getUrl());
        setText(R.id.username_field_value, model.getUsername());
        setText(R.id.pwd_field_value, model.getPassword());
        setText(R.id.comments_field_value, model.getComment());
        if(!Util.isEmpty(model.getSecretKey())) {
            setText(R.id.secret_key_field_value, model.getSecretKey());
        }
    }

    private void loadViewPage(final Credentials model) {
        // Is it after updateRequest service (or) credentials didn't enabled secretKey
        boolean updateRequest = (Boolean) getRequestAttribute(EDIT_REQUEST_ATTR_NAME, false);
        boolean biometricValidated = (Boolean) getRequestAttribute(BIOMTR_VALID_ATTR_NAME, false);
        if(updateRequest || Util.isEmpty(model.getSecretKey()) || biometricValidated) {
            prepareViewPage(model);
            return;
        }

        // Validation completed from AlertDialogActivity
        String errorMsg = "";
        boolean validated = (Boolean) getRequestAttribute(VALIDATED_REQUEST_ATTR_NAME, false);
        String inputSecret = (String) getRequestAttribute(USER_INPUT_SECRET_ATTR_NAME, "");
        if(validated) {
            if(inputSecret.equals(model.getSecretKey())) {
                prepareViewPage(model);
                return;
            } else {
                errorMsg = getString(R.string.INVALID_SECRET_KEY);
            }
        }

        Intent secretKeyDailog = new Intent(this, AlertDialogActivity.class);
        secretKeyDailog.putExtra(ERROR_MSG_ATTR_NAME, errorMsg);
        secretKeyDailog.putExtra(RECORD_ID_REQUEST_ATTR_NAME, model.getId());
        startActivity(secretKeyDailog);
    }

    public void listView(View view) {
        Intent listView = new Intent(this, CredentialListActivity.class);
        startActivity(listView);
    }

    public void deleteDetails(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle(getString(R.string.CRED_DELETE_ACTION));
        builder.setMessage(getString(R.string.DELETE_CRED_CNFM_MSG));
        builder.setIcon(R.drawable.warning);

        builder.setPositiveButton(getString(R.string.DELETE), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDataService().deleteEntry(getRecordId());
                makeToast(getString(R.string.CRED_DELETED));
                listView(view);
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

    public void updateDetails(View view) {
        Intent editPage = new Intent(this, AddCredentialActivity.class);
        editPage.putExtra(EDIT_REQUEST_ATTR_NAME, true);
        editPage.putExtra(RECORD_ID_REQUEST_ATTR_NAME, getRecordId());
        startActivity(editPage);
    }
}
