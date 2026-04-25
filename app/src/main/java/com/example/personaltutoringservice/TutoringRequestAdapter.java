package com.example.personaltutoringservice;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TutoringRequestAdapter extends RecyclerView.Adapter<TutoringRequestAdapter.RequestViewHolder> {

    private List<TutoringRequest> requestList;
    private OnCancelClickListener cancelClickListener;

    public interface OnCancelClickListener {
        void onCancelClick(TutoringRequest request);
    }

    public TutoringRequestAdapter(List<TutoringRequest> requestList, OnCancelClickListener cancelClickListener) {
        this.requestList = requestList;
        this.cancelClickListener = cancelClickListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutoring_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        TutoringRequest request = requestList.get(position);

        String status = request.getStatus() != null ? request.getStatus() : "Pending";
        String paymentStatus = request.getPaymentStatus() != null ? request.getPaymentStatus() : "Unpaid";

        holder.tvTutorName.setText(request.getTutorName());
        holder.tvSubject.setText("Subject: " + request.getSubject());
        holder.tvAvailability.setText("Availability: " + request.getAvailability());
        holder.tvDate.setText("Date: " + (request.getRequestedDate() != null ? request.getRequestedDate() : "--"));
        holder.tvTime.setText("Time: " + (request.getRequestedTime() != null ? request.getRequestedTime() : "--"));
        holder.tvSessionType.setText("Session Type: " + (request.getSessionType() != null ? request.getSessionType() : "--"));

        if (request.getPrice() != null) {
            holder.tvPrice.setText(String.format("Price: $%.0f/hr", request.getPrice()));
        } else {
            holder.tvPrice.setText("Price: $--/hr");
        }

        holder.tvStatus.setText("Status: " + status);
        holder.tvPaymentStatus.setText("Payment: " + paymentStatus);

        holder.btnCancel.setOnClickListener(v -> {
            if (cancelClickListener != null) {
                cancelClickListener.onCancelClick(request);
            }
        });

        if ("Accepted".equalsIgnoreCase(status)) {
            holder.btnMessageTutor.setVisibility(View.VISIBLE);
            holder.btnMessageTutor.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("tutorName", request.getTutorName());
                v.getContext().startActivity(intent);
            });
        } else {
            holder.btnMessageTutor.setVisibility(View.GONE);
        }

        if ("Accepted".equalsIgnoreCase(status) && !"Paid".equalsIgnoreCase(paymentStatus)) {
            holder.btnPayNow.setVisibility(View.VISIBLE);
            holder.btnPayNow.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PaymentActivity.class);
                intent.putExtra("requestId", request.getId());
                intent.putExtra("tutorName", request.getTutorName());
                intent.putExtra("price", request.getPrice() != null ? request.getPrice() : 0.0);
                v.getContext().startActivity(intent);
            });
        } else {
            holder.btnPayNow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTutorName, tvSubject, tvAvailability, tvDate, tvTime, tvSessionType,
                tvPrice, tvStatus, tvPaymentStatus;
        Button btnCancel, btnMessageTutor, btnPayNow;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTutorName = itemView.findViewById(R.id.tvRequestTutorName);
            tvSubject = itemView.findViewById(R.id.tvRequestSubject);
            tvAvailability = itemView.findViewById(R.id.tvRequestAvailability);
            tvDate = itemView.findViewById(R.id.tvRequestDate);
            tvTime = itemView.findViewById(R.id.tvRequestTime);
            tvSessionType = itemView.findViewById(R.id.tvRequestSessionType);
            tvPrice = itemView.findViewById(R.id.tvRequestPrice);
            tvStatus = itemView.findViewById(R.id.tvRequestStatus);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);

            btnCancel = itemView.findViewById(R.id.btnCancelRequest);
            btnMessageTutor = itemView.findViewById(R.id.btnMessageTutor);
            btnPayNow = itemView.findViewById(R.id.btnPayNow);
        }
    }
}