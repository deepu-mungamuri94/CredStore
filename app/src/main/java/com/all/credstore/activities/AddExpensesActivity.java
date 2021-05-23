package com.all.credstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.all.credstore.R;
import com.all.credstore.models.Expense;
import com.all.credstore.utils.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddExpensesActivity extends BaseActivity {

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ExpensesListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        Calendar cal = Calendar.getInstance();
        getDatePicker().init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
    }

    public void addExpense(View view) {
        saveExpense(view);
    }

    public void addExpenseNew(View view) {
        if(saveExpense(view)) {
            Intent toPage = new Intent(this, AddExpensesActivity.class);
            startActivity(toPage);
        }
    }

    public void cancel(View view) {
        onBackPressed();
    }

    private boolean saveExpense(View view) {
        String costString = getTextValue(R.id.expense_cost_field);
        Long costValue = 0L;
        try {
            costValue = Long.parseLong(costString);
        } catch (NumberFormatException ex) {
            setErrMsgAndFocusField(R.string.INVALID_COST, R.id.expense_cost_field);
            return false;
        }

        String comment = getTextValue(R.id.expense_comments_field);
        if(Util.isEmpty(comment)) {
            setErrMsgAndFocusField(R.string.EXPENSE_COST_COMMENT, R.id.expense_comments_field);
            return false;
        }

        DatePicker picker = getDatePicker();
        Date expenseDate = new GregorianCalendar(picker.getYear(), picker.getMonth()-1, picker.getDayOfMonth()).getTime();

        Expense expense = new Expense(costValue, comment, expenseDate.getTime());
        getDataService().saveOrUpdateExpense(expense);

        makeToast(getString(R.string.EXPENSE_SAVED));
        return true;
    }

    private void setErrMsgAndFocusField(int msgId, int editViewId) {
        setText(R.id.add_expense_error_view, getString(msgId));
        focusView(editViewId);
    }

    private DatePicker getDatePicker() {
        return (DatePicker)findViewById(R.id.expense_date_field);
    }
}
