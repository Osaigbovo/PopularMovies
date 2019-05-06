package com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.Review;
import com.osaigbovo.udacity.popularmovies.ui.widget.ExpandableTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for Movie Reviews.
 *
 * @author Osaigbovo Odiase.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter() {
    }

    public void addReviews(List<Review> reviewList) {
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (reviewList != null) {
            return reviewList.size();
        } else {
            return 0;
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_review_name)
        TextView mMovieReviewName;
        @BindView(R.id.text_review_content)
        ExpandableTextView mMovieReviewContent;
        @BindView(R.id.image_button_expand_collapse)
        ImageButton mImageExpandCollapse;

        ReviewViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            // set interpolators for both expanding and collapsing animations
            mMovieReviewContent.setInterpolator(new OvershootInterpolator());

            // toggle the ExpandableTextView
            mImageExpandCollapse.setOnClickListener(this);
        }

        void onBind(final int position) {

            final Review data = reviewList.get(position);

            if (data.getAuthor() != null) {
                mMovieReviewName.setText(data.getAuthor());
            }

            if (data.getContent() != null) {
                mMovieReviewContent.setText(data.getContent());
            }
        }

        @Override
        public void onClick(View view) {
            mMovieReviewContent.toggle();

            mImageExpandCollapse.setImageResource(mMovieReviewContent.isExpanded() ?
                    R.drawable.ic_expand_arrow : R.drawable.ic_collapse_arrow);
        }
    }
}
