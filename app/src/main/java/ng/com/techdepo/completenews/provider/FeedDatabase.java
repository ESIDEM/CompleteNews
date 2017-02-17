package ng.com.techdepo.completenews.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ESIDEM jnr on 2/15/2017.
 */

public class FeedDatabase extends SQLiteOpenHelper {

    /** Schema version. */
    public static final int DATABASE_VERSION = 3;
    /** Filename for SQLite file. */
    public static final String DATABASE_NAME = "feed.db";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";
    /** SQL statement to create "entry" table. */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedContract.Entry.TABLE_NAME + " (" +
                    FeedContract.Entry._ID + " INTEGER PRIMARY KEY," +
                    FeedContract.Entry.COLUMN_NAME_TITLE    + TYPE_TEXT + COMMA_SEP +
                    FeedContract.Entry.COLUMN_NAME_LINK + TYPE_TEXT + COMMA_SEP +
                    FeedContract.Entry.COLUMN_NAME_GUID + TYPE_TEXT + COMMA_SEP +
                    FeedContract.Entry.COLUMN_NAME_PUBLISHED + TYPE_INTEGER +COMMA_SEP +
                    FeedContract.Entry.COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    FeedContract.Entry.COLUMN_NAME_IMAGE_URL + TYPE_TEXT + ");";


    /** SQL statement to drop "entry" table. */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedContract.Entry.TABLE_NAME;

    public FeedDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
