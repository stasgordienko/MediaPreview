package com.stang.mediapreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.stang.mediapreview.PhotoPrevFragment.MEDIALIST_LOADER_ID;


public class VideoPrevFragment extends Fragment {
    public static final String TAG = MainActivity.TAG;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private TextureView mVideoView;
    private GridLayoutManager mGridLayoutManager;
    private VideoPrevFragment.VideoRecyclerViewAdapter mVideoAdapter;
    private MediaPlayer mMediaPlayer;

    private int mSelectedPosition = 0;
    private boolean isPlaying = false;
    private boolean isPrepared = false;

    public VideoPrevFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_video_prev, container, false);
        mVideoView = (TextureView) view.findViewById(R.id.videoView);
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaying(!isPlaying);
            }
        });
        mVideoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                Surface videoSurface = new Surface(surfaceTexture);
                try {
                    if(mMediaPlayer == null) {
                        mMediaPlayer= new MediaPlayer();
                    }
                    //mMediaPlayer.setDataSource("http://");
                    mMediaPlayer.setSurface(videoSurface);
                    //mMediaPlayer.prepare();
                    //mMediaPlayer.setOnBufferingUpdateListener(this);
                    //mMediaPlayer.setOnCompletionListener(this);
                    //mMediaPlayer.setOnPreparedListener(this);
                    //mMediaPlayer.setOnVideoSizeChangedListener(this);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    //mMediaPlayer.start();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });


        RangeSeekBar<Integer> rangeSeekBar = (RangeSeekBar<Integer>) view.findViewById(R.id.seekBar);
        // Set the range
        rangeSeekBar.setRangeValues(15, 90);
        rangeSeekBar.setSelectedMinValue(20);
        rangeSeekBar.setSelectedMaxValue(88);

        mGridLayoutManager = new GridLayoutManager(getContext(), 5);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.videoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mVideoAdapter = new VideoRecyclerViewAdapter(getContext(), null);
        mRecyclerView.setAdapter(mVideoAdapter);

        setMediaList(MainActivity.getVideoList());

        return view;
    }

    private void setPlaying(boolean statePlaying) {
        isPlaying = statePlaying;
        if(mMediaPlayer != null && isPrepared) {
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.start();
            }
        }
        Log.d(TAG, "setPlaying " + statePlaying);
        //
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
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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


    public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoPrevFragment.RecyclerViewHolders> {
        private ArrayList<String> mVideoList;
        private Context context;


        public VideoRecyclerViewAdapter(Context context, ArrayList<String> itemList) {
            this.mVideoList = itemList;
            this.context = context;
        }

        @Override
        public VideoPrevFragment.RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(getContext()).inflate(R.layout.photo_card_view, null);
            VideoPrevFragment.RecyclerViewHolders rcv = new VideoPrevFragment.RecyclerViewHolders(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(VideoPrevFragment.RecyclerViewHolders holder, int position) {
            //setThumbnail(holder.mListImageView, position);

            holder.mListImageView//.setImageResource(R.drawable.p20161120142040);
                    .setImageBitmap(ThumbnailUtils.createVideoThumbnail(mVideoAdapter.mVideoList.get(position),
                    MediaStore.Images.Thumbnails.MINI_KIND));
            Log.d(TAG, "onBindViewHolder" + position);
            holder.mListImageView.setSelected(position == mSelectedPosition);
        }

        @Override
        public int getItemCount() {
            if(this.mVideoList != null) {
                return this.mVideoList.size();
            } else {
                return 0;
            }

        }

        public void setMediaList(ArrayList<String> mediaList) {
            mVideoList = mediaList;
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
            isPrepared = false;
            v.setSelected(true);
            mVideoAdapter.notifyItemChanged(mSelectedPosition);
            mSelectedPosition = getAdapterPosition();
            Log.d(TAG, "OnClick");
            if(mMediaPlayer != null) {
                mMediaPlayer.reset();
                try {
                    mMediaPlayer.setDataSource(mVideoAdapter.mVideoList.get(mSelectedPosition));
                    //mMediaPlayer.setSurface(videoSurface);
                    mMediaPlayer.prepare();
                    isPrepared = true;
                } catch (Exception e) {
                    // todo
                }

            }

            //////mVideoView.setImageURI(Uri.parse(mVideoAdapter.mVideoList.get(mSelectedPosition)));
        }
    }

    public void setMediaList(ArrayList<String> mediaList) {
        mVideoAdapter.setMediaList(mediaList);
        if(mediaList != null && mediaList.size() > 0) {
            /////mVideoView.setImageURI(Uri.parse(mVideoAdapter.mPhotoList.get(mSelectedPosition)));
            ////setImage(mImageView, mSelectedPosition);
        }
    }

    public void setThumbnail(ImageView imageView, int position) {
        String url = mVideoAdapter.mVideoList.get(position);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(url,
                MediaStore.Images.Thumbnails.MINI_KIND);
        imageView.setImageBitmap(thumb);
    }

}
