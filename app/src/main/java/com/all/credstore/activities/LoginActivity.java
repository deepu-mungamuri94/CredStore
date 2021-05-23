package com.all.credstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.all.credstore.R;
import com.all.credstore.services.biometric.DeviceBiometrics;
import com.all.credstore.services.biometric.SuccessRedirector;
import com.all.credstore.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private List<Integer> passCodeDigts = new ArrayList<>();
    private boolean showPasscode = false;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Util.isLoginActivity = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reset();
        if(!getDataService().pinRegistered()) {
            setViewMessage(R.id.login_msg_view, getString(R.string.REGISTER_PIN_MSG), false);
        } else if(updatePINRequest()) {
            setViewMessage(R.id.login_msg_view, getString(R.string.UPDATE_PIN_MSG), false);
        } else {
            showBiometricPromptIfSupports();
        }
    }

    public void capturePin(View view) {
        showClickEventBackground(view);
        setViewMessage(R.id.login_msg_view, "", true);
        if(passCodeDigts.size() == MAX_PIN_SIZE) {
            return;
        }
        Button numButton = findViewById(view.getId());
        int number = Integer.parseInt(numButton.getText().toString());
        passCodeDigts.add(number);
        updatePINView();
        submitPIN(view);
    }

    public void submitPIN(View view) {
        if(passCodeDigts.size() != MAX_PIN_SIZE) {
            if(view.getId() == R.id.button_ok) {
                setViewMessage(R.id.login_msg_view, getString(R.string.INCOMPLETE_PIN_MSG), true);
            }
            return;
        }
        String pin = Util.convertListToString(passCodeDigts);
        boolean updatePINRequest = updatePINRequest();
        if(!getDataService().pinRegistered() || updatePINRequest) {
            getDataService().savePIN(pin);
            setViewMessage(R.id.login_msg_view, getString(updatePINRequest? R.string.PIN_UPDATED_MSG : R.string.PIN_REGISTERED_MSG), false);
            getIntent().putExtra(CHANGE_PIN_REQUEST_ATTR_NAME, false);
        } else {
            if(!getDataService().isValidPIN(pin)) {
                setViewMessage(R.id.login_msg_view, getString(R.string.INVALID_PIN), true);
            } else {
                showList();
            }
        }
        reset();
    }

    public void clearPin(View view) {
        if(passCodeDigts.isEmpty()) {
            return;
        }

        passCodeDigts.remove(passCodeDigts.size() - 1);
        updatePINView();
    }

    public void showHidePin(View view) {
        showPasscode = !showPasscode;
        updatePINView();
        ImageView eyeView = findViewById(R.id.show_hide_pin_view);
        eyeView.setImageResource(showPasscode ? R.drawable.eye_close : R.drawable.eye_open);
    }

    private void showList() {
        Intent listView = new Intent(this, HomeActivity.class);
        startActivity(listView);
    }

    private void updatePINView() {
        for(int i=0; i<MAX_PIN_SIZE; i++) {
            TextView pinView = findViewById(getViewIdByIdString("pin_holder_"+(i+1)));
            pinView.setBackground(getDrawable(R.drawable.circle));
            pinView.setText(null);
            pinView.setBackgroundTintList(null);
            if(passCodeDigts.size() > i) {
                pinView.setBackgroundTintList(getResources().getColorStateList(R.color.appTextColor, null));
                if(showPasscode) {
                    pinView.setBackgroundTintList(getResources().getColorStateList(R.color.blackColor, null));
                    pinView.setText(String.valueOf(passCodeDigts.get(i)));
                }
            }
        }
    }

    private boolean updatePINRequest() {
        return (Boolean)getRequestAttribute(CHANGE_PIN_REQUEST_ATTR_NAME, false);
    }

    private void reset() {
        passCodeDigts.clear();
        updatePINView();
    }

    private void showBiometricPromptIfSupports() {
        new DeviceBiometrics().validate(this, new SuccessRedirector() {
            @Override
            public void toIntent() {
                showList();
            }
        });
    }
}
