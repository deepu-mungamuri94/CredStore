package com.all.credstore.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.all.credstore.models.Encoder;
import com.all.credstore.models.Expense;
import com.all.credstore.utils.Util;
import com.all.credstore.models.Credentials;

import java.util.ArrayList;
import java.util.List;

public class DataService extends SQLiteOpenHelper {

    private static final String dbName = "pm.db";
    private static final int dbVersion = 1;

    private static final String COMMA   = ",";
    private static final String ROW_SEPRT = "\n";

    private static final String USER_PASS_TABLE = "user_pass";
    private static final String PIN_LOCK_TABLE = "pin_locks";
    private static final String EXPENSE_TABLE = "expenses";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TAG = "tag";
    private static final String COLUMN_UNAME = "u_name";
    private static final String COLUMN_PWD = "u_pwd";
    private static final String COLUMN_URL = "site_url";
    private static final String SECRET_KEY = "secret_key";
    private static final String COLUMN_COMMENT = "comment";
    private static final String COLUMN_COST = "cost";
    private static final String COLUMN_TIME = "expense_time";

    private static final String ENCODED_LOCK_COLUMN = "encoded_lock";

    public DataService(Context context) {
        super(context, dbName, null, dbVersion);
    }
    public DataService(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_locks_table_query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR(1024))", PIN_LOCK_TABLE, COLUMN_ID, ENCODED_LOCK_COLUMN);
        db.execSQL(create_locks_table_query);

