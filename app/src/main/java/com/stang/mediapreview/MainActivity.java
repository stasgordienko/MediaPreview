package com.stang.mediapreview;

import android.Manifest;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    public static final String TAG = "MEDIAPREVIEW";
    public final static Uri PHOTO_DATA_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public final static Uri VIDEO_DATA_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static final int PHOTOLIST_LOADER_ID = 1;
    public static final int VIDEOLIST_LOADER_ID = 2;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private PagerAdapter tabAdapter;
    private ViewPager viewPager;

    public static ArrayList<String> mPhotoList;
    public static ArrayList<String> mVideoList;

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

    public void loadMediaListFromExtStorage(){
        Bundle bndl = new Bundle();
        bndl.putString(MediaListLoader.ARGS_MEDIALIST_URI, PHOTO_DATA_URI.toString());

        getLoaderManager().initLoader(PHOTOLIST_LOADER_ID, bndl, mPhotoListLoaderCalbacks)
                .forceLoad();
        getLoaderManager().initLoader(VIDEOLIST_LOADER_ID, bndl, mVideoListLoaderCalbacks)
                .forceLoad();

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


    private LoaderManager.LoaderCallbacks<ArrayList<String>> mPhotoListLoaderCalbacks = new LoaderManager.LoaderCallbacks<ArrayList<String>>() {
        @Override
        public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
            Loader<ArrayList<String>> loader = null;
            if (id == PHOTOLIST_LOADER_ID) {
                loader = new MediaListLoader(getApplicationContext(), args);
            }
            return loader;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<String>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
            //tabAdapter.photoTab.setMediaList(data);
            mPhotoList = data;
        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<String>> mVideoListLoaderCalbacks = new LoaderManager.LoaderCallbacks<ArrayList<String>>() {
        @Override
        public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
            Loader<ArrayList<String>> loader = null;
            if (id == VIDEOLIST_LOADER_ID) {
                loader = new MediaListLoader(getApplicationContext(), args);
            }
            return loader;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<String>> loader) {
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
            //tabAdapter.videoTab.setMediaList(data);
            mVideoList = data;
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
