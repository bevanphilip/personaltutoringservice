package com.example.personaltutoringservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TutorRequestAdapter extends RecyclerView.Adapter<TutorRequestAdapter.RequestViewHolder> {

    private List<TutoringRequest> requestList;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(TutoringRequest request);
        void onDecline(TutoringRequest request);
    }

    public TutorRequestAdapter(List<TutoringRequest> requestList, OnRequestActionListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutor_incoming_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        TutoringRequest request = requestList.get(position);

        holder.tvStudent.setText("Student: User");
        holder.tvTutorName.setText("Tutor: " + request.getTutorName());
        holder.tvSubject.setText("Subject: " + request.getSubject());
        holder.tvAvailability.setText("Availability: " + request.getAvailability());
        holder.tvStatus.setText("Status: " + request.getStatus());

        if (request.getPrice() != null) {
            holder.tvPrice.setText(String.format("Price: $%.0f/hr", request.getPrice()));
        } else {
            holder.tvPrice.setText("Price: $--/hr");
        }

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(request));
        holder.btnDecline.setOnClickListener(v -> listener.onDecline(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudent, tvTutorName, tvSubject, tvAvailability, tvPrice, tvStatus;
        Button btnAccept, btnDecline;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudent = itemView.findViewById(R.id.tvIncomingStudent);
            tvTutorName = itemView.findViewById(R.id.tvIncomingTutorName);
            tvSubject = itemView.findViewById(R.id.tvIncomingSubject);
            tvAvailability = itemView.findViewById(R.id.tvIncomingAvailability);
            tvPrice = itemView.findViewById(R.id.tvIncomingPrice);
            tvStatus = itemView.findViewById(R.id.tvIncomingStatus);
            btnAccept = itemView.findViewById(R.id.btnAcceptRequest);
            btnDecline = itemView.findViewById(R.id.btnDeclineRequest);
        }
    }
}