package com.example.personaltutoringservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.TutorViewHolder> {

    private List<Tutor> tutorList;

    public TutorAdapter(List<Tutor> tutorList) {
        this.tutorList = tutorList;
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

        // Name
        holder.tvName.setText(tutor.getName());

        // Subject
        String subject = tutor.getSubject() != null ? tutor.getSubject() : "General";
        holder.tvSubject.setText(subject);

        // Dynamic subject color
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
                colorRes = R.color.subject_programming;
                break;
            default:
                colorRes = R.color.subject_default;
        }

        holder.tvSubject.setBackgroundTintList(
                holder.itemView.getContext().getColorStateList(colorRes)
        );

        // Rating
        if (tutor.getRating() != null) {
            holder.tvRating.setText(String.format("%.1f", tutor.getRating()));
        } else {
            holder.tvRating.setText("N/A");
        }

        // Price
        if (tutor.getPrice() != null) {
            holder.tvPrice.setText(String.format("$%.0f/hr", tutor.getPrice()));
        } else {
            holder.tvPrice.setText("$--/hr");
        }
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