package com.all.credstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.all.credstore.R;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void showCredentialPage(View view) {
        startActivity(new Intent(this, CredentialListActivity.class));
    }

    public void showExpensePage(View view) {
        startActivity(new Intent(this, AddExpensesActivity.class));
    }
}
