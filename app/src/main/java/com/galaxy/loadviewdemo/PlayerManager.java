package com.galaxy.loadviewdemo;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.sigma.drm.SigmaHelper;
import com.sigma.player.HlsMediaSource;
import com.sigma.player.playlist.DefaultHlsPlaylistParserFactory;

public class PlayerManager {
    private static PlayerManager playerManagerInstance;
    private SimpleExoPlayer player;
    private long contentPosition;
    private Context mContext;
    private static RenderersFactory renderersFactory = null;
    private static final String MERCHANT_ID = "nexta";
    private static final String APP_ID = "nexta-transcode";
    private static final String USER_ID = "66391073-d6dc-4500-b6bd-2cf9b28deb96";
    private static final String SESSION_ID = "e3131c2d-2a88-4421-8f7d-26257e57b100";
    private boolean stateBeforePause;

    public PlayerManager() {
    }

    public static synchronized PlayerManager getPlayerManagerInstance() {
        if (playerManagerInstance == null) {
            synchronized (PlayerManager.class) {
                if (playerManagerInstance == null) {
                    playerManagerInstance = new PlayerManager();
                    SigmaHelper.instance().configure(MERCHANT_ID, APP_ID, USER_ID, SESSION_ID);
                }
            }

        }
        return playerManagerInstance;
    }

    public void initializePlayer(Context context, PlayerViewContainer playerViewContainer, String contentUrl) {
        this.mContext = context;
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        player = ExoPlayerFactory.newSimpleInstance(context, getRenderersFactory(), trackSelector, (DrmSessionManager<FrameworkMediaCrypto>) null);
        MediaSource mediaSource = createMediaSource(Uri.parse(contentUrl != null ? contentUrl : ""), "");
        if (mediaSource == null) return;
        player.seekTo(contentPosition);
        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(true);

        // resize
        playerViewContainer.getPlayerView().setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

//        playerViewContainer.getPlayerView().setShutterBackgroundColor(Color.TRANSPARENT);
//        playerViewContainer.getPlayerView().setKeepContentOnPlayerReset(true);
        playerViewContainer.getPlayerView().setPlayer(player);
        playerViewContainer.getPlayerView().requestFocus();
        player.addVideoListener(playerViewContainer.getVideoListener());
        player.addListener(playerViewContainer.getPlayerListener());
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }
        });
    }

    public void resume() {
        if (player != null) {
            player.setPlayWhenReady(stateBeforePause);
        }
    }

    public void pause() {
        if (player != null) {
            stateBeforePause = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
        }
    }

    public void reset() {
        if (player != null) {
            contentPosition = player.getContentPosition();
            player.release();
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            contentPosition = 0;
            player = null;
        }
    }

    public void setLastPosition(long lastPosition) {
        this.contentPosition = lastPosition;
    }

    public void setContentPosition(long contentPosition) {
        if (player != null) {
            player.seekTo(contentPosition);
        }
    }

    public long getContentPosition() {
        return player != null ? player.getContentPosition() : 0;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    private MediaSource createMediaSource(Uri uri, String extension) {
        try {
            @C.ContentType int type = Util.inferContentType(uri, extension);
            switch (type) {
                case C.TYPE_DASH:
                    return new DashMediaSource.Factory(buildDataSourceFactory())
                            .createMediaSource(uri);
                case C.TYPE_SS:
                    return new SsMediaSource.Factory(buildDataSourceFactory())
                            .createMediaSource(uri);
                case C.TYPE_HLS:
                    return new HlsMediaSource.Factory(buildDataSourceFactory())
                            .setPlaylistParserFactory(new DefaultHlsPlaylistParserFactory())
                            .createMediaSource(uri);
                case C.TYPE_OTHER:
                    return new ExtractorMediaSource.Factory(buildDataSourceFactory()).createMediaSource(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private RenderersFactory getRenderersFactory() {
        if (renderersFactory == null) {
            renderersFactory = new DefaultRenderersFactory(mContext)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
        }
        return renderersFactory;
    }

    /**
     * Returns a {@link DataSource.Factory}.
     */
    public DataSource.Factory buildDataSourceFactory() {
        return new DefaultDataSourceFactory(mContext, buildHttpDataSourceFactory());
    }

    /**
     * Returns a {@link HttpDataSource.Factory}.
     */
    public HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(mContext, "SigmaDRM"));
    }
}
