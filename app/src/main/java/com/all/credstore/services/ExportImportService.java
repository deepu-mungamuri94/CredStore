package com.all.credstore.services;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.all.credstore.R;
import com.all.credstore.activities.CredentialListActivity;
import com.all.credstore.utils.Constants;
import com.all.credstore.utils.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ExportImportService {

    private static final String BACKUP_FILE_NAME = "credStore.txt";
    public static final int PERMISSION_REQUEST_STORAGE = 1000;
    public static final int READ_REQUEST_CODE = 42;

    public void startImport(AppCompatActivity appCompatActivity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && appCompatActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            appCompatActivity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        appCompatActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void startExport(AppCompatActivity appCompatActivity, String csvData) {
        try {
            FileOutputStream out = appCompatActivity.openFileOutput(BACKUP_FILE_NAME, Context.MODE_PRIVATE);
            out.write(csvData.getBytes());

            File filelocation = new File(appCompatActivity.getFilesDir(), BACKUP_FILE_NAME);
            Uri path = FileProvider.getUriForFile(appCompatActivity.getApplicationContext(), "com.all.credstore.fileprovider", filelocation);

            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/txt");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, BACKUP_FILE_NAME);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            appCompatActivity.startActivity(Intent.createChooser(fileIntent, appCompatActivity.getString(R.string.EXPORT_TITLE)));
        } catch (Exception ex) {
            Toast.makeText(appCompatActivity, "Failed to export data. Error :" + ex.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults, AppCompatActivity appCompatActivity) {
        if(requestCode == ExportImportService.PERMISSION_REQUEST_STORAGE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.PERMISSION_GRANTED), Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.PERMISSION_DENIED), Toast.LENGTH_SHORT);
                appCompatActivity.finish();
            }
        }
    }

    public String onActivityResult(int requestCode, int resultCode, @Nullable Intent data, AppCompatActivity appCompatActivity) {
        if (requestCode == ExportImportService.READ_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String path = uri.getPath();
                path = path.substring(path.indexOf(":") + 1);
                parseImportData(path, appCompatActivity);
            }
        }
        return null;
    }

    private void parseImportData(String filePath, AppCompatActivity appCompatActivity) {
        String data = readFile(filePath, appCompatActivity);
        if(Util.isEmpty(data)) {
            Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.NO_DATA_TO_IMPORT), Toast.LENGTH_LONG);
            return;
        }
        Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.IMPORT_STARTED), Toast.LENGTH_SHORT);
        new DataService(appCompatActivity).saveImportedCredentials(data);

        Intent listPage = new Intent(appCompatActivity, CredentialListActivity.class);
        listPage.putExtra(Constants.IMPORTED_ATTR_NAME, true);
        appCompatActivity.startActivity(listPage);
    }

    private String readFile(String filePath, AppCompatActivity appCompatActivity) {
        File file = new File(filePath);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (Exception ex) {
            Toast.makeText(appCompatActivity, "Import failed. Invalid file: " + ex.getMessage(), Toast.LENGTH_LONG);
        }
        return text.toString();
    }
}
