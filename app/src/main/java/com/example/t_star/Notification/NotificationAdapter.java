package com.example.t_star.Notification;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    List<NotificationModel> list;

    public NotificationAdapter(List<NotificationModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int p) {
        NotificationModel m = list.get(p);
        h.title.setText(m.getTitle());
        h.msg.setText(m.getMessage());
        h.time.setText(
                DateFormat.format("dd MMM, hh:mm a", m.getTime())
        );
    }

    @Override public int getItemCount() { return list.size(); }

    class Holder extends RecyclerView.ViewHolder {
        TextView title, msg, time;
        Holder(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            msg   = v.findViewById(R.id.tvMessage);
            time  = v.findViewById(R.id.tvTime);
        }
    }
}

