package com.example.t_star.TEACHERACTIVITIES.ATTENDANCE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.ArrayList;

public class AttendanceAdapter
        extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private ArrayList<Attendacemodel> studentList;
    private boolean isHoliday = false;
    private boolean isLocked = false;
    private boolean showMonthly = true; // teacher screen


    public AttendanceAdapter(ArrayList<Attendacemodel> studentList) {
        this.studentList = studentList;
    }

    public void setHoliday(boolean holiday) {
        isHoliday = holiday;

        if (holiday) {
            for (Attendacemodel s : studentList) {
                s.status = "leave";   // 🔥 holiday = leave
            }
        }
        notifyDataSetChanged();
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tattendanceview, parent, false);
        return new AttendanceViewHolder(view);
    }
    @Override
    public void onBindViewHolder(
            @NonNull AttendanceViewHolder holder, int position) {

        Attendacemodel student = studentList.get(position);

        holder.tvRoll.setText(String.valueOf(student.rollNo));
        holder.tvName.setText(student.name);

        // ✅ PERCENTAGE DISPLAY (SAFE, NO MIX)
        if (showMonthly) {
            holder.tvPercentage.setText(student.monthlyPercentage + "%");
        } else {
            holder.tvPercentage.setText(student.percentage + "%");
        }

        holder.toggleAttendance.setOnCheckedChangeListener(null);

        holder.toggleAttendance.setChecked(
                "present".equals(student.status)
        );

        holder.toggleAttendance.setEnabled(!isHoliday && !isLocked);

        holder.toggleAttendance.setOnCheckedChangeListener((b, checked) -> {
            student.status = checked ? "present" : "absent";
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView tvRoll, tvName,tvPercentage;
        ToggleButton toggleAttendance;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoll = itemView.findViewById(R.id.tvRoll);
            tvName = itemView.findViewById(R.id.tvName);
            tvPercentage = itemView.findViewById(R.id.tvPercentage);
            toggleAttendance = itemView.findViewById(R.id.toggleAttendance);
        }
    }

}
