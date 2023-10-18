package com.galaxy.loadviewdemo;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.Player;
import com.google.android.material.textview.MaterialTextView;
import com.merchant.base.dialog.BaseDialog;
import com.nexta.launcher.R;
import com.nexta.launcher.constant.AppConstants;
import com.nexta.launcher.functions.studying.subject.model.PlayerManager;
import com.nexta.launcher.functions.studying.subject.view.PlayerViewContainer;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class PlayVideoDialog extends BaseDialog {
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.playerViewContainer)
    PlayerViewContainer playerViewContainer;
    @BindView(R.id.tvTitle)
    MaterialTextView tvTitle;
    @BindView(R.id.buttonClose)
    View buttonClose;

    @BindDimen(R.dimen._250sdp)
    int videoSize;

    private PlayerManager mPlayerManager;
    private PlayVideoDialogListener listener;
    private String contentUrl;
    private String title;
    private boolean mExoPlayerFullscreen = false;
    private Size currentSize = null;
    private long watchedTime;

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_play_video;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDialog(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        tvTitle.setText(title);
        mPlayerManager = PlayerManager.getPlayerManagerInstance();
        mPlayerManager.initializePlayer(requireContext(), playerViewContainer, contentUrl);

        playerViewContainer.setListener(new PlayerViewContainer.PlayerViewContainerListener() {
            @Override
            public void onCurrentPosition(long currentPosition) {
            }

            @Override
            public void onPlayerStateChangedStateBuffering() {

            }

            @Override
            public void onPlayerStateChangedStateReady() {

            }

            @Override
            public void onPlayerStateChangedStateEnd() {

            }

            @Override
            public void onVideoSizeChanged(int with, int height) {
                // update size
                if (currentSize == null) {
                    currentSize = new Size(with, height);
                    updateMimeScreenSize();
                }
            }
        });

        playerViewContainer.getBtnPlay().setOnClickListener(view1 -> {
            if (getPlayer() != null) {
                if (getPlayer().getPlaybackState() == Player.STATE_ENDED) {
                    getPlayer().seekTo(AppConstants.ZERO);
                    getPlayer().setPlayWhenReady(true);
                    return;
                }
                getPlayer().setPlayWhenReady(!getPlayer().getPlayWhenReady());
            }
        });

        playerViewContainer.getImgFullScreen().setOnClickListener(v -> {
            if (!mExoPlayerFullscreen)
                openFullscreenDialog();
            else
                closeFullscreenDialog();
        });
    }

    private void updateMimeScreenSize() {
        // update params
        if (!mExoPlayerFullscreen && currentSize != null) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int scWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
            int height = (int) (scWidth / (currentSize.getWidth() * 1f / currentSize.getHeight()));
            playerViewContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));

            tvTitle.setVisibility(View.VISIBLE);
            buttonClose.setVisibility(View.VISIBLE);
        }
    }

    public void pauseVideo() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mPlayerManager != null) {
            watchedTime = getPlayer() != null ? getPlayer().getCurrentPosition() : 0;
            mPlayerManager.releasePlayer();
        }
        if (listener != null) {
            listener.onCallBack(watchedTime);
        }
        super.onDismiss(dialog);
    }

    @OnClick(R.id.buttonClose)
    public void onCloseClicked() {
        dismissAllowingStateLoss();
    }

    @OnClick(R.id.root_view)
    public void onTouchOutsizeClicked() {
        dismissAllowingStateLoss();
    }

    private void openFullscreenDialog() {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        playerViewContainer.getImgFullScreen().setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        playerViewContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }


    private void closeFullscreenDialog() {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mExoPlayerFullscreen = false;
        updateMimeScreenSize();
        playerViewContainer.getImgFullScreen().setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_fullscreen_expand));
    }

    private Player getPlayer() {
        return mPlayerManager != null ? mPlayerManager.getPlayer() : null;
    }

    public PlayVideoDialog setData(String contentUrl, String title) {
        this.contentUrl = contentUrl;
        this.title = title;
        return this;
    }

    public PlayVideoDialog setCallBack(PlayVideoDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public interface PlayVideoDialogListener {
        void onCallBack(long time);
    }
}
