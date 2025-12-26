package com.example.t_star.STUDENTACTIVITIES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.t_star.R;

import java.util.List;

public class S_Fragment_Classes_RC_Adapter extends RecyclerView.Adapter<S_Fragment_Classes_RC_Adapter.VH> {

    private Context context;
    private List<S_Fragment_Classes_RC_Model> list;
    private OnClick listener;

    public interface OnClick {
        void onClick(S_Fragment_Classes_RC_Model model);
    }

    public S_Fragment_Classes_RC_Adapter(Context context, List<S_Fragment_Classes_RC_Model> list, OnClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.s_fragment_class_item, parent, false);
        return new VH(v);
    }


    //Rutuja

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        S_Fragment_Classes_RC_Model model = list.get(position);

        holder.tvClassName.setText("Class: " + model.getClassName());
        holder.tvTeacherName.setText("Teacher: " + model.getTeacherName());
        holder.tvClassId.setText("ID: " + model.getClassId());

        // RESET
        holder.tvStatus.setVisibility(View.GONE);
        holder.itemView.setAlpha(1f);
        holder.itemView.setClickable(true);

        if ("pending".equals(model.getStatus())) {

            holder.tvStatus.setText("  Pending  ");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pendingg);
            holder.tvStatus.setVisibility(View.VISIBLE);

            holder.itemView.setAlpha(0.6f);
            holder.itemView.setClickable(false);

        }
        else if ("approved".equals(model.getStatus())) {

            if (!model.isSeenApproved()) {
                holder.tvStatus.setText("  Approved  ");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approved);
                holder.tvStatus.setVisibility(View.VISIBLE);
            }

            holder.itemView.setAlpha(1f);
            holder.itemView.setClickable(true);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(model));
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvClassName, tvTeacherName, tvClassId ,tvStatus;

        public VH(@NonNull View v) {
            super(v);

            tvClassName = v.findViewById(R.id.tvClassName);
            tvTeacherName = v.findViewById(R.id.tvTeacherName);
//            tvSubjectName = v.findViewById(R.id.tvSubjectName);
            tvClassId = v.findViewById(R.id.tvClassId);
            tvStatus = v.findViewById(R.id.tvStatus);

        }
    }
}
