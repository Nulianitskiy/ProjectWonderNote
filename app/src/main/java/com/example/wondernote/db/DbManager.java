package com.example.wondernote.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wondernote.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private Context context;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbManager(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
    }

    public void openDb() {
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDb(String title, String disc, String uri) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE, title);
        cv.put(Constants.DISC, disc);
        cv.put(Constants.URI, uri);
        db.insert(Constants.TABLE_NAME, null, cv);
    }

    public void updateItem(String title, String disc, String uri, int id) {
        String selection = Constants._ID + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE, title);
        cv.put(Constants.DISC, disc);
        cv.put(Constants.URI, uri);
        db.update(Constants.TABLE_NAME, cv, selection, null);
    }

    public void delete(int id) {
        String selection = Constants._ID + "=" + id;
        db.delete(Constants.TABLE_NAME, selection, null);
    }

    public List<ListItem> getFromDb() {
        List<ListItem> tempList = new ArrayList<>();
        Cursor cursor = db.query(Constants.TABLE_NAME, null, null,
                null, null, null, null);

        while (cursor.moveToNext()) {
            ListItem item = new ListItem();
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(Constants.TITLE));
            @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(Constants.DISC));
            @SuppressLint("Range") String uri = cursor.getString((cursor.getColumnIndex(Constants.URI)));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(Constants._ID));
            item.setTitle(title);
            item.setDesc(desc);
            item.setUri(uri);
            item.setId(_id);
            tempList.add(item);
        }
        cursor.close();
        return tempList;
    }

    public void closeDb() {
        dbHelper.close();
    }
}
