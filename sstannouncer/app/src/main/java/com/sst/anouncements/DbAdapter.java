package com.sst.anouncements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sst.anouncements.Feed.Entry;

import java.util.ArrayList;
import java.util.Date;

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
    private static final String ENTRIES_TABLE = "entries";
    private static final String ENTRIES_TABLE_COL_ID = "id";
    private static final String ENTRIES_TABLE_COL_AUTHOR = "author";
    private static final String ENTRIES_TABLE_COL_TITLE = "title";
    private static final String ENTRIES_TABLE_COL_CONTENT = "content";
    private static final String ENTRIES_TABLE_COL_PUBLISHDATE = "publishDate";
    private static final String ENTRIES_TABLE_COL_LASTUPDATED = "lastUpdated";

    private String[] ENTRIES_TABLE_COLUMNS = {
            ENTRIES_TABLE_COL_ID, ENTRIES_TABLE_COL_AUTHOR, ENTRIES_TABLE_COL_TITLE,
            ENTRIES_TABLE_COL_CONTENT, ENTRIES_TABLE_COL_PUBLISHDATE, ENTRIES_TABLE_COL_LASTUPDATED
    };

    private static final String ENTRIES_TABLE_CREATE = "CREATE TABLE " + ENTRIES_TABLE + "("
            + ENTRIES_TABLE_COL_ID + " TEXT NOT NULL PRIMARY KEY, "
            + ENTRIES_TABLE_COL_AUTHOR + " TEXT NOT NULL, "
            + ENTRIES_TABLE_COL_TITLE + " TEXT NOT NULL, "
            + ENTRIES_TABLE_COL_CONTENT + " TEXT NOT NULL, "
            + ENTRIES_TABLE_COL_PUBLISHDATE + " INTEGER NOT NULL, "
            + ENTRIES_TABLE_COL_LASTUPDATED + " INTEGER NOT NULL, "
            + "CONSTRAINT PUBLISHDATE_IS_DATE CHECK(date(" + ENTRIES_TABLE_COL_PUBLISHDATE + ") IS NOT NULL),"
            + "CONSTRAINT LASTUPDATED_IS_DATE CHECK(date(" + ENTRIES_TABLE_COL_LASTUPDATED + ") IS NOT NULL)"
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
            + "FOREIGN KEY(" + CATEGORIES_TABLE_COL_ENTRY_ID + ") REFERENCES " + ENTRIES_TABLE + "("
            + ENTRIES_TABLE_COL_ID + ")"
            + ");";

    // blogger_links table
    private static final String BLOGGER_LINKS_TABLE = "blogger_links";
    private static final String BLOGGER_LINKS_TABLE_COL_ENTRY_ID = "entry_id";
    private static final String BLOGGER_LINKS_TABLE_COL_BLOGGERLINK = "bloggerLink";

    private String[] BLOGGER_LINKS_TABLE_COLUMNS = {
            BLOGGER_LINKS_TABLE_COL_ENTRY_ID, BLOGGER_LINKS_TABLE_COL_BLOGGERLINK
    };

    private static final String BLOGGER_LINKS_TABLE_CREATE = "CREATE TABLE " + BLOGGER_LINKS_TABLE + "("
            + BLOGGER_LINKS_TABLE_COL_ENTRY_ID + " TEXT NOT NULL, "
            + BLOGGER_LINKS_TABLE_COL_BLOGGERLINK + " TEXT NOT NULL UNIQUE,"
            + "FOREIGN KEY(" + BLOGGER_LINKS_TABLE_COL_ENTRY_ID + ") REFERENCES " + BLOGGER_LINKS_TABLE + "("
            + ENTRIES_TABLE_COL_ID + ")"
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

    public void insertEntry(Entry entry) {
        String entryId = entry.getId();

        // entries table
        ContentValues entriesTableValues = new ContentValues();
        entriesTableValues.put(ENTRIES_TABLE_COL_ID, entryId);
        entriesTableValues.put(ENTRIES_TABLE_COL_AUTHOR, entry.getAuthorName());
        entriesTableValues.put(ENTRIES_TABLE_COL_TITLE, entry.getTitle());
        entriesTableValues.put(ENTRIES_TABLE_COL_CONTENT, entry.getContent());
        entriesTableValues.put(ENTRIES_TABLE_COL_PUBLISHDATE, entry.getPublished().getTime());
        entriesTableValues.put(ENTRIES_TABLE_COL_LASTUPDATED, entry.getLastUpdated().getTime());
        SQLdb.insert(ENTRIES_TABLE, null, entriesTableValues);

        // categories table
        for (String category : entry.getCategories()) {
            ContentValues categoriesTableValues = new ContentValues();
            categoriesTableValues.put(CATEGORIES_TABLE_COL_ENTRY_ID, entryId);
            categoriesTableValues.put(CATEGORIES_TABLE_COL_CATEGORY, category);
            SQLdb.insert(CATEGORIES_TABLE, null, categoriesTableValues);
        }

        // blogger_links table
        ContentValues blogger_linksTableValues = new ContentValues();
        blogger_linksTableValues.put(BLOGGER_LINKS_TABLE_COL_ENTRY_ID, entryId);
        blogger_linksTableValues.put(BLOGGER_LINKS_TABLE_COL_BLOGGERLINK, entry.getBloggerLink());
        SQLdb.insert(BLOGGER_LINKS_TABLE, null, blogger_linksTableValues);
    }

    private ArrayList<String> getCategories(String entryId) {
        Cursor cursor = SQLdb.query(CATEGORIES_TABLE, CATEGORIES_TABLE_COLUMNS,
                CATEGORIES_TABLE_COL_ENTRY_ID + " = " + entryId, null, null, null, null);

        ArrayList<String> categories = new ArrayList<>();
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            categories.add(cursor.getString(1));
        }

        cursor.close();

        return categories;
    }

    private String getBloggerLink(String entryId) {
        Cursor cursor = SQLdb.query(BLOGGER_LINKS_TABLE, BLOGGER_LINKS_TABLE_COLUMNS,
                BLOGGER_LINKS_TABLE_COL_ENTRY_ID + " = " +  entryId, null, null, null, null);
        cursor.moveToFirst();

        String bloggerLink = cursor.getString(1);

        cursor.close();

        return bloggerLink;
    }

    public Entry getEntry(String entryId) {
        Cursor cursor = SQLdb.query(ENTRIES_TABLE, ENTRIES_TABLE_COLUMNS,
                ENTRIES_TABLE_COL_ID + " = " + entryId, null, null, null, null);
        cursor.moveToFirst();

        String authorName = cursor.getString(1);
        String title = cursor.getString(2);
        String content = cursor.getString(3);
        Date publishDate = new Date(cursor.getLong(4));
        Date lastUpdated = new Date(cursor.getLong(5));

        cursor.close();

        ArrayList<String> categories = getCategories(entryId);
        String bloggerLink = getBloggerLink(entryId);

        return new Entry(entryId, publishDate, lastUpdated, categories, authorName, bloggerLink,
                title, content);
    }

    public ArrayList<Entry> getAllEntries() {
        Cursor cursor = SQLdb.query(ENTRIES_TABLE, ENTRIES_TABLE_COLUMNS,
                null, null, null, null, null);

        ArrayList<Entry> entries = new ArrayList<>();
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            String id = cursor.getString(0);
            String authorName = cursor.getString(1);
            String title = cursor.getString(2);
            String content = cursor.getString(3);
            Date publishDate = new Date(cursor.getLong(4));
            Date lastUpdated = new Date(cursor.getLong(5));

            ArrayList<String> categories = getCategories(id);
            String bloggerLink = getBloggerLink(id);

            entries.add(new Entry(id, publishDate, lastUpdated, categories, authorName, bloggerLink,
                    title, content));
        }

        cursor.close();

        // Entries are NOT sorted
        return entries;
    }

    public void deleteEntry(String entryId) {
        SQLdb.delete(CATEGORIES_TABLE, CATEGORIES_TABLE_COL_ENTRY_ID + " = " + entryId, null);
        SQLdb.delete(BLOGGER_LINKS_TABLE, BLOGGER_LINKS_TABLE_COL_ENTRY_ID + " = " + entryId, null);
        SQLdb.delete(ENTRIES_TABLE, ENTRIES_TABLE_COL_ID + " = " + entryId, null);
    }

    private static class DbHelper extends SQLiteOpenHelper {
        private DbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ENTRIES_TABLE_CREATE);
            db.execSQL(CATEGORIES_TABLE_CREATE);
            db.execSQL(BLOGGER_LINKS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DbHelper.class.getName(),
                    "Updating database version from version " + oldVersion +
                            " to version " + newVersion + ". This will destroy all data.");
            db.execSQL("DROP TABLE IF EXISTS " + ENTRIES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + BLOGGER_LINKS_TABLE);
            onCreate(db);
        }
    }
}
