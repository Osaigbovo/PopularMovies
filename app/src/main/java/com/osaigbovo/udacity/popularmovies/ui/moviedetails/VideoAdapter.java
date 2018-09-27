package com.osaigbovo.udacity.popularmovies.ui.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.osaigbovo.udacity.popularmovies.BuildConfig;
import com.osaigbovo.udacity.popularmovies.PopularMoviesApp;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.Video;
import com.osaigbovo.udacity.popularmovies.ui.trailer.YoutubeLightboxActivity;

import java.util.ArrayList;

import timber.log.Timber;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private ArrayList<Video> vdo;
    //private Context context;

    VideoAdapter(/*Context context*/) {
        //this.context = context;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

        if (!vdo.isEmpty()) {
            Video videoResult = vdo.get(position);
            holder.bindToPost(videoResult);
            Timber.i(videoResult.getKey());
            Timber.i("Video " + position);
        }
    }

    @Override
    public int getItemCount() {
        if (vdo != null) {
            return vdo.size();
        } else {
            return 0;
        }
    }

    public void setVideo(ArrayList<Video> vdo) {
        this.vdo = vdo;
        notifyDataSetChanged();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder
            implements YouTubeThumbnailView.OnInitializedListener {


        private YouTubeThumbnailView thumb;
        //private Map<View, YouTubeThumbnailLoader> mLoaders = new HashMap<>();

        public VideoViewHolder(View view) {
            super(view);
            thumb = view.findViewById(R.id.youtube_thumbnail);
        }

        public void bindToPost(Video video) {
            Context context = thumb.getContext();
            if (thumb.getTag() == null) {
                thumb.setTag(video.getKey());
                thumb.initialize(BuildConfig.YOUTUBE_KEY, this);
            }

            thumb.setOnClickListener(v -> {
                String videoid = (String) thumb.getTag();
                Intent lightboxIntent = new Intent(context, YoutubeLightboxActivity.class);
                lightboxIntent.putExtra(YoutubeLightboxActivity.KEY_VIDEO_ID, videoid);
                PopularMoviesApp.getContext().startActivity(lightboxIntent);
            });
        }

        @Override
        public void onInitializationSuccess(YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            //mLoaders.put(view, loader);
            loader.setVideo((String) view.getTag());

        /*Set a YouTubeThumbnailLoader.OnThumbnailLoadedListener which is invoked whenever a new
        thumbnail has finished loading and has been displayed in this YouTube thumbnail view.*/
            loader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView view, String s) {
                /*Releases system resources used by this YouTubeThumbnailLoader.
                  Note that after calling this method any further interaction with this YouTubeThumbnailLoader is forbidden.
                  A new instance must be created to load thumbnails into a YouTubeThumbnailView.*/
                    loader.release();
                }

                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView,
                                             YouTubeThumbnailLoader.ErrorReason errorReason) {

                }
            });
        }

        @Override
        public void onInitializationFailure(YouTubeThumbnailView thumbnailView,
                                            YouTubeInitializationResult errorReason) {
            final String errorMessage = errorReason.toString();
            thumbnailView.setImageResource(R.drawable.no_thumbnail);
            //Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

}
