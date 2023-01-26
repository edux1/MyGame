package com.example.mygame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLManager {
    SQLiteDatabase db;

    SQLManager(Context context) {
        db = context.openOrCreateDatabase("game.sql", context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS mygame " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player TEXT NOT NULL," +
                " level INT NOT NULL," +
                " score INT NOT NULL)");
    }

    void saveGame(String player, int level, int score) {
        db.execSQL("INSERT INTO mygame VALUES (null," +
                "'" + player + "'," +
                String.valueOf(level) + "," +
                String.valueOf(score) + ")");
        Log.e("---> ", "GAME SAVED!");
    }

    String getPlayer(int ranking) {
        Cursor cursor = db.rawQuery("SELECT player FROM mygame ORDER BY score DESC LIMIT 1 OFFSET "+ ranking, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    String getScore(int ranking) {
        Cursor cursor = db.rawQuery("SELECT score FROM mygame ORDER BY score DESC LIMIT 1 OFFSET "+ ranking, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    String getLevel(int ranking) {
        Cursor cursor = db.rawQuery("SELECT level FROM mygame ORDER BY score DESC LIMIT 1 OFFSET "+ ranking, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    void delete_last_row() {
        Cursor cursor = db.rawQuery("DELETE FROM mygame WHERE id NOT IN (SELECT id FROM mygame ORDER BY score DESC LIMIT 10)", null);
        cursor.moveToFirst();
    }

    void delete_table() {
        db.execSQL("DELETE FROM mygame");
    }

    int table_size() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM mygame", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
