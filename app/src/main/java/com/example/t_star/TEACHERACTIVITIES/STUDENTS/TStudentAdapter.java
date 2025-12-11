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
    private final List<TStudentModel> originalList;   // main list
    private List<TStudentModel> filteredList;         // search list

    public TStudentAdapter(Context context, List<TStudentModel> list) {
        this.context = context;
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public StudentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tstudentsview, parent, false);
        return new StudentVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentVH holder, int position) {

        TStudentModel model = filteredList.get(position);

        holder.tvName.setText(model.getStudentName());
        holder.tvRoll.setText("Roll No: " + model.getStudentId());
        holder.ivImage.setImageResource(model.getImageRes());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    // ------------------ SEARCH FILTER ------------------
    public void filter(String query) {
        query = query.toLowerCase();
        filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (TStudentModel student : originalList) {
                if (student.getStudentName().toLowerCase().contains(query)) {
                    filteredList.add(student);
                }
            }
        }

        notifyDataSetChanged();
    }


    // ------------------ VIEW HOLDER ------------------
    static class StudentVH extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName, tvRoll;

        public StudentVH(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.imgStudent);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvRoll = itemView.findViewById(R.id.tvStudentRoll);
        }
    }
}
