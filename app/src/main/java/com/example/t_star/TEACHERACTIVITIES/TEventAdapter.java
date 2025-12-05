package com.example.t_star.TEACHERACTIVITIES;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class TEventAdapter extends RecyclerView.Adapter<TEventAdapter.VH> {

    public interface OnEventClick {
        void onEventClick(TEventModel item);
    }

    private final List<TEventModel> list;
    private final OnEventClick listener;

    public TEventAdapter(List<TEventModel> list, OnEventClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teventview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TEventModel m = list.get(position);

        holder.title.setText(m.getTitle());
        holder.date.setText(m.getDate());
        holder.desc.setText(m.getDescription());

        holder.itemView.setOnClickListener(v -> listener.onEventClick(m));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, date, desc;

        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvEventTitle);
            date = v.findViewById(R.id.tvEventDate);
            desc = v.findViewById(R.id.tvEventDesc);
        }
    }
}
