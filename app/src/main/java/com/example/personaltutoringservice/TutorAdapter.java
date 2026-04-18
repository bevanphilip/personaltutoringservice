package com.example.personaltutoringservice;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

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

        holder.tvName.setText("Name: " + safeText(tutor.getName()));
        holder.tvSubject.setText("Subject: " + safeText(tutor.getSubject()));
        holder.tvPrice.setText("Price: $" + safeText(tutor.getPrice()) + "/hr");
        holder.tvLocation.setText("Location: " + safeText(tutor.getLocation()));

        if (tutor.getRating() > 0) {
            holder.tvRating.setText("Rating: " +
                    String.format(Locale.getDefault(), "%.1f", tutor.getRating()));
        } else {
            holder.tvRating.setText("Rating: No ratings yet");
        }

        holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTutorClick(tutor);
            }
        });

        holder.tvName.setOnClickListener(v -> {
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
        TextView tvName, tvSubject, tvPrice, tvRating, tvLocation;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTutorName);
            tvSubject = itemView.findViewById(R.id.tvTutorSubject);
            tvPrice = itemView.findViewById(R.id.tvTutorPrice);
            tvRating = itemView.findViewById(R.id.tvTutorRating);
            tvLocation = itemView.findViewById(R.id.tvTutorLocation);
        }
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "Not set" : value;
    }
}