package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class TAssignStudentStatusAdapter
        extends RecyclerView.Adapter<TAssignStudentStatusAdapter.VH> {

    private final List<TAssignStudentStatusModel> list;

    public TAssignStudentStatusAdapter(List<TAssignStudentStatusModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tassign_student_status, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        TAssignStudentStatusModel m = list.get(pos);

        h.name.setText(m.getName());

        if (m.isSubmitted()) {
            h.status.setText("SUBMITTED");
            h.status.setTextColor(Color.parseColor("#2E7D32"));
            h.name.setTypeface(null, Typeface.BOLD);
            h.name.setTextColor(Color.BLACK);
        } else {
            h.status.setText("PENDING");
            h.status.setTextColor(Color.GRAY);
            h.name.setTypeface(null, Typeface.NORMAL);
            h.name.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, status;

        VH(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.tvStudentName);
            status = v.findViewById(R.id.tvStatus);
        }
    }
}
