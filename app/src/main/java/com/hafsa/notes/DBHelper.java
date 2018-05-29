package com.hafsa.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<FirstTable> fetchWords(String search) {
        List<FirstTable> firstTables = new ArrayList<>();

        // query
        String selectQuery = "SELECT * FROM " + FirstTable.TABLE_NAME + " WHERE" + FirstTable.COLUMN_WORD  +" LIKE %" + search + "%";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FirstTable firstTable = new FirstTable();
                firstTable.setId(cursor.getInt(cursor.getColumnIndex(FirstTable.COLUMN_ID)));
                firstTable.setWord(cursor.getString(cursor.getColumnIndex(FirstTable.COLUMN_WORD)));
                firstTable.setDefinition(cursor.getString(cursor.getColumnIndex(FirstTable.COLUMN_DEFINITION)));
                firstTable.setSynonym(cursor.getString(cursor.getColumnIndex(FirstTable.COLUMN_SYNONYM)));
                firstTable.setSentence(cursor.getString(cursor.getColumnIndex(FirstTable.COLUMN_SENTENCE)));
                firstTable.setForm(cursor.getString(cursor.getColumnIndex(FirstTable.COLUMN_FORM)));
                firstTable.setNotify(cursor.getInt(cursor.getColumnIndex(FirstTable.COLUMN_NOTIFY)));
                firstTable.setBookmark(cursor.getInt(cursor.getColumnIndex(FirstTable.COLUMN_BOOKMARK)));
                firstTable.setRecent(cursor.getInt(cursor.getColumnIndex(FirstTable.COLUMN_RECENT)));
                firstTables.add(firstTable);

            } while (cursor.moveToNext());
        }

        return firstTables;
    }

    public int updateBookmark(FirstTable firstTable) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FirstTable.COLUMN_BOOKMARK, 1);

        // updating row
        return db.update(FirstTable.TABLE_NAME, values, FirstTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(firstTable.getId())});
    }

    public int updateRecentSearch(FirstTable firstTable) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FirstTable.COLUMN_RECENT, 1);

        // updating row
        return db.update(FirstTable.TABLE_NAME, values, FirstTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(firstTable.getId())});
    }
}
