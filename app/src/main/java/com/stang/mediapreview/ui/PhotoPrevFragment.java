package com.stang.mediapreview.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.stang.mediapreview.R;
import com.stang.mediapreview.adapters.GalleryRecyclerViewAdapter;
import com.stang.mediapreview.ui.MainActivity;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;


public class PhotoPrevFragment extends Fragment {
    public static final String TAG = MainActivity.TAG;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RoundedImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private GridLayoutManager mGridLayoutManager;
    private GalleryRecyclerViewAdapter mPhotoAdapter;

    private int mSelectedPosition = 0;


    public PhotoPrevFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_prev, container, false);

        mImageView = (RoundedImageView) view.findViewById(R.id.imageView);
        mAttacher = new PhotoViewAttacher(mImageView, true);
        //mAttacher.update();

        mPhotoAdapter = new GalleryRecyclerViewAdapter(getContext(), null);
        mPhotoAdapter.setOnClickListener(new GalleryRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int oldPosition, int newPosition) {
                mSelectedPosition = newPosition;
                mImageView.setImageURI(Uri.parse(mPhotoAdapter.getItem(mSelectedPosition)));
                mAttacher.update();
            }
        });

        mGridLayoutManager = new GridLayoutManager(getContext(), 5);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.photoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mPhotoAdapter);

        setMediaList(MainActivity.getPhotoList());

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()  + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void setMediaList(ArrayList<String> mediaList) {
        mPhotoAdapter.setMediaList(mediaList);
        if (mediaList != null && mediaList.size() > 0) {
            String imgUrl = mPhotoAdapter.getItem(mSelectedPosition);
            mImageView.setImageURI(Uri.parse(imgUrl));
            mAttacher.update();
        }
    }

}