        String create_table_query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s VARCHAR(256), %s VARCHAR(1024), %s VARCHAR(1024), %s VARCHAR(256), %s VARCHAR(1024), %s VARCHAR(1024))", USER_PASS_TABLE, COLUMN_ID, COLUMN_TAG, COLUMN_UNAME, COLUMN_PWD, COLUMN_URL, SECRET_KEY, COLUMN_COMMENT);
        db.execSQL(create_table_query);

        String create_table_expense = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INT, %s VARCHAR(1024), %s TIMESTAMP DEFAULT CURRENT_TIMESTAMP)", EXPENSE_TABLE, COLUMN_ID, COLUMN_COST, COLUMN_COMMENT, COLUMN_TIME);
        db.execSQL(create_table_expense);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_locks_table_query = String.format("DROP TABLE IF EXISTS %s", PIN_LOCK_TABLE);
        db.execSQL(drop_locks_table_query);

        String drop_table_query = String.format("DROP TABLE IF EXISTS %s", USER_PASS_TABLE);
        db.execSQL(drop_table_query);

        String drop_expense_table = String.format("DROP TABLE IF EXISTS %s", EXPENSE_TABLE);
        db.execSQL(drop_expense_table);

        db.close();
    }

    public void saveOrUpdateExpense(Expense expense) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COST, expense.getCost());
        values.put(COLUMN_COMMENT, expense.getComment());
        if(expense.getTime() > 0) {
            values.put(COLUMN_TIME, expense.getTime());
        }

        SQLiteDatabase db = getWritableDatabase();
        if(expense.getId() > 0) {
            values.put(COLUMN_ID, expense.getId());
            db.update(EXPENSE_TABLE, values, "id="+expense.getId(), null);
        } else {
            db.insert(EXPENSE_TABLE, null, values);
        }

        db.close();
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String delete_entry_query = String.format("DELETE FROM %s WHERE id=%d", EXPENSE_TABLE, id);
        db.execSQL(delete_entry_query);
        db.close();
    }

    public List<Expense> getExpenses() {
        String get_all_query = String.format("SELECT * FROM %s", USER_PASS_TABLE);
        get_all_query += " ORDER BY " + COLUMN_TIME + " DESC";
        return getExpenseList(get_all_query);
    }

    public boolean pinRegistered() {
        String pin = getDBEncodedPIN();
        return (pin != null && !pin.trim().isEmpty());
    }

    public boolean isValidPIN(String pin) {
        String dbEncodedPIN = getDBEncodedPIN();
        return Encoder.validPassword(dbEncodedPIN, pin);
    }

    private String getDBEncodedPIN() {
        String query = String.format("SELECT * FROM %s", PIN_LOCK_TABLE);
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String encoded_pin = null;
        if(cursor != null && cursor.moveToNext()) {
            encoded_pin = cursor.getString(cursor.getColumnIndex(ENCODED_LOCK_COLUMN));
        }
        db.close();
        return encoded_pin;
    }

    public void savePIN(String pin) {
        saveEncodedPIN(Encoder.encode(pin));
    }

    private void saveEncodedPIN(String encoded_pin) {
        String existingPIN = getDBEncodedPIN();
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ENCODED_LOCK_COLUMN, encoded_pin);

        if(!Util.isEmpty(existingPIN)) {
            db.update(PIN_LOCK_TABLE, values, ENCODED_LOCK_COLUMN + "='"+existingPIN+"'", null);
        } else {
            db.insert(PIN_LOCK_TABLE, null, values);
        }
        db.close();
    }

    public void addOrUpdateEntry(Credentials model) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TAG, model.getTag());
        values.put(COLUMN_PWD, Encoder.getSecuredPassword(model.getPassword()));
        values.put(COLUMN_UNAME, Encoder.getSecuredPassword(model.getUsername()));
        values.put(COLUMN_URL, model.getUrl());
        values.put(COLUMN_COMMENT, model.getComment());
        if(!Util.isEmpty(model.getSecretKey())) {
            values.put(SECRET_KEY, Encoder.getSecuredPassword(model.getSecretKey()));
        }

        SQLiteDatabase db = getWritableDatabase();
        if(model.getId() > 0) {
            values.put(COLUMN_ID, model.getId());
            db.update(USER_PASS_TABLE, values, "id="+model.getId(), null);
        } else {
            db.insert(USER_PASS_TABLE, null, values);
        }

        db.close();
    }

    public void deleteEntry(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String delete_entry_query = String.format("DELETE FROM %s WHERE id=%d", USER_PASS_TABLE, id);
        db.execSQL(delete_entry_query);
        db.close();
    }

    public Credentials getEntry(int id) {
        String get_entry_query = String.format("SELECT * FROM %s WHERE id=%d", USER_PASS_TABLE, id);
        List<Credentials> results = getResults(get_entry_query);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Credentials> getAll(String tag) {
        String get_all_query = String.format("SELECT * FROM %s", USER_PASS_TABLE);
        tag = (tag != null) ? tag.trim() : null;
        if(!Util.isEmpty(tag)) {
            get_all_query += String.format(" WHERE %s like '%s'", COLUMN_TAG, "%"+tag+"%");
        }
        get_all_query += " ORDER BY " + COLUMN_TAG + " ASC";
        return getResults(get_all_query);
    }

    private List<Credentials> getResults(String query) {
        List<Credentials> results = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor == null || !cursor.moveToNext()) {
            return results;
        }

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String tag = cursor.getString(cursor.getColumnIndex(COLUMN_TAG));
            String url = cursor.getString(cursor.getColumnIndex(COLUMN_URL));
            String uname = cursor.getString(cursor.getColumnIndex(COLUMN_UNAME));
            String pwd = cursor.getString(cursor.getColumnIndex(COLUMN_PWD));
            String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
            String secretKey = cursor.getString(cursor.getColumnIndex(SECRET_KEY));
            if(!Util.isEmpty(secretKey)) {
                secretKey = Encoder.getDecodedSecuredPassword(secretKey);
            }
            pwd = Encoder.getDecodedSecuredPassword(pwd);
            uname = Encoder.getDecodedSecuredPassword(uname);
            results.add(new Credentials(id, tag, uname, pwd, url, secretKey, comment));
            cursor.moveToNext();
        }
        db.close();

        return results;
    }

    private List<Expense> getExpenseList(String query) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor == null || !cursor.moveToNext()) {
            return expenses;
        }

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            Long cost = cursor.getLong(cursor.getColumnIndex(COLUMN_COST));
            String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
            Long time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));

            expenses.add(new Expense(id, cost, comment, time));
            cursor.moveToNext();
        }
        db.close();
        return expenses;
    }

    public List<String> getDistinctURLValues() {
        List<String> values = new ArrayList<>();
        String colName = COLUMN_URL;
        String sql = String.format("SELECT distinct(%s) FROM %s", colName, USER_PASS_TABLE);
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor == null || !cursor.moveToNext()) {
            return values;
        }

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            String url = cursor.getString(cursor.getColumnIndex(colName));
            values.add(url);
            cursor.moveToNext();
        }
        db.close();

        return values;
    }

    public String getExportedCredentials() {
        List<Credentials> credentials = getAll(null);
        if(credentials == null || credentials.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(encode(COLUMN_TAG)).append(COMMA)       //Title Bar is the first row
                .append(encode(COLUMN_URL)).append(COMMA)
                .append(encode(COLUMN_UNAME)).append(COMMA)
                .append(encode(COLUMN_PWD)).append(COMMA)
                .append(encode(COLUMN_COMMENT)).append(COMMA)
                .append(encode(SECRET_KEY)).append(ROW_SEPRT);
        for(Credentials cred : credentials) {
            builder.append(encode(cred.getTag())).append(COMMA);
            builder.append(encode(cred.getUrl())).append(COMMA);
            builder.append(encode(cred.getUsername())).append(COMMA);
            builder.append(encode(cred.getPassword())).append(COMMA);
            builder.append(encode(cred.getComment())).append(COMMA);
            builder.append(encode(cred.getSecretKey())).append(ROW_SEPRT);
        }

        return encode(builder.toString());
    }

    public void saveImportedCredentials(String data) {
        List<Credentials> credentials = new ArrayList<>();
        data = decode(data);
        String[] rows = data.split(ROW_SEPRT);
        if(rows == null || rows.length <= 1) {
            return ;
        }

        //String titleBar = rows[0];
        for(int i=1; i<rows.length; i++) {
            String row = rows[i];
            String[] columns = row.split(COMMA);
            Credentials cred = new Credentials(decode(columns[0]), decode(columns[2]), decode(columns[3]),
                    decode(columns[1]), decode(columns[4]), decode(columns[5]));
            credentials.add(cred);
        }

        for(Credentials cred : credentials) {
            addOrUpdateEntry(cred);
        }
    }

    private String encode(String input) {
        input = Util.reverse(input);
        return Encoder.getSecuredPassword(input);
    }
    private String decode(String input) {
        input = Encoder.getDecodedSecuredPassword(input);
        return Util.reverse(input);
    }
}
