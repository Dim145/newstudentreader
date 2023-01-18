package com.example.newstudentclassifier;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
    // database version
    private static final int DATABASE_VERSION = 6;
    // database name
    private static final String DATABASE_NAME = "mydatabase.db";
    // table name
    private static final String TABLE_NAME = "users";
    // column names
    private static final String COLUMN_ID = "id_student";
    private static final String COLUMN_NAME = "name";

    // create table sql query
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ID + " TEXT," + COLUMN_NAME + " TEXT" + ")";

    // drop table sql query
    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_USER_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * Method to insert data into the table
     *
     * @param name
     */
    public void addUser(String name, String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ID, id);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean checkUser(String id_student)
    {
        String[] columns = {
                COLUMN_ID
        };

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id_student};

        Cursor cursor = db.query(TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    public String getUserNameFromId(String id_student)
    {
        String[] columns = {
                COLUMN_ID,
                COLUMN_NAME
        };

        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id_student};

        Cursor cursor = db.query(TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        String name = "";

        if (cursor.moveToFirst())
        {
            do
            {
                int index = cursor.getColumnIndex(COLUMN_NAME);

                if(index >= 0)
                {
                    name = cursor.getString(index);
                }
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return name;
    }
}