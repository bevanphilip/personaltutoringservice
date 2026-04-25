package com.example.personaltutoringservice;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.TutorViewHolder> {

    private List<Tutor> tutorList;
    private OnTutorClickListener listener;

    public interface OnTutorClickListener {
        void onTutorClick(Tutor tutor);
    }

    public TutorAdapter(List<Tutor> tutorList, OnTutorClickListener listener) {
        this.tutorList = tutorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutor, parent, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        Tutor tutor = tutorList.get(position);

        holder.tvName.setText(tutor.getName() != null ? tutor.getName() : "Tutor");

        String subject = tutor.getSubject() != null ? tutor.getSubject() : "General";
        holder.tvSubject.setText(subject);

        int colorRes;
        switch (subject.toLowerCase()) {
            case "mathematics":
                colorRes = R.color.subject_math;
                break;
            case "science":
            case "chemistry":
            case "physics":
                colorRes = R.color.subject_science;
                break;
            case "programming":
            case "engineering":
            case "java":
                colorRes = R.color.subject_programming;
                break;
            default:
                colorRes = R.color.subject_default;
        }

        holder.tvSubject.setBackgroundTintList(
                ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), colorRes)
                )
        );

        if (tutor.getRating() != null) {
            holder.tvRating.setText("⭐ " + String.format("%.1f", tutor.getRating()));
        } else {
            holder.tvRating.setText("⭐ N/A");
        }

        if (tutor.getPrice() != null) {
            holder.tvPrice.setText(String.format("$%.0f/hr", tutor.getPrice()));
        } else {
            holder.tvPrice.setText("$--/hr");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTutorClick(tutor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    public static class TutorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSubject, tvRating, tvPrice;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTutorName);
            tvSubject = itemView.findViewById(R.id.tvTutorSubject);
            tvRating = itemView.findViewById(R.id.tvTutorRating);
            tvPrice = itemView.findViewById(R.id.tvTutorPrice);
        }
    }
}