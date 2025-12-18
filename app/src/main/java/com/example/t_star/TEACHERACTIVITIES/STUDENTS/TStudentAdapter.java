package com.example.t_star.TEACHERACTIVITIES.STUDENTS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.ArrayList;
import java.util.List;

public class TStudentAdapter extends RecyclerView.Adapter<TStudentAdapter.StudentVH> {

    private final Context context;
    private final List<TStudentModel> originalList = new ArrayList<>();
    private final List<TStudentModel> filteredList = new ArrayList<>();

    public TStudentAdapter(Context context, List<TStudentModel> list) {
        this.context = context;
        updateList(list); // 🔥 initial load
    }

    // 🔥 MOST IMPORTANT FIX
    public void updateList(List<TStudentModel> newList) {

        originalList.clear();
        filteredList.clear();

        if (newList != null) {
            originalList.addAll(newList);
            filteredList.addAll(newList);
        }

        notifyDataSetChanged(); // 🔥 force UI refresh
    }

    @NonNull
    @Override
    public StudentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.tstudentsview, parent, false);
        return new StudentVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentVH holder, int pos) {

        TStudentModel m = filteredList.get(pos);

        holder.tvName.setText(m.getStudentName());
        holder.tvID.setText("ID: " + m.getStudentId());
        holder.ivImage.setImageResource(m.getImageRes());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // 🔍 Search support
    public void filter(String q) {

        filteredList.clear();

        if (q == null || q.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            q = q.toLowerCase().trim();
            for (TStudentModel s : originalList) {
                if (s.getStudentName().toLowerCase().contains(q) ||
                        s.getStudentId().toLowerCase().contains(q)) {
                    filteredList.add(s);
                }
            }
        }

        notifyDataSetChanged();
    }

    static class StudentVH extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName, tvID;

        public StudentVH(@NonNull View v) {
            super(v);
            ivImage = v.findViewById(R.id.imgStudent);
            tvName = v.findViewById(R.id.tvStudentName);
            tvID = v.findViewById(R.id.tvStudentID);
        }
    }
}
