package com.stang.mediapreview.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.stang.mediapreview.R;
import com.stang.mediapreview.adapters.GalleryRecyclerViewAdapter;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;


public class VideoPrevFragment extends Fragment {
    public static final String TAG = MainActivity.TAG;
    public static final int UPDATER_DELAY = 1000;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private TextureView mVideoView;
    private GridLayoutManager mGridLayoutManager;
    private GalleryRecyclerViewAdapter mVideoAdapter;
    private MediaPlayer mMediaPlayer;
    private Surface mVideoSurface;
    private RangeSeekBar<Integer> mRangeSeekBar;
    private int mStartPosition = 0;

    private int mSelectedPosition = 0;
    private boolean isPlaying = false;
    private boolean isPlayerPrepared = false;

    private Handler mHandler = new Handler();


    private final Runnable mUpdater = new Runnable() {
        public void run() {
            try {
                if (isPlayerPrepared && mMediaPlayer.isPlaying()) {
                    update();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public VideoPrevFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_prev, container, false);
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
                mVideoSurface = new Surface(surfaceTexture);
                try {
                    if (mMediaPlayer == null) {
                        mMediaPlayer = new MediaPlayer();
                    }
                    mMediaPlayer.setSurface(mVideoSurface);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlaying = false;
                            mHandler.removeCallbacks(mUpdater);
                        }
                    });
                    //mMediaPlayer.setOnBufferingUpdateListener(this);
                    //mMediaPlayer.setOnPreparedListener(this);
                    //mMediaPlayer.setOnVideoSizeChangedListener(this);
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


        mRangeSeekBar = (RangeSeekBar<Integer>) view.findViewById(R.id.seekBar);
        // Set the range
        mRangeSeekBar.setRangeValues(0, 100);
        mRangeSeekBar.setSelectedMinValue(0);
        mRangeSeekBar.setSelectedMaxValue(100);
        mRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                if (mStartPosition != mRangeSeekBar.getSelectedMinValue()) {
                    mStartPosition = mRangeSeekBar.getSelectedMinValue();
                    seekToStart();
                }
            }
        });


        mVideoAdapter = new GalleryRecyclerViewAdapter(getContext(), null);
        mVideoAdapter.setOnClickListener(new GalleryRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int oldPosition, int newPosition) {
                mSelectedPosition = newPosition;
                mRecyclerView.findViewHolderForAdapterPosition(oldPosition).itemView.setSelected(false);
                mRecyclerView.findViewHolderForAdapterPosition(newPosition).itemView.setSelected(true);
                play();
            }
        });

        mGridLayoutManager = new GridLayoutManager(getContext(), 5);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.videoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mVideoAdapter);

        setMediaList(MainActivity.getVideoList());

        return view;
    }

    private void update() {
        int endPosition = (int) ((float) mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMaxValue() / 100));
        if (mMediaPlayer.getCurrentPosition() < endPosition) {
            mHandler.removeCallbacks(mUpdater);
            mHandler.postDelayed(mUpdater, UPDATER_DELAY);
        } else {
            setPlaying(false);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        setPlaying(false);
        super.onHiddenChanged(hidden);
    }

    public void setPlaying(boolean statePlaying) {
        if (mMediaPlayer != null && isPlayerPrepared) {
            if (statePlaying == mMediaPlayer.isPlaying()) return;
            isPlaying = statePlaying;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                int endPosition = (int) ((float) mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMaxValue() / 100));
                if (mMediaPlayer.getCurrentPosition() >= endPosition) {
                    int startPosition = (int) ((float) mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMinValue() / 100));
                    mMediaPlayer.seekTo(startPosition);
                }
                mMediaPlayer.start();
                mHandler.removeCallbacks(mUpdater);
                mHandler.post(mUpdater);
            }
        }
        Log.d(TAG, "setPlaying " + statePlaying);
    }

    public void seekToStart() {
        if (isPlayerPrepared) {
            int seekPosition = (int) ((float) mMediaPlayer.getDuration() * ((float) mStartPosition / 100));
            mMediaPlayer.seekTo(seekPosition);
        }
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


    public boolean isPlaing() {
        return isPlaying;
    }


    public void play() {
        boolean startPlayAfterPrepare = (isPlayerPrepared && mMediaPlayer.isPlaying());
        isPlayerPrepared = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(mVideoAdapter.getItem(mSelectedPosition));
                mMediaPlayer.prepare();

                //calc video surface view size
                int videoWidth = mMediaPlayer.getVideoWidth();
                int videoHeight = mMediaPlayer.getVideoHeight();
                android.view.ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
                int screenWidth = mVideoView.getWidth();
                lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);
                mVideoView.setLayoutParams(lp);

                int startPosition = (int) ((float) mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMinValue() / 100));
                isPlayerPrepared = true;
                mMediaPlayer.seekTo(startPosition);
                if (startPlayAfterPrepare) {
                    setPlaying(true);
                }
            } catch (Exception e) {
                // todo
                isPlayerPrepared = false;
            }
        }
    }


    public void setMediaList(ArrayList<String> mediaList) {
        if (mediaList != null && mediaList.size() > 0) {
            mSelectedPosition = 0;
            mVideoAdapter.setMediaList(mediaList);
            play();
        }
    }

}
