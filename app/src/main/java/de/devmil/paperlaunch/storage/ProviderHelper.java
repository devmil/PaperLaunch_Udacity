package de.devmil.paperlaunch.storage;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

public abstract class ProviderHelper {
    private ProviderHelper() {

    }

    public static final String CONTENT_AUTHORITY = "de.devmil.paperlaunch";
    public static final String PATH_ENTRIES = "entries";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String CONTENT_ITEM_TYPE_ENTRIES =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRIES;
    public static final String CONTENT_TYPE_ENTRIES =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENTRIES;

    public static Uri ENTRIES = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();
    public static Uri ENTRIES_WITH_ID = ENTRIES.buildUpon().appendPath("id").build();

    public static Uri getEntryUri(long entryId) {
        return ContentUris.withAppendedId(ProviderHelper.ENTRIES_WITH_ID, entryId);
    }
}
