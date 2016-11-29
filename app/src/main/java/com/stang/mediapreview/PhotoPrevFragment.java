package com.stang.mediapreview;

import android.content.Context;
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
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import uk.co.senab.photoview.PhotoViewAttacher;


public class PhotoPrevFragment extends Fragment{
    public static final String TAG = MainActivity.TAG;
    public static final int MEDIALIST_LOADER_ID = 1;
    public static final int PERMISSION_REQUEST_CODE = 1;

    //private ArrayList<String> mPhotoList;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RoundedImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private GridLayoutManager mGridLayoutManager;
    private PhotoRecyclerViewAdapter mPhotoAdapter;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_prev, container, false);

        mImageView = (RoundedImageView) view.findViewById(R.id.imageView);

        mAttacher = new PhotoViewAttacher(mImageView, true);
        //mAttacher.setMinimumScale(1);
        //mAttacher.setScale(1);
        mAttacher.update();

        mGridLayoutManager = new GridLayoutManager(getContext(), 5);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.photoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mPhotoAdapter = new PhotoRecyclerViewAdapter(getContext(), null);
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


    public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
        private ArrayList<String> mPhotoList;
        private Context context;


        public PhotoRecyclerViewAdapter(Context context, ArrayList<String> itemList) {
            this.mPhotoList = itemList;
            this.context = context;
        }

        @Override
        public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(getContext()).inflate(R.layout.photo_card_view, null);
            RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);

            return rcv;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolders holder, int position) {
            setImage(holder.mListImageView, position);
            holder.mListImageView.setSelected(position == mSelectedPosition);
        }

        @Override
        public int getItemCount() {
            if(this.mPhotoList != null) {
                return this.mPhotoList.size();
            } else {
                return 0;
            }

        }

        public void setMediaList(ArrayList<String> list) {
            mPhotoList = list;
            notifyDataSetChanged();
        }
    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RoundedImageView mListImageView;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mListImageView = (RoundedImageView) itemView.findViewById(R.id.photo);
        }

        @Override
        public void onClick(View v) {
            v.setSelected(true);
            //Log.d(TAG, "OnClick viewId=" + v.getId() + " photo viewId=" + R.id.photo);
            mPhotoAdapter.notifyItemChanged(mSelectedPosition);
            mSelectedPosition = getAdapterPosition();

            mImageView.setImageURI(Uri.parse(mPhotoAdapter.mPhotoList.get(mSelectedPosition)));
            //setImage(mImageView, getAdapterPosition());
        }
    }

    public void setMediaList(ArrayList<String> mediaList) {
        mPhotoAdapter.setMediaList(mediaList);
        if(mediaList != null && mediaList.size() > 0) {
            mImageView.setImageURI(Uri.parse(mPhotoAdapter.mPhotoList.get(mSelectedPosition)));
            //setImage(mImageView, mSelectedPosition);
        }
    }

    public ArrayList<String> getMediaList() {
        return mPhotoAdapter.mPhotoList;
    }

    public void setImage(ImageView imageView, int position) {
        String url = mPhotoAdapter.mPhotoList.get(position);
        ImageLoader.getInstance().displayImage(url, imageView);
        //imageView.setImageURI(Uri.parse(url));
       mAttacher.update();
    }
}
