package com.example.t_star.TEACHERACTIVITIES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class TAssignmentAdapter extends RecyclerView.Adapter<TAssignmentAdapter.VH> {

    public interface OnItemClick {
        void onItemClick(TAssignmentModel item);
    }

    private final List<TAssignmentModel> items;
    private final OnItemClick listener;

    public TAssignmentAdapter(List<TAssignmentModel> items, OnItemClick listener){
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trecyclerview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TAssignmentModel m = items.get(position);
        holder.title.setText(m.getTitle());
        holder.desc.setText(m.getDescription());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(m));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, desc;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvAssignmentTitle);
            desc = itemView.findViewById(R.id.tvAssignmentDesc);
        }
    }
}
