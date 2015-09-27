package fi.ptm.sqlitewithloadersexample;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by pasi on 27/09/15.
 */
public class HighscoreContentProvider extends ContentProvider {
    private static final String AUTHORITY = "fi.ptm.sqlitewithloadersexample.contentprovider";
    private static final String BASE_PATH = "highscores";

    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/highscores";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/highscore";

    private HighscoreDatabaseOpenHelper database;

    // Used for the UriMacher
    private static final int HIGHSCORES   = 1;
    private static final int HIGHSCORE_ID = 2;

    // is URI ending with /# -> only one highscore, else all
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, HIGHSCORES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", HIGHSCORE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new HighscoreDatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(HighscoreDatabase.TABLE_HIGHSCORE);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case HIGHSCORES:
                break;
            case HIGHSCORE_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere("_id ="+uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case HIGHSCORES:
                id = sqlDB.insert(HighscoreDatabase.TABLE_HIGHSCORE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int count;
        switch (sURIMatcher.match(uri)) {
            case HIGHSCORES:
                count = db.delete(HighscoreDatabase.TABLE_HIGHSCORE,selection,selectionArgs);
                break;
            case HIGHSCORE_ID:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(
                        HighscoreDatabase.TABLE_HIGHSCORE,
                        HighscoreDatabase.COLUMN_ID + "="+rowId + (!TextUtils.isEmpty(selection)? " AND ("+selection+')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int count;
        switch (sURIMatcher.match(uri)) {
            case HIGHSCORES:
                count = db.update(HighscoreDatabase.TABLE_HIGHSCORE, values, selection, selectionArgs);
                break;
            case HIGHSCORE_ID:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(HighscoreDatabase.TABLE_HIGHSCORE, values,
                        HighscoreDatabase.COLUMN_ID + "="+rowId+
                                (!TextUtils.isEmpty(selection)? " AND ("+selection+')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;

    }
}
