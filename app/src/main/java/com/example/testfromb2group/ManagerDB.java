package com.example.testfromb2group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

public class ManagerDB extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "b2_test.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "test_events_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DIVISION_TYPE = "divisionType";
    public static final String COLUMN_DEL = "del";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + "id" + " INTEGER PRIMARY KEY,"
            + "name" + " TEXT,"
            + COLUMN_DIVISION_TYPE + " TEXT,"
            + COLUMN_DEL + " INTEGER"
            + ");";

    public ManagerDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
        Log.d(TAG, "Database table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading the database from version " + oldVersion + " to " + newVersion);
    }

    public boolean insertData(JSONObject data) {
        SQLiteDatabase db = this.getWritableDatabase(); // Получаем доступ для записи
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_ID, data.getInt("id"));
            values.put(COLUMN_NAME, data.getString("name"));
            // Проверяем на null для divisionType и del
            values.put(COLUMN_DIVISION_TYPE, data.isNull("divisionType") ? null : data.getString("divisionType"));
            values.put(COLUMN_DEL, data.isNull("del") ? null : data.getInt("del"));
        } catch (Exception e) {
            Log.e(TAG, "Error getting data from JSON", e);
            db.close();
            return false;
        }

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        if (result == -1) {
            Log.e(TAG, "Failed to insert row");
            return false;
        } else {
            Log.d(TAG, "Successfully inserted row");
            return true;
        }
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }

    //В этом методе получим все строки для дальнейшего вывода на экран
    public List<HandbookOfConsequencesOfViolations> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<HandbookOfConsequencesOfViolations> dataList = new ArrayList<>();

        // SQL-запрос для выбора всех данных
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        //это что то вроде итератора только у андроида аналог ResultSet
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);

            // Проверяем, есть ли данные
            if (cursor.moveToFirst()) {
                do {
                    // Получаем данные из каждого столбца
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    @SuppressLint("Range") Integer divisionType = cursor.getInt(cursor.getColumnIndex(COLUMN_DIVISION_TYPE));
                    @SuppressLint("Range") int del = cursor.getInt(cursor.getColumnIndex(COLUMN_DEL));

                    // Форматируем данные для отображения
                    var data = new HandbookOfConsequencesOfViolations(id, name, divisionType,del);
                    dataList.add(data);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting data from database", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Закрываем курсор
            }
            db.close(); // Закрываем базу данных
        }
        return dataList;
    }
}

