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
    private List<TStudentModel> students;
    private final List<TStudentModel> fullList;

    public TStudentAdapter(Context context, List<TStudentModel> students) {
        this.context = context;
        this.students = students;
        this.fullList = new ArrayList<>(students);
    }

    @NonNull
    @Override
    public StudentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tstudentsview, parent, false);
        return new StudentVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentVH holder, int position) {
        TStudentModel model = students.get(position);
        holder.tvName.setText(model.getName());
        holder.tvRoll.setText("Roll No: " + model.getRollNo());
        holder.img.setImageResource(model.getImageRes());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    // 🔍 Search Filter
    public void filter(String text) {
        text = text.toLowerCase();
        students.clear();

        if (text.isEmpty()) {
            students.addAll(fullList);
        } else {
            for (TStudentModel item : fullList) {
                if (item.getName().toLowerCase().contains(text) ||
                        item.getRollNo().toLowerCase().contains(text)) {
                    students.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class StudentVH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvRoll;

        public StudentVH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgStudent);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvRoll = itemView.findViewById(R.id.tvStudentRoll);
        }
    }
}
