package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class TAssignStudentStatusAdapter
        extends RecyclerView.Adapter<TAssignStudentStatusAdapter.VH> {

    List<TAssignStudentStatusModel> list;

    public TAssignStudentStatusAdapter(List<TAssignStudentStatusModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int v) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tassign_student_status,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        TAssignStudentStatusModel m = list.get(pos);

        h.name.setText(m.getName());

        if(m.isSubmitted()) {
            h.status.setText("SUBMITTED");
            h.status.setTextColor(Color.parseColor("#2E7D32"));

            h.itemView.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getFileUrl()));
                v.getContext().startActivity(i);
            });

        } else {
            h.status.setText("PENDING");
            h.status.setTextColor(Color.GRAY);

            // disable click
            h.itemView.setOnClickListener(null);
        }
    }


    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, status;
        public VH(@NonNull View v){
            super(v);
            name = v.findViewById(R.id.tvStudentName);
            status = v.findViewById(R.id.tvStatus);
        }
    }
}
