package com.zhang.zhixuan.savecontact;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.zhang.zhixuan.savecontact.Fields.ContactColumns;


public class ContactContentProvider extends ContentProvider {

    private static final String DATABASE_NAME = "contact_database";
    private static final String DATABASE_VERSION = "1";
    private static final String CONTACT_TABLE_NAME = "contact_list";
    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;
    private static final UriMatcher sUriMatcher;
    static{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Fields.AUTHORITY, "contacts", CONTACTS);
        sUriMatcher.addURI(Fields.AUTHORITY, "contacts/#", CONTACT_ID);
    }

    private DatabaseHelper mOpenHelper;
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, Integer.parseInt(DATABASE_VERSION));
            Log.i("my_data", "DATABASE_VERSION=" + DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("my_data", "onCreate(SQLiteDatabase db)");
            String sql ="CREATE TABLE " + CONTACT_TABLE_NAME + " ("
                    + ContactColumns._ID + " INTEGER PRIMARY KEY,"
                    + ContactColumns.NAME + " varchar(255)," + ContactColumns.NUMBER
                    + " TEXT," + ContactColumns.TEXT + " TEXT" + ");";

            Log.i("my_data", "sql=" + sql);
            db.execSQL(sql);

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("my_data",
                    " onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)="
                            + newVersion);
            db.execSQL("DROP TABLE IF EXISTS contact");
            onCreate(db);

        }


    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {

            case CONTACTS:
                qb.setTables(CONTACT_TABLE_NAME);
                break;

            case CONTACT_ID:
                qb.setTables(CONTACT_TABLE_NAME);
                qb.appendWhere(ContactColumns._ID + "="
                        + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Fields.ContactColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, orderBy);
        return c;
    }
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CONTACTS:
                return ContactColumns.CONTENT_TYPE;

            case CONTACT_ID:
                return ContactColumns.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        if (sUriMatcher.match(uri) != CONTACTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        if (values.containsKey(Fields.ContactColumns.TEXT) == false) {
            values.put(ContactColumns.TEXT, "");
        }

        if (values.containsKey(ContactColumns.NAME) == false) {
            Resources r = Resources.getSystem();
            values.put(Fields.ContactColumns.NAME, r
                    .getString(android.R.string.untitled));
        }

        if (values.containsKey(Fields.ContactColumns.NUMBER) == false) {
            values.put(ContactColumns.NUMBER, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = db.insert(CONTACT_TABLE_NAME, ContactColumns.NUMBER, values);
        if (rowId > 0) {
            Uri diaryUri = ContentUris.withAppendedId(
                    Fields.ContactColumns.CONTENT_URI, rowId);

            return diaryUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        String rowId = uri.getPathSegments().get(1);

        return db.delete(CONTACT_TABLE_NAME, ContactColumns._ID + "=" + rowId, null);
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        String rowId = uri.getPathSegments().get(1);

        return db.update(CONTACT_TABLE_NAME, values, ContactColumns._ID + "="
                + rowId, null);
    }

}
