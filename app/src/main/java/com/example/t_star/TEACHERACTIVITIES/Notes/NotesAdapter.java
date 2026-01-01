package com.example.t_star.TEACHERACTIVITIES.Notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.Holder> {

    public List<NotesModel> list;
    Context context;


    OnNoteClick listener;

    public interface OnNoteClick {
        void onClick(NotesModel note);
    }

    public NotesAdapter(List<NotesModel> list, Context ctx, OnNoteClick l){
        this.list = list;
        this.context = ctx;
        this.listener = l;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
                .inflate(R.layout.row_notes, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull Holder h, int i) {

        NotesModel n = list.get(i);
        h.title.setText(n.title);
        h.content.setText(n.content);
        h.date.setText(n.getFormattedDate());
        h.itemView.setOnClickListener(v -> listener.onClick(n));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class Holder extends RecyclerView.ViewHolder {

        TextView title, content,date;

        Holder(View v){
            super(v);
            title = v.findViewById(R.id.rowTitle);
            content = v.findViewById(R.id.rowContent);
            date = v.findViewById(R.id.rowDate);

        }
    }
}
