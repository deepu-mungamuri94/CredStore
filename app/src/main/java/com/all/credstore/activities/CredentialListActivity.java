package com.all.credstore.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.all.credstore.models.Credentials;
import com.all.credstore.R;
import com.all.credstore.adapters.RecyclerViewAdapter;
import com.all.credstore.utils.Util;

import java.util.List;

public class CredentialListActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        renderList(null);
        setupOnKeyEventSearch();
        hideKeyboard(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedMenuItemId = item.getItemId();

        boolean isPinUpdateRequest = (selectedMenuItemId == R.id.pin_update);
        if(isPinUpdateRequest || selectedMenuItemId == R.id.log_out) {
            Intent loginPage = new Intent(this, LoginActivity.class);
            loginPage.putExtra(CHANGE_PIN_REQUEST_ATTR_NAME, isPinUpdateRequest);
            startActivity(loginPage);
        } else if(R.id.import_data == selectedMenuItemId) {
            exportImportService.startImport(this);
        } else if(R.id.export_data == selectedMenuItemId) {
            String exportedCreds = getDataService().getExportedCredentials();
            if(exportedCreds != null) {
                exportImportService.startExport(this, exportedCreds);
            } else {
                makeToast(getString(R.string.NO_DATA_TO_EXPORT));
            }
        } else if(R.id.minmz_option == selectedMenuItemId) {
            boolean updatedValue = !item.isChecked();
            item.setChecked(updatedValue);
            Util.storeDataInSharedPreferences(MENU_ITEM_MINMZ_CHKBOX, updatedValue, this);
            makeToast(String.format("%s App Minimization > Login page", updatedValue? "Enabled":"Disabled"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void hideKeyboard(View view) {
        super.hideKeyboard(view);
    }

    public void searchCredentials(View view) {
        renderList(getTextValue(R.id.list_search_field));
        hideKeyboard(view);

        boolean importAction = (Boolean)getRequestAttribute(IMPORTED_ATTR_NAME, false);
        if(importAction) {
            makeToast(getString(R.string.IMPORT_SUCCESS));
        }
    }

    public void addNewCredential(View view) {
        Intent activity = new Intent(this, AddCredentialActivity.class);
        startActivity(activity);
    }

    private void renderList(String searchString) {
        setVisibility(R.id.no_data_found, View.GONE);
        recyclerView = findViewById(R.id.list_recycler);
        recyclerView.setVisibility(View.GONE);

        List<Credentials> credentials = getDataService().getAll(searchString);
        setText(R.id.list_size, String.valueOf(credentials.size()));
        if(credentials.isEmpty()) {
            setVisibility(R.id.no_data_found, View.VISIBLE);
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(credentials);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setupOnKeyEventSearch() {
        EditText searchField = findViewById(R.id.list_search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                renderList(s.toString());
            }
        });
    }

    public void showMenu(View v) {
        Context context = new ContextThemeWrapper(this, R.style.menuPopupStyle);
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_list_view, popup.getMenu());
        popup.getMenu().findItem(R.id.minmz_option).setChecked(Util.isMinimizationEnabled(this));
        popup.show();
    }
}
