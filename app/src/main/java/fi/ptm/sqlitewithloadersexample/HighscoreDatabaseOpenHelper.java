package fi.ptm.sqlitewithloadersexample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pasi on 27/09/15.
 */
public class HighscoreDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "highscoretable.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public HighscoreDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // database creation
    @Override
    public void onCreate(SQLiteDatabase database) {
        HighscoreDatabase.onCreate(database);
    }

    // database upgrade
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        HighscoreDatabase.onUpgrade(database, oldVersion, newVersion);
    }

}
