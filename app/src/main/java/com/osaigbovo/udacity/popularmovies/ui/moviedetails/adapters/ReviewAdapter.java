/*
 * Copyright 2018.  Osaigbovo Odiase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osaigbovo.udacity.popularmovies.ui.moviedetails.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.Review;
import com.osaigbovo.udacity.popularmovies.ui.widget.ExpandableTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
