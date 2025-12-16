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
    private List<TStudentModel> originalList;
    private List<TStudentModel> filteredList;

    public TStudentAdapter(Context context, List<TStudentModel> list) {
        this.context = context;
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
    }

    public void updateList(List<TStudentModel> newList) {
        originalList = newList;
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public StudentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tstudentsview, parent, false);
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

    public void filter(String q) {
        q = q.toLowerCase().trim();
        filteredList = new ArrayList<>();

        if (q.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (TStudentModel s : originalList) {
                if (s.getStudentName().toLowerCase().contains(q) || s.getStudentId().toLowerCase().contains(q)) {
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
