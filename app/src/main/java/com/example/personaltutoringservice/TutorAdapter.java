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
        holder.tvName.setText("Name: "     + tutor.getName());
        holder.tvSubject.setText("Subject: " + tutor.getSubject());
        holder.tvPrice.setText("Price: $"  + tutor.getPrice() + "/hr");
        holder.tvLocation.setText("Location: " + tutor.getLocation());
    }

    @Override
    public int getItemCount() { return tutorList.size(); }

    public static class TutorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSubject, tvLocation, tvPrice;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvTutorName);
            tvSubject   = itemView.findViewById(R.id.tvTutorSubject);
            tvPrice    = itemView.findViewById(R.id.tvTutorPrice);
            tvLocation = itemView.findViewById(R.id.tvTutorLocation);
        }
    }
}