package com.all.credstore.services.biometric;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class DeviceBiometrics {

    private static Boolean supports;

    public void validate(AppCompatActivity activityInstance, SuccessRedirector successRedirector) {
        if(activityInstance == null || successRedirector == null) {
            return;
        }

        // Prompt if and only if device supports
        if((supports != null && supports) || isSupported(activityInstance)) {
            BiometricPrompt biometricPrompt = prepareBioMetricPromptObj(activityInstance, successRedirector);
            BiometricPrompt.PromptInfo promptInfo = buildPrompt();
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private boolean isSupported(AppCompatActivity activityInstance) {
        BiometricManager biometricManager = BiometricManager.from(activityInstance);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                supports = true;
                break;
            default:
                break;
        }
        return supports;
    }

    private BiometricPrompt.PromptInfo buildPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Device's Biometric authentication")
                .setNegativeButtonText("Cancel & Use Lock code")
                .build();
    }

    private BiometricPrompt prepareBioMetricPromptObj(final AppCompatActivity activityInstance, final SuccessRedirector successRedirector) {
        Executor executor = ContextCompat.getMainExecutor(activityInstance.getApplicationContext());
        return new BiometricPrompt(activityInstance, executor, new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(activityInstance, "Authentication error", Toast.LENGTH_LONG);
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Toast.makeText(activityInstance, "Unlocked with device's biometrics", Toast.LENGTH_SHORT);
            successRedirector.toIntent();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Toast.makeText(activityInstance, "Authentication Failed", Toast.LENGTH_LONG);
        }
        });
    }
}
