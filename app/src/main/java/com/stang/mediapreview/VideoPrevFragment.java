package com.stang.mediapreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import java.util.ArrayList;


public class VideoPrevFragment extends Fragment {
    public static final String TAG = MainActivity.TAG;
    public static final int UPDATER_DELAY = 1000;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private TextureView mVideoView;
    private GridLayoutManager mGridLayoutManager;
    private VideoPrevFragment.VideoRecyclerViewAdapter mVideoAdapter;
    private MediaPlayer mMediaPlayer;
    private Surface mVideoSurface;
    private RangeSeekBar<Integer> mRangeSeekBar;
    private int mStartPosition = 0;

    private int mSelectedPosition = 0;
    private boolean isPlaying = false;
    private boolean isPlayerPrepared = false;

    private Handler mHandler = new Handler();

    public VideoPrevFragment() {
        // Required empty public constructor
    }

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
                mVideoSurface = new Surface(surfaceTexture);
                try {
                    if(mMediaPlayer == null) {
                        mMediaPlayer= new MediaPlayer();
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
                if(mStartPosition != mRangeSeekBar.getSelectedMinValue()) {
                    mStartPosition = mRangeSeekBar.getSelectedMinValue();
                    seekToStart();
                }
            }
        });

        mGridLayoutManager = new GridLayoutManager(getContext(), 5);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.videoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mVideoAdapter = new VideoRecyclerViewAdapter(getContext(), null);
        mRecyclerView.setAdapter(mVideoAdapter);

        setMediaList(MainActivity.getVideoList());


        return view;
    }

    private void update() {
        int endPosition = (int)((float)mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMaxValue() / 100));
        if(mMediaPlayer.getCurrentPosition() < endPosition) {
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
        if(mMediaPlayer != null && isPlayerPrepared) {
            if(statePlaying == mMediaPlayer.isPlaying()) return;
            isPlaying = statePlaying;
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                int endPosition = (int)((float)mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMaxValue() / 100));
                if(mMediaPlayer.getCurrentPosition() >= endPosition) {
                    int startPosition = (int)((float)mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMinValue() / 100));
                    mMediaPlayer.seekTo(startPosition);
                }
                mMediaPlayer.start();
                mHandler.removeCallbacks(mUpdater);
                mHandler.post(mUpdater);
            }
        }
        Log.d(TAG, "setPlaying " + statePlaying);
        //
    }

    private void seekToStart() {
        if(isPlayerPrepared) {
            int seekPosition = (int)((float)mMediaPlayer.getDuration() * ((float)mStartPosition / 100));
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
            holder.mListImageView.setSelected(position == mSelectedPosition);
            String imgUrl =  mVideoAdapter.mVideoList.get(position);
            Log.d(TAG, "onBindViewHolder, position=" + position + " url=" + imgUrl);
            Glide.with(getContext()).load(Uri.parse(imgUrl).toString())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.mListImageView);
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
            v.setSelected(true);
            mVideoAdapter.notifyItemChanged(mSelectedPosition);
            mSelectedPosition = getAdapterPosition();
            play();
    }

     }

    public void play(){
        boolean startPlayAfterPrepare = (isPlayerPrepared && mMediaPlayer.isPlaying());
        isPlayerPrepared = false;
        if(mMediaPlayer != null) {
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(mVideoAdapter.mVideoList.get(mSelectedPosition));
                mMediaPlayer.prepare();

                //calc video surface view size
                int videoWidth = mMediaPlayer.getVideoWidth();
                int videoHeight = mMediaPlayer.getVideoHeight();
                android.view.ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
                int screenWidth = mVideoView.getWidth();
                lp.height = (int) (((float)videoHeight / (float)videoWidth) * (float)screenWidth);
                mVideoView.setLayoutParams(lp);

                int startPosition = (int)((float)mMediaPlayer.getDuration() * ((float) mRangeSeekBar.getSelectedMinValue() / 100));
                isPlayerPrepared = true;
                mMediaPlayer.seekTo(startPosition);
                if(startPlayAfterPrepare) {
                    mMediaPlayer.start();

                }
            } catch (Exception e) {
                // todo
                isPlayerPrepared = false;
            }
        }
    }



    public void setMediaList(ArrayList<String> mediaList) {
        if(mediaList != null && mediaList.size() > 0) {
            mSelectedPosition = 0;
            mVideoAdapter.setMediaList(mediaList);
            play();
        }
    }

    public void setThumbnail(ImageView imageView, int position) {
        String imgUrl = mVideoAdapter.mVideoList.get(position);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imgUrl, MediaStore.Images.Thumbnails.MINI_KIND);
        imageView.setImageBitmap(thumb);
    }

}
