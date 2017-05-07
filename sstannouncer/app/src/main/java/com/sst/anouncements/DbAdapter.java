package com.sst.anouncements;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
    /**
     * entries
     * +----+--------+-------+---------+-------------+-------------+
     * | id | author | title | content | publishDate | lastUpdated |
     * +----+--------+-------+---------+-------------+-------------+
     *
     * categories
     * +----+------------+
     * | id | categories |
     * +----+------------+
     *
     * blogger_links
     * +----+-------------+
     * | id | bloggerLink |
     * +----+-------------+
     */

    private static final String DATABASE_NAME = "";
    private static final int DATABASE_VERSION = 1;

    // Entries table
    private static final String ENTRY_TABLE = "entries";
    private static final String ENTRY_TABLE_COL_ID = "id";
    private static final String ENTRY_TABLE_COL_AUTHOR = "author";
    private static final String ENTRY_TABLE_COL_TITLE = "title";
    private static final String ENTRY_TABLE_COL_CONTENT = "content";
    private static final String ENTRY_TABLE_COL_PUBLISHDATE = "publishDate";
    private static final String ENTRY_TABLE_COL_LASTUPDATED = "lastUpdated";

    private String[] ENTRY_TABLE_COLUMNS = {
            ENTRY_TABLE_COL_ID, ENTRY_TABLE_COL_AUTHOR, ENTRY_TABLE_COL_TITLE,
            ENTRY_TABLE_COL_CONTENT, ENTRY_TABLE_COL_PUBLISHDATE, ENTRY_TABLE_COL_LASTUPDATED
    };

    private static final String ENTRY_TABLE_CREATE = "CREATE TABLE " + ENTRY_TABLE + "("
            + ENTRY_TABLE_COL_ID + " TEXT NOT NULL PRIMARY KEY, "
            + ENTRY_TABLE_COL_AUTHOR + " TEXT NOT NULL, "
            + ENTRY_TABLE_COL_TITLE + " TEXT NOT NULL, "
            + ENTRY_TABLE_COL_CONTENT + " TEXT NOT NULL, "
            + ENTRY_TABLE_COL_PUBLISHDATE + " INTEGER NOT NULL, "
            + ENTRY_TABLE_COL_LASTUPDATED + " INTEGER NOT NULL, "
            + "CONSTRAINT PUBLISHDATE_IS_DATE CHECK(date(" + ENTRY_TABLE_COL_PUBLISHDATE + ") IS NOT NULL),"
            + "CONSTRAINT LASTUPDATED_IS_DATE CHECK(date(" + ENTRY_TABLE_COL_LASTUPDATED + ") IS NOT NULL)"
            + ");";

    // Categories table
    private static final String CATEGORIES_TABLE = "categories";
    private static final String CATEGORIES_TABLE_COL_ENTRY_ID = "entry_id";
    private static final String CATEGORIES_TABLE_COL_CATEGORY = "category";

    private String[] CATEGORIES_TABLE_COLUMNS = {
            CATEGORIES_TABLE_COL_ENTRY_ID, CATEGORIES_TABLE_COL_CATEGORY
    };

    private static final String CATEGORIES_TABLE_CREATE = "CREATE TABLE " + CATEGORIES_TABLE + "("
            + CATEGORIES_TABLE_COL_ENTRY_ID + " TEXT NOT NULL,"
            + CATEGORIES_TABLE_COL_CATEGORY + " TEXT NOT NULL,"
            + "FOREIGN KEY(" + CATEGORIES_TABLE_COL_ENTRY_ID + ") REFERENCES " + ENTRY_TABLE + "("
            + ENTRY_TABLE_COL_ID + ")"
            + ");";

    // blogger_links table
    private static final String BLOGGER_LINK_TABLE = "blogger_links";
    private static final String BLOGGER_LINK_TABLE_COL_ENTRY_ID = "entry_id";
    private static final String BLOGGER_LINK_TABLE_COL_BLOGGERLINK = "bloggerLink";

    private String[] BLOGGER_LINK_TABLE_COLUMNS = {
            BLOGGER_LINK_TABLE_COL_ENTRY_ID, BLOGGER_LINK_TABLE_COL_BLOGGERLINK
    };

    private static final String BLOGGER_LINK_TABLE_CREATE = "CREATE TABLE " + BLOGGER_LINK_TABLE + "("
            + BLOGGER_LINK_TABLE_COL_ENTRY_ID + " TEXT NOT NULL, "
            + BLOGGER_LINK_TABLE_COL_BLOGGERLINK + " TEXT NOT NULL,"
            + "FOREIGN KEY(" + BLOGGER_LINK_TABLE_COL_ENTRY_ID + ") REFERENCES " + BLOGGER_LINK_TABLE + "("
            + ENTRY_TABLE_COL_ID + ")"
            + ");";

    private SQLiteDatabase SQLdb;
    private DbHelper dbHelper;
    private Context context;

    public DbAdapter(Context ctx) {
        this.context = ctx;
    }

    public DbAdapter open() throws android.database.SQLException {
        dbHelper = new DbHelper(context);
        SQLdb = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private static class DbHelper extends SQLiteOpenHelper {
        private DbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ENTRY_TABLE_CREATE);
            db.execSQL(CATEGORIES_TABLE_CREATE);
            db.execSQL(BLOGGER_LINK_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DbHelper.class.getName(),
                    "Updating database version from version " + oldVersion +
                            " to version " + newVersion + ". This will destroy all data.");
            db.execSQL("DROP TABLE IF EXISTS " + ENTRY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + BLOGGER_LINK_TABLE);
            onCreate(db);
        }
    }
}
