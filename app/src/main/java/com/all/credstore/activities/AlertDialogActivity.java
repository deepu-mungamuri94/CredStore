package com.all.credstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.all.credstore.R;
import com.all.credstore.services.biometric.DeviceBiometrics;
import com.all.credstore.services.biometric.SuccessRedirector;
import com.all.credstore.utils.Util;

public class AlertDialogActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        moveBack(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.alert_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setText(R.id.alert_title, getString(R.string.ALERT_DAILOG_SECRET_TTLE));

        String statusMsg = (String)getRequestAttribute(ERROR_MSG_ATTR_NAME, "");
        setText(R.id.alert_message, statusMsg);

        showBiometricPromptIfSupports();
    }

    public void moveForward(View view) {
        String inputSecret = getTextValue(R.id.alert_secret_value);
        if(Util.isEmpty(inputSecret)) {
            setText(R.id.alert_message, getString(R.string.EMPTY_SECRET_KEY));
            return;
        }

        redirectToViewPage(false, inputSecret);
    }

    public void moveBack(View view) {
        Intent list = new Intent(this, CredentialListActivity.class);
        startActivity(list);
    }

    private void showBiometricPromptIfSupports() {
        new DeviceBiometrics().validate(this, new SuccessRedirector() {
            @Override
            public void toIntent() {
                redirectToViewPage(true, null);
            }
        });
    }

    private void redirectToViewPage(boolean validatedWithBiometric, String inputSecret) {
        Intent viewPage = new Intent(this, ViewCredentialActivity.class);
        viewPage.putExtra(RECORD_ID_REQUEST_ATTR_NAME, (int)getRequestAttribute(RECORD_ID_REQUEST_ATTR_NAME, 0));
        if(validatedWithBiometric) {
            viewPage.putExtra(BIOMTR_VALID_ATTR_NAME, true);
        } else {
            viewPage.putExtra(VALIDATED_REQUEST_ATTR_NAME, true);
            viewPage.putExtra(USER_INPUT_SECRET_ATTR_NAME, inputSecret);
        }
        startActivity(viewPage);
    }
}
