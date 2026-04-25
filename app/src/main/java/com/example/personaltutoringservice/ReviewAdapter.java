package com.example.personaltutoringservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvReviewRating.setText("⭐ " + String.format("%.1f", review.getRating()));
        holder.tvReviewComment.setText("\"" + review.getComment() + "\"");
        holder.tvReviewStudent.setText("- " + review.getStudentName());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewRating, tvReviewComment, tvReviewStudent;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewRating = itemView.findViewById(R.id.tvReviewRating);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewStudent = itemView.findViewById(R.id.tvReviewStudent);
        }
    }
}
