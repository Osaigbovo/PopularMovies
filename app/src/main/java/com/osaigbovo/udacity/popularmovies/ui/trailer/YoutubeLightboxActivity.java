package com.osaigbovo.udacity.popularmovies.ui.trailer;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.osaigbovo.udacity.popularmovies.BuildConfig;
import com.osaigbovo.udacity.popularmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Activity shows how the YouTubePlayerView can be used to create a "Lightbox" similar to that
 * of the StandaloneYouTubePlayer. Avoided rebuffering the video by setting some configchange flags
 * on this activities declaration in the manifest.
 *
 * @author Osaigbovo Odiase.
 */
public class YoutubeLightboxActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";

    @BindView(R.id.relativelayout_youtube_activity)
    RelativeLayout relativeLayout;
    @BindView(R.id.youTubePlayerView)
    YouTubePlayerView mPlayerView;

    private YouTubePlayer mPlayer;
    private String mVideoId;
    private boolean isFullscreen;
    private int millis;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_lightbox_youtube);
        ButterKnife.bind(this);

        relativeLayout.setOnClickListener(v -> onBackPressed());

        mPlayerView.initialize(BuildConfig.YOUTUBE_KEY, this);

        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
        }

        final Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_VIDEO_ID)) {
            mVideoId = extras.getString(KEY_VIDEO_ID);
        } else {
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        mPlayer = youTubePlayer;
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        youTubePlayer.setOnFullscreenListener(b -> isFullscreen = b);

        if (mVideoId != null && !wasRestored) {
            youTubePlayer.loadVideo(mVideoId);
        }
        if (wasRestored) {
            youTubePlayer.seekToMillis(millis);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Unable to load video", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPlayer != null) {
            outState.putInt(KEY_VIDEO_TIME, mPlayer.getCurrentTimeMillis());
        }
    }

    @Override
    public void onBackPressed() {
        boolean finish = true;
        try {
            if (mPlayer != null) {
                if (isFullscreen) {
                    finish = false;
                    mPlayer.setOnFullscreenListener(b -> {
                        //Wait until we are out of fullscreen before finishing this activity
                        if (!b) {
                            finish();
                        }
                    });
                    mPlayer.setFullscreen(false);
                }
                mPlayer.pause();
            }
        } catch (final IllegalStateException e) {
            e.printStackTrace();
        }

        if (finish) {
            super.onBackPressed();
        }
    }

}
