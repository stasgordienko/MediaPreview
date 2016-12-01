package com.stang.mediapreview.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stang.mediapreview.R;
import com.stang.mediapreview.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by Stanislav on 01.12.2016.
 */

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.RecyclerViewHolders> {
    public static final String TAG = MainActivity.TAG;
    private ArrayList<String> mMediaList;
    private Context context;
    private OnClickListener mOnClickListener;
    private int mSelectedPosition = 0;

    public interface OnClickListener {
        void onClick(View view, int oldPosition, int newPosition);
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public GalleryRecyclerViewAdapter(Context context, ArrayList<String> itemList) {
        this.mMediaList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.photo_card_view, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.mPositionInMedialist = position;

        String imgUrl = getItem(position);

        ImageLoader.getInstance().displayImage(imgUrl, holder.mListThumbnailImageView);

//        Glide.with(context).load(Uri.parse(imgUrl).toString())
//                .thumbnail(0.5f)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(holder.mListThumbnailImageView);

        holder.mListThumbnailImageView.setSelected(mSelectedPosition == position);

        Log.d(TAG, "onBindViewHolder, position=" + position + " url=" + imgUrl);
    }

    @Override
    public int getItemCount() {
        if (this.mMediaList != null) {
            return this.mMediaList.size();
        } else {
            return 0;
        }

    }

    public String getItem(int index) {
        if (mMediaList != null) {
            return mMediaList.get(index);
        } else
            return null;
    }

    public void setMediaList(ArrayList<String> mediaList) {
        mMediaList = mediaList;
        notifyDataSetChanged();
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        public RoundedImageView mListThumbnailImageView;
        public int mPositionInMedialist;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(v, mSelectedPosition, mPositionInMedialist);
                    }
                    mSelectedPosition = mPositionInMedialist;
                }
            });
            mListThumbnailImageView = (RoundedImageView) itemView.findViewById(R.id.photo);
        }

     }
}

