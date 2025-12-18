package com.example.t_star.STUDENTACTIVITIES.Adaptar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Models.Attendance;

import java.util.List;

public class AttendanceAdapter
        extends RecyclerView.Adapter<AttendanceAdapter.VH> {

    private final Context context;
    private List<Attendance> list;

    public AttendanceAdapter(Context context, List<Attendance> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<Attendance> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_attendance, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Attendance attendance = list.get(position);

        holder.tvDate.setText(attendance.getDate());
        holder.tvSubject.setText(attendance.getSubject());

        String status = attendance.getStatus();
        holder.tvStatus.setText(capitalize(status));

        switch (status) {
            case Attendance.STATUS_PRESENT:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_present);
                break;

            case Attendance.STATUS_ABSENT:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_absent);
                break;

            case Attendance.STATUS_LEAVE:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_leave);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvDate, tvSubject, tvStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }
}
