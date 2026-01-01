package com.example.t_star.TEACHERACTIVITIES.CLASSES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.ArrayList;
import java.util.List;

public class TClassesAdapter extends RecyclerView.Adapter<TClassesAdapter.ClassVH> {

    // ================= CLICK =================
    public interface OnClassClick {
        void onClassSelect(TClassesModel model, int position);
    }

    // ================= LONG CLICK =================
    public interface OnClassLongClick {
        void onLongClick(TClassesModel model, int position);
    }

    private final Context context;
    private final List<TClassesModel> list;
    private final OnClassClick clickListener;
    private final OnClassLongClick longClickListener;

    // ================= CONSTRUCTOR =================
    public TClassesAdapter(Context context,
                           ArrayList<TClassesModel> list,
                           OnClassClick clickListener,
                           OnClassLongClick longClickListener) {

        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    // ================= VIEW HOLDER =================
    @NonNull
    @Override
    public ClassVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.tclassesview, parent, false);

        return new ClassVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassVH holder, int position) {

        TClassesModel model = list.get(position);

        holder.tvClassName.setText(model.getClassName());
        holder.tvClassId.setText("Class ID: " + model.getClassId());
        holder.tvTotal.setText("Students: " + model.getStudentsCount());

        // ---------- CLICK ----------
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClassSelect(model, position);
            }
        });

        // ---------- LONG CLICK (DELETE) ----------
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(model, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= UPDATE LIST =================
    public void updateList(List<TClassesModel> newList) {
        list.clear();
        if (newList != null) list.addAll(newList);
        notifyDataSetChanged();
    }

    // ================= VH =================
    static class ClassVH extends RecyclerView.ViewHolder {

        TextView tvClassName, tvClassId, tvTotal;

        public ClassVH(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassId   = itemView.findViewById(R.id.tvClassId);
            tvTotal     = itemView.findViewById(R.id.tvtotal);
        }
    }
}
