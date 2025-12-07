package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class TAssignmentAdapter extends RecyclerView.Adapter<TAssignmentAdapter.AssignmentVH> {

    public interface OnAssignmentClick {
        void onAssignmentSelect(TAssignmentModel model, int position);
    }

    private final List<TAssignmentModel> list;
    private final OnAssignmentClick listener;

    public TAssignmentAdapter(List<TAssignmentModel> list, OnAssignmentClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignmentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tassignmentsview, parent, false);
        return new AssignmentVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentVH holder, int position) {
        TAssignmentModel model = list.get(position);

        holder.tvTitle.setText(model.getTitle());
        holder.tvDesc.setText(model.getDescription());

        holder.card.setOnClickListener(v ->
                listener.onAssignmentSelect(model, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AssignmentVH extends RecyclerView.ViewHolder {

        CardView card;
        TextView tvTitle, tvDesc;

        public AssignmentVH(@NonNull View itemView) {
            super(itemView);
            card = (CardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvAssignmentTitle);
            tvDesc = itemView.findViewById(R.id.tvAssignmentDesc);
        }
    }
}
