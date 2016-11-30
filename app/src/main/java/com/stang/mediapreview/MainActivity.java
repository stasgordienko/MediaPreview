package com.stang.mediapreview;

import android.Manifest;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    public static final String TAG = "APP";
    public final static Uri PHOTO_DATA_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public final static Uri VIDEO_DATA_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static final int PHOTOLIST_LOADER_ID = 1;
    public static final int VIDEOLIST_LOADER_ID = 2;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private PagerAdapter tabAdapter;
    private ViewPager viewPager;

    public volatile static ArrayList<String> mPhotoList;
    public volatile static ArrayList<String> mVideoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabAdapter = new PagerAdapter
                (getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted();
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showSnackbarRequestPermission();
            } else {
                requestReadExtStoragePermission();
            }
        }


    }

    private void onPermissionGranted(){
        loadMediaListFromExtStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, getString(R.string.permission_granted), Toast.LENGTH_LONG).show();
                onPermissionGranted();
            }   else {
                showSnackbarRequestPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestReadExtStoragePermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }


    private void showSnackbarRequestPermission(){
        final String message = getString(R.string.storage_permission_needed);
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.grant), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestReadExtStoragePermission();
                    }
                })
                .show();
    }


    public void loadMediaListFromExtStorage(){
        Bundle bndl_photo = new Bundle();
        bndl_photo.putString(MediaListLoader.ARGS_MEDIALIST_URI, PHOTO_DATA_URI.toString());
        bndl_photo.putString(MediaListLoader.ARGS_MEDIALIST_COLUMN, MediaStore.Images.Media.DATA);
        getLoaderManager().initLoader(PHOTOLIST_LOADER_ID, bndl_photo, mPhotoListLoaderCallbacks).forceLoad();


        Bundle bndl_video = new Bundle();
        bndl_video.putString(MediaListLoader.ARGS_MEDIALIST_URI, VIDEO_DATA_URI.toString());
        bndl_video.putString(MediaListLoader.ARGS_MEDIALIST_COLUMN, MediaStore.Video.Media.DATA);
        getLoaderManager().initLoader(VIDEOLIST_LOADER_ID, bndl_video, mVideoListLoaderCallbacks).forceLoad();

    }

    private LoaderManager.LoaderCallbacks<ArrayList<String>> mPhotoListLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<String>>() {
        @Override
        public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
            Loader<ArrayList<String>> loader = null;
            if (id == PHOTOLIST_LOADER_ID) {
                loader = new MediaListLoader(getApplicationContext(), args);
                Log.d(TAG, "PhotoList loader starts...");
            }
            return loader;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<String>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
            mPhotoList = data;
            if(tabAdapter.photoTab != null) {
                tabAdapter.photoTab.setMediaList(data);
                Log.d(TAG, "PhotoList loader ENDS...");
            }
        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<String>> mVideoListLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<String>>() {
        @Override
        public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
            Loader<ArrayList<String>> loader = null;
            if (id == VIDEOLIST_LOADER_ID) {
                loader = new MediaListLoader(getApplicationContext(), args);
                Log.d(TAG, "VideoList loader starts...");
            }
            return loader;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<String>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
            mVideoList = data;
            if(tabAdapter.videoTab != null) {
                tabAdapter.videoTab.setMediaList(data);
                Log.d(TAG, "VideoList loader ENDS...");
            }
        }
    };

    public static ArrayList<String> getPhotoList(){
        return mPhotoList;
    }

    public static ArrayList<String> getVideoList(){
        return mVideoList;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
