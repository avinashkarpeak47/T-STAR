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
import com.example.t_star.STUDENTACTIVITIES.Models.Attendance;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private final Context context;
    private List<Attendance> attendanceList = new ArrayList<>();

    public AttendanceAdapter(Context context, List<Attendance> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    public void updateList(List<Attendance> newList) {
        this.attendanceList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance item = attendanceList.get(position);

        holder.tvStudentName.setText(item.getStudentName());
        holder.tvRollNo.setText("Roll: " + item.getRollNo());
        holder.tvDateSubject.setText(item.getDate() + " · " + item.getSubject());

        String status = item.getStatus();
        holder.tvStatusText.setText(statusText(status));
        setStatusColor(holder.viewStatus, status);
    }

    @Override
    public int getItemCount() {
        return attendanceList == null ? 0 : attendanceList.size();
    }

    private String statusText(String status) {
        if (Attendance.STATUS_PRESENT.equals(status)) return "Present";
        if (Attendance.STATUS_ABSENT.equals(status)) return "Absent";
        if (Attendance.STATUS_LEAVE.equals(status)) return "Leave";
        return "";
    }

    private void setStatusColor(View viewStatus, String status) {
        int colorRes;
        if (Attendance.STATUS_PRESENT.equals(status)) {
            colorRes = R.color.status_submitted;
        } else if (Attendance.STATUS_ABSENT.equals(status)) {
            colorRes = R.color.status_pending;
        } else if (Attendance.STATUS_LEAVE.equals(status)) {
            colorRes = R.color.status_checked;
        } else {
            colorRes = android.R.color.darker_gray;
        }

        viewStatus.setBackgroundTintList(
                ContextCompat.getColorStateList(context, colorRes)
        );
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName, tvRollNo, tvDateSubject, tvStatusText;
        View viewStatus;
        CardView cardRoot;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = (CardView) itemView;
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvRollNo = itemView.findViewById(R.id.tvRollNo);
            tvDateSubject = itemView.findViewById(R.id.tvDateSubject);
            tvStatusText = itemView.findViewById(R.id.tvStatusText);
        }
    }
}
