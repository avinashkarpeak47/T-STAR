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

public class TStudentAdapter extends RecyclerView.Adapter<TStudentAdapter.VH> {

    private final Context context;
    private final List<TStudentModel> original = new ArrayList<>();
    private final List<TStudentModel> filtered = new ArrayList<>();

    public TStudentAdapter(Context c, List<TStudentModel> list) {
        context = c;
        updateList(list);
    }

    public void updateList(List<TStudentModel> list) {
        original.clear();
        filtered.clear();
        if (list != null) {
            original.addAll(list);
            filtered.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.tstudentsview, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        TStudentModel m = filtered.get(pos);
        h.tvName.setText(m.getStudentName());
        h.tvEmail.setText("Email : " + m.getStudentEmail());
        h.tvID.setText("ID : " + m.getStudentId());
        h.img.setImageResource(m.getImageRes());
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    public void filter(String q) {
        filtered.clear();
        if (q == null || q.trim().isEmpty()) {
            filtered.addAll(original);
        } else {
            q = q.toLowerCase();
            for (TStudentModel s : original) {
                if (s.getStudentName().toLowerCase().contains(q) ||
                        s.getStudentEmail().toLowerCase().contains(q)) {
                    filtered.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvEmail, tvID;

        VH(View v) {
            super(v);
            img = v.findViewById(R.id.imgStudent);
            tvName = v.findViewById(R.id.tvStudentName);
            tvEmail = v.findViewById(R.id.tvStudentEmail);
            tvID = v.findViewById(R.id.tvStudentID);
        }
    }
}
