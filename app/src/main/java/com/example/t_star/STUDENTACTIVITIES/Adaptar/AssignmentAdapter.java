package com.example.t_star.STUDENTACTIVITIES.Adaptar;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Models.Assignment;

import java.util.ArrayList;
import java.util.List;



public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private Context context;
    private List<Assignment> assignmentList = new ArrayList<>();

    public AssignmentAdapter(Context context, List<Assignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    public void updateList(List<Assignment> newList) {
        this.assignmentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);

        holder.tvTitle.setText(assignment.getTitle());
        holder.tvDescription.setText(assignment.getDescription());
        holder.tvExtra.setText(assignment.getExtraInfo());

        String status = assignment.getStatus();
        holder.tvStatusText.setText(statusText(status));
        setStatusColor(holder.viewStatus, status);
    }

    @Override
    public int getItemCount() {
        return assignmentList == null ? 0 : assignmentList.size();
    }

    private String statusText(String status) {
        switch (status) {
            case Assignment.STATUS_PENDING:
                return "Pending";
            case Assignment.STATUS_SUBMITTED:
                return "Submitted";
            case Assignment.STATUS_CHECKED:
                return "Checked";
            default:
                return "";
        }
    }

    private void setStatusColor(View viewStatus, String status) {
        int colorRes;
        switch (status) {
            case Assignment.STATUS_PENDING:
                colorRes = R.color.status_pending;
                break;
            case Assignment.STATUS_SUBMITTED:
                colorRes = R.color.status_submitted;
                break;
            case Assignment.STATUS_CHECKED:
                colorRes = R.color.status_checked;
                break;
            default:
                colorRes = android.R.color.darker_gray;
        }
        viewStatus.setBackgroundTintList(
                ContextCompat.getColorStateList(context, colorRes)
        );
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvExtra, tvStatusText;
        View viewStatus;
        CardView cardRoot;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = (CardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvExtra = itemView.findViewById(R.id.tvExtra);
            tvStatusText = itemView.findViewById(R.id.tvStatusText);
            viewStatus = itemView.findViewById(R.id.viewStatus);
        }
    }
}

