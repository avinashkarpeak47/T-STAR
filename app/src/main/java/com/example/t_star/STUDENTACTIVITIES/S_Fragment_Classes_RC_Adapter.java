package com.example.t_star.STUDENTACTIVITIES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

/**
 * RecyclerView adapter for showing list of student classes.
 */
public class S_Fragment_Classes_RC_Adapter
        extends RecyclerView.Adapter<S_Fragment_Classes_RC_Adapter.ClassViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(S_Fragment_Classes_RC_Model model);
    }

    private Context context;
    private List<S_Fragment_Classes_RC_Model> classList;
    private OnItemClickListener listener;

    public S_Fragment_Classes_RC_Adapter(Context context,
                                         List<S_Fragment_Classes_RC_Model> classList,
                                         OnItemClickListener listener) {

        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.s_item_classes_card, parent, false);

        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        S_Fragment_Classes_RC_Model model = classList.get(position);

        holder.tvClassName.setText(model.getClassName());
        holder.tvSubjectName.setText("Subject: " + model.getSubjectName());

        // Click listener to notify parent fragment/activity
        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return classList != null ? classList.size() : 0;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {

        TextView tvClassName, tvSubjectName;
        ImageView imgArrow;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            imgArrow = itemView.findViewById(R.id.imgArrow);
        }
    }
}
