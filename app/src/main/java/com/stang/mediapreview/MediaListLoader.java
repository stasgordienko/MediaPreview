package com.stang.mediapreview;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Stanislav on 28.11.2016.
 */

public class MediaListLoader extends AsyncTaskLoader<ArrayList<String>> {
    public final static String TAG = MainActivity.TAG;
    public final static String ARGS_MEDIALIST_URI = "medialist_uri";

    private Uri mDataUri;
    private Context mContext;

    public MediaListLoader(Context context, Bundle args) {
        super(context);
        mContext = context;
        mDataUri = (args != null) ? Uri.parse((String) args.get(ARGS_MEDIALIST_URI)) : null;
    }

    @Override
    public ArrayList<String> loadInBackground() {
        ArrayList<String> mediaList = new ArrayList<>();

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(mDataUri, null, null, null, null);

        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            do {
                mediaList.add(parseCursor(cursor));
            } while (cursor.moveToNext());
        }
        return mediaList;
    }

    public static String parseCursor(Cursor cursor) {
        String filename = "file://" + cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        Log.d(TAG, "LOADER, file: " + filename);
        return filename;
    }
}
