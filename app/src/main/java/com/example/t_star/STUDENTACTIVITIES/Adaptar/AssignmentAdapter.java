package com.example.t_star.STUDENTACTIVITIES.Adaptar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Models.Assignment;
import com.example.t_star.STUDENTACTIVITIES.S_Assignment_Submit_Activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter
        extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private final Context context;
    private List<Assignment> list;
    private final String classId;

    public AssignmentAdapter(Context context,
                             List<Assignment> assignmentList,
                             String classId) {
        this.context = context;
        this.list = assignmentList;
        this.classId = classId;
    }

    public void updateList(List<Assignment> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.s_item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        Assignment a = list.get(position);

        holder.tvTitle.setText(a.getTitle());
        holder.tvSubject.setText(a.getSubject());

        // ---- Due date formatting ----
        holder.tvDueDate.setText(
                "Due: " + formatDate(a.getDueDateMillis())
        );

        holder.tvMarks.setVisibility(View.GONE);

        // ---- DERIVE STATUS SAFELY ----
        String status = a.getStatus();
        if (status == null) {
            status = Assignment.STATUS_PENDING;
        }

        // ---- STATUS UI ----
        switch (status) {

            case Assignment.STATUS_PENDING:
                holder.tvStatus.setText("Pending");
                holder.tvStatus.setTextColor(Color.parseColor("#F57C00"));
                break;

            case Assignment.STATUS_SUBMITTED:
                holder.tvStatus.setText("Submitted");
                holder.tvStatus.setTextColor(Color.parseColor("#1976D2"));
                break;

            case Assignment.STATUS_EVALUATED:
                holder.tvStatus.setText("Evaluated");
                holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));

                holder.tvMarks.setVisibility(View.VISIBLE);
                holder.tvMarks.setText(
                        "Marks: " + a.getMarksObtained()
                                + " / " + a.getTotalMarks()
                );
                break;
        }

        // ---- CLICK BEHAVIOR ----
        holder.itemView.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Assignment clicked = list.get(pos);

            Intent intent =
                    new Intent(context,
                            S_Assignment_Submit_Activity.class);

            intent.putExtra("classId", classId);
            intent.putExtra("assignmentId",
                    clicked.getAssignmentId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvSubject, tvDueDate, tvStatus, tvMarks;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvAssignmentTitle);
            tvSubject = itemView.findViewById(R.id.tvAssignmentSubject);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMarks = itemView.findViewById(R.id.tvMarks);
        }
    }

    // ---- DATE FORMATTER ----
    private String formatDate(long millis) {
        if (millis <= 0) return "N/A";
        return new SimpleDateFormat(
                "dd MMM yyyy", Locale.getDefault()
        ).format(new Date(millis));

    }
}