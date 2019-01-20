package com.poman.atm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExpenseHelper extends SQLiteOpenHelper {
    public ExpenseHelper(Context context) {
        this(context,"atm",null,1);
    }

    private ExpenseHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE expense (_id INTEGER PRIMARY KEY NOT NULL," +
                "cdate VARCHAR NOT NULL," +
                "info VARCHAR, amount INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void sqrtByDate() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.rawQuery("SELECT * FROM expense ORDER BY cdate DESC",null);
    }

    public void queryColumns() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("expense",null,null,null,null,null,null);
        String[] columnNames = cursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            Log.d("ExpenseHelper", "queryColumns: "+columnNames[i]);
        }
        db.close();
    }

    public boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("expense",null,null) >= 1;
    }
}
