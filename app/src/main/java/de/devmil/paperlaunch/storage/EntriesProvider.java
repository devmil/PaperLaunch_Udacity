package de.devmil.paperlaunch.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class EntriesProvider extends ContentProvider {

    private static final int MATCHES = 100;
    private static final int MATCHES_ID = 101;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = ProviderHelper.CONTENT_AUTHORITY;
        matcher.addURI(authority, ProviderHelper.PATH_ENTRIES, MATCHES);
        matcher.addURI(authority, ProviderHelper.PATH_ENTRIES + "/id/#", MATCHES_ID);

        return matcher;
    }

    private static UriMatcher mUriMatcher = buildUriMatcher();

    private EntriesSQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new EntriesSQLiteOpenHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result;
        int match = mUriMatcher.match(uri);

        switch(match) {
            case MATCHES:
                result = mOpenHelper.getReadableDatabase().query(
                        EntriesSQLiteOpenHelper.TABLE_ENTRIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MATCHES_ID:
                result = mOpenHelper.getReadableDatabase().query(
                        EntriesSQLiteOpenHelper.TABLE_ENTRIES,
                        projection,
                        EntriesSQLiteOpenHelper.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = mUriMatcher.match(uri);
        switch(match) {
            case MATCHES:
                return ProviderHelper.CONTENT_TYPE_ENTRIES;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri );
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = mUriMatcher.match(uri);

        switch (match) {
            case MATCHES:
                db.beginTransaction();

                long id = db.insert(
                        EntriesSQLiteOpenHelper.TABLE_ENTRIES,
                        null,
                        values
                );

                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                return ProviderHelper.getEntryUri(id);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int result = 0;

        switch(match) {
            case MATCHES:
                result = db.delete(
                        EntriesSQLiteOpenHelper.TABLE_ENTRIES,
                        selection,
                        selectionArgs
                );
                break;
        }
        if (selection == null || result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return result;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int result = 0;

        switch(match) {
            case MATCHES:
                result = db.update(
                        EntriesSQLiteOpenHelper.TABLE_ENTRIES,
                        values,
                        selection,
                        selectionArgs);
                break;
        }
        if(result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }
}
