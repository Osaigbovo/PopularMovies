package com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.osaigbovo.udacity.popularmovies.BuildConfig;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.Video;
import com.osaigbovo.udacity.popularmovies.ui.trailer.YoutubeLightboxActivity;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Adapter for MOvie Trailers.
 *
 * @author Osaigbovo Odiase.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private ArrayList<Video> vdo;
    //private Context context;

    public VideoAdapter(/*Context context*/) {
        //this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creating a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

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
            /*implements YouTubeThumbnailView.OnInitializedListener*/ {


        private final YouTubeThumbnailView thumb;
        //private Map<View, YouTubeThumbnailLoader> mLoaders = new HashMap<>();

        VideoViewHolder(View view) {
            super(view);
            thumb = view.findViewById(R.id.youtube_thumbnail);
        }

        void bindToPost(Video video) {
            Context context = thumb.getContext();
            if (thumb.getTag() == null) {
                thumb.setTag(video.getKey());
                thumb.initialize(BuildConfig.YOUTUBE_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
                                                        YouTubeThumbnailLoader youTubeThumbnailLoader) {

                        youTubeThumbnailLoader.setVideo((String) youTubeThumbnailView.getTag());

                         /*Set a YouTubeThumbnailLoader.OnThumbnailLoadedListener which is invoked whenever a new
                            thumbnail has finished loading and has been displayed in this YouTube thumbnail view.*/
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(
                                new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                                    @Override
                                    public void onThumbnailLoaded(YouTubeThumbnailView view, String s) {
                                        youTubeThumbnailLoader.release();
                                    }

                                    @Override
                                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView,
                                                                 YouTubeThumbnailLoader.ErrorReason errorReason) {
                                    }
                                });
                    }

                    @Override
                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        final String errorMessage = youTubeInitializationResult.toString();
                        youTubeThumbnailView.setImageResource(R.drawable.no_thumbnail);
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }

            thumb.setOnClickListener(v -> {
                String videoid = (String) thumb.getTag();
                Intent lightboxIntent = new Intent(context, YoutubeLightboxActivity.class);
                lightboxIntent.putExtra(YoutubeLightboxActivity.KEY_VIDEO_ID, videoid);
                context.startActivity(lightboxIntent);
            });
        }
    }

}
