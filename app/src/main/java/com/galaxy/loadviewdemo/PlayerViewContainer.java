package com.galaxy.loadviewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.video.VideoListener;

public class PlayerViewContainer extends FrameLayout {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.playerView)
    PlayerView playerView;
    @BindView(R.id.main_media_frame)
    FrameLayout mainMediaFrame;
    @BindView(R.id.btnPlay)
    ImageView btnPlay;
    @BindView(R.id.exo_fullscreen_icon)
    ImageView imgFullScreen;
    private PlayerManager mPlayerManager;
    private PlayerViewContainerListener listener;

    public PlayerViewContainer(@NonNull Context context) {
        super(context);
        initViews();
    }

    public PlayerViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PlayerViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public PlayerViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_player_container, this, true);
        ButterKnife.bind(this);
    }

    public PlayerView getPlayerView() {
        return playerView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public ImageView getBtnPlay() {
        return btnPlay;
    }

    public ImageView getImgFullScreen() {
        return imgFullScreen;
    }

    public void setVisibilityButtonFullScreen(boolean isVisibility) {
        imgFullScreen.setVisibility(isVisibility ? VISIBLE : GONE);
    }

    private Player.EventListener playerListener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            Log.d("ZERO", reason + " onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.d("ZERO", " onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.d("ZERO", " onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.d("ZERO", " onPlayerStateChanged");
            if (btnPlay != null) {
                btnPlay.setSelected(playWhenReady && playbackState != Player.STATE_ENDED);
            }
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    progressBar.setVisibility(View.VISIBLE);
                    if (listener != null) {
                        listener.onPlayerStateChangedStateBuffering();
                    }
                    break;
                case Player.STATE_READY:
                    progressBar.setVisibility(View.INVISIBLE);
                    if (listener != null) {
                        listener.onPlayerStateChangedStateReady();
                    }
                    break;

                case Player.STATE_ENDED:
                    progressBar.setVisibility(View.INVISIBLE);
                    if (listener != null) {
                        listener.onPlayerStateChangedStateEnd();
                    }
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Log.d("ZERO", " onRepeatModeChanged");
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            Log.d("ZERO", " onShuffleModeEnabledChanged");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.d("ZERO", "onPlayerError");
            if (progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            if (mPlayerManager != null) {
                mPlayerManager.setContentPosition(0);
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            Log.d("ZERO", reason + " onPositionDiscontinuity");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.d("ZERO", " onPlaybackParametersChanged");
        }

        @Override
        public void onSeekProcessed() {
            Log.d("ZERO", " onSeekProcessed");
        }
    };

    private VideoListener videoListener = new VideoListener() {
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            if (listener != null) listener.onVideoSizeChanged(width, height);
        }
    };

    public Player.EventListener getPlayerListener() {
        return playerListener;
    }

    public VideoListener getVideoListener() {
        return videoListener;
    }

    @OnClick({R.id.btnPlay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                handlePlayVideo();
                break;
        }
    }

    private void handlePlayVideo() {
        Player player = getPlayer();
        if (player != null) {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
                player.setPlayWhenReady(true);
                return;
            }
            player.setPlayWhenReady(!player.getPlayWhenReady());
        }
    }

    public interface PlayerViewContainerListener {
        void onCurrentPosition(long currentPosition);

        void onPlayerStateChangedStateBuffering();

        void onPlayerStateChangedStateReady();

        void onPlayerStateChangedStateEnd();

        void onVideoSizeChanged(int with, int height);
    }

    public void setPlayerManager(PlayerManager mPlayerManager) {
        this.mPlayerManager = mPlayerManager;
    }

    private Player getPlayer() {
        return mPlayerManager != null ? mPlayerManager.getPlayer() : null;
    }

    public void setListener(PlayerViewContainerListener listener) {
        this.listener = listener;
    }

}
