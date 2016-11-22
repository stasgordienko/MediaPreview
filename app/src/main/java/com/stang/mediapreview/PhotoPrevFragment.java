package com.stang.mediapreview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;


public class PhotoPrevFragment extends Fragment {
    public static final String TAG = MainActivity.TAG;

    private ArrayList<Uri> mPhotoList;
    public View mPreviousSelectedView;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RoundedImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private GridLayoutManager mGridLayoutManager;
    private PhotoRecyclerViewAdapter mPhotoAdapter;

    public PhotoPrevFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoList = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            mPhotoList.add(null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_prev, container, false);

        mImageView = (RoundedImageView) view.findViewById(R.id.imageView);

        // Set the Drawable displayed
        Drawable bitmap = getResources().getDrawable(android.R.drawable.btn_radio);
        mImageView.setImageDrawable(bitmap);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        // (not needed unless you are going to change the drawable later)
        mAttacher = new PhotoViewAttacher(mImageView);
        //mAttacher.update();

        mGridLayoutManager = new GridLayoutManager(getContext(), 5);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mPhotoAdapter = new PhotoRecyclerViewAdapter(getContext(), mPhotoList);
        mRecyclerView.setAdapter(mPhotoAdapter);

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


    public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
        private ArrayList<Uri> mPhotoList;
        private Context context;


        public PhotoRecyclerViewAdapter(Context context, ArrayList<Uri> itemList) {
            this.mPhotoList = itemList;
            this.context = context;
        }

        @Override
        public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_card_view, null);
            RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);

            return rcv;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolders holder, int position) {
             //holder.mTextView.setText("image" + position);
            //url = mPhotoList.get(position);
            //imageLoader(holder.mImageView, url);
        }

        @Override
        public int getItemCount() {
            return this.mPhotoList.size();
        }
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RoundedImageView mImageView;
        public TextView mTextView;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageView = (RoundedImageView) itemView.findViewById(R.id.photo);
            //mTextView = (TextView) itemView.findViewById(R.id.label);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Image clicked. Adapter position = " + getAdapterPosition());

            if(mPreviousSelectedView != null){
                mPreviousSelectedView.setSelected(false);
            }
            v.setSelected(true);
            mPreviousSelectedView = v;

        }
    }
}
