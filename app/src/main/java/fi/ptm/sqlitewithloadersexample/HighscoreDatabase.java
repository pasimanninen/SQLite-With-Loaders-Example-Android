package fi.ptm.sqlitewithloadersexample;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by pasi on 27/09/15.
 */
public class HighscoreDatabase {
    // Database table
    public static final String TABLE_HIGHSCORE = "highscores";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SCORE = "score";


    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
        + TABLE_HIGHSCORE
        + "("
        + COLUMN_ID + " integer primary key autoincrement, "
        + COLUMN_NAME + " text not null, "
        + COLUMN_SCORE + " int"
        + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHSCORE);
        onCreate(database);
    }
}
