package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.ArrayList;
import java.util.List;

public class TAssignmentAdapter extends RecyclerView.Adapter<TAssignmentAdapter.VH> {

    public interface OnAssignmentClick {
        void onClick(TAssignmentModel model);
    }

    private final List<TAssignmentModel> fullList = new ArrayList<>();     // master list
    private final List<TAssignmentModel> filteredList = new ArrayList<>(); // visible list
    private final OnAssignmentClick listener;

    public TAssignmentAdapter(OnAssignmentClick listener) {
        this.listener = listener;
    }

    // 🔹 Called when Firebase gives new data
    public void setData(List<TAssignmentModel> newList) {

        fullList.clear();
        fullList.addAll(newList);

        filteredList.clear();
        filteredList.addAll(newList);

        notifyDataSetChanged();
    }

    // 🔹 Search Filter
    public void filter(String text) {

        filteredList.clear();

        if (text == null || text.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            text = text.toLowerCase();

            for (TAssignmentModel m : fullList) {
                String t = m.getTitle() == null ? "" : m.getTitle().toLowerCase();
                String d = m.getDescription() == null ? "" : m.getDescription().toLowerCase();

                if (t.contains(text) || d.contains(text)) {
                    filteredList.add(m);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tassignmentsview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        TAssignmentModel m = filteredList.get(pos);

        h.title.setText(m.getTitle());
        h.desc.setText(m.getDescription());

        h.itemView.setOnClickListener(v -> listener.onClick(m));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView title, desc;

        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvAssignmentTitle);
            desc  = v.findViewById(R.id.tvAssignmentDesc);
        }
    }
}
