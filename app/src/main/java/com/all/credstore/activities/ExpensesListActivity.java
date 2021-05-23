package com.all.credstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.all.credstore.R;

public class ExpensesListActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);
    }

    public void addExpense(View view) {
        startActivity(new Intent(this, AddExpensesActivity.class));
    }
}
