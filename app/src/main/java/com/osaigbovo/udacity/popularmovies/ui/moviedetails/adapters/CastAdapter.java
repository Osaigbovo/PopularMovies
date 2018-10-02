package com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.Cast;
import com.osaigbovo.udacity.popularmovies.ui.widget.CircularImageView;
import com.osaigbovo.udacity.popularmovies.util.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.osaigbovo.udacity.popularmovies.util.AppConstants.BASE_IMAGE_URL;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> castList;

    public CastAdapter() {
    }

    public void addCasts(List<Cast> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CastAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CastAdapter.CastViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_cast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.CastViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class CastViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_cast)
        CircularImageView mMovieCastImage;
        @BindView(R.id.text_cast_name)
        TextView mMovieCastName;
        @BindView(R.id.text_cast_character)
        TextView mMovieCastCharacter;

        CastViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void onBind(final int position) {

            final Cast cast = castList.get(position);

            Context context = mMovieCastImage.getContext();
            GlideApp.with(context)
                    .load(BASE_IMAGE_URL + cast.getProfilePath())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .priority(Priority.NORMAL)
                    .placeholder(R.drawable.ic_crew_cast)
                    //.centerCrop()
                    .into(mMovieCastImage);

            mMovieCastName.setText(cast.getName());
            mMovieCastCharacter.setText(cast.getCharacter());

        }
    }
}
