package com.example.t_star.TEACHERACTIVITIES.CLASSES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class TClassesAdapter extends RecyclerView.Adapter<TClassesAdapter.ClassVH> {

    public interface OnClassClick {
        void onClassSelect(TClassesModel model, int position);
    }

    private final Context context;
    private List<TClassesModel> list;
    private final OnClassClick listener;

    public TClassesAdapter(Context context, List<TClassesModel> list, OnClassClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tclassesview, parent, false);
        return new ClassVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassVH holder, int position) {

        TClassesModel model = list.get(position);

        holder.tvClassName.setText(model.getClassName());
        holder.tvClassId.setText("Class ID: " + model.getClassId());
        holder.tvTotal.setText("Students: " + model.getStudentsCount());

        holder.itemView.setOnClickListener(v -> listener.onClassSelect(model, position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ★ ONLY THIS METHOD SHOULD EXIST ★
    public void updateList(List<TClassesModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class ClassVH extends RecyclerView.ViewHolder {

        TextView tvClassName, tvClassId, tvTotal;

        public ClassVH(@NonNull View itemView) {
            super(itemView);

            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassId = itemView.findViewById(R.id.tvClassId);
            tvTotal = itemView.findViewById(R.id.tvtotal);
        }
    }
}
