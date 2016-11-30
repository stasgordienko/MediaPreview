package com.stang.mediapreview;

/**
 * Created by Stanislav on 28.11.2016.
 */

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Stanislav on 14.11.2016.
 */

public class MediaPreviewApplication extends Application {

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnFail(android.support.design.R.drawable.abc_ic_star_black_48dp)
            .showImageForEmptyUri(android.support.design.R.drawable.abc_ic_star_black_48dp)
            //.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            //.resetViewBeforeLoading()
            .cacheInMemory()
            //.cacheOnDisc()
            //.decodingType(ImageScaleType.EXACTLY)
            .build();


    @Override
    public void onCreate() {
        super.onCreate();
        // init all global libraries
        initImageLoader();
    }


    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(128, 128) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                //.taskExecutor(...)
                //.taskExecutorForCachedImages(...)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(10) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
                //.imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                //.writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

}