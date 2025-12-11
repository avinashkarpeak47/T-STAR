package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS;

import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class TAssignmentAdapter extends RecyclerView.Adapter<TAssignmentAdapter.VH> {

    public interface OnAssignmentClick {
        void onClick(TAssignmentModel model);
    }

    private final List<TAssignmentModel> list;
    private final OnAssignmentClick listener;

    public TAssignmentAdapter(List<TAssignmentModel> list, OnAssignmentClick listener) {
        this.list = list;
        this.listener = listener;
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

        TAssignmentModel m = list.get(pos);

        h.title.setText(m.getTitle());
        h.desc.setText(m.getDescription());

        h.itemView.setOnClickListener(v -> listener.onClick(m));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {

        TextView title, desc;

        public VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvAssignmentTitle);
            desc = v.findViewById(R.id.tvAssignmentDesc);
        }
    }
}
