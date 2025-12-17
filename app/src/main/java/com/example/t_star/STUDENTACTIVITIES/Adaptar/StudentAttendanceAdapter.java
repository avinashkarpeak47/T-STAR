package com.example.t_star.STUDENTACTIVITIES.Adaptar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Models.StudentAttendance;

import java.util.List;

public class StudentAttendanceAdapter
        extends RecyclerView.Adapter<StudentAttendanceAdapter.ViewHolder> {

    private final Context context;
    private List<StudentAttendance> list;

    public StudentAttendanceAdapter(Context context, List<StudentAttendance> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<StudentAttendance> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_student_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StudentAttendance model = list.get(position);

        holder.tvDate.setText(model.getDate());

        String status = model.getStatus();
        holder.tvStatus.setText(status.toUpperCase());

        if (StudentAttendance.STATUS_PRESENT.equals(status)) {
            holder.tvStatus.setTextColor(context.getColor(R.color.green));
        } else if (StudentAttendance.STATUS_ABSENT.equals(status)) {
            holder.tvStatus.setTextColor(context.getColor(R.color.red));
        } else {
            holder.tvStatus.setTextColor(context.getColor(R.color.orange));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
