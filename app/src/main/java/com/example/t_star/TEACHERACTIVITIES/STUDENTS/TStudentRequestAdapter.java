package com.example.t_star.TEACHERACTIVITIES.STUDENTS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TStudentRequestAdapter extends RecyclerView.Adapter<TStudentRequestAdapter.VH> {

    public interface OnAcceptClick {
        void onAccept(String uid);
    }

    private final Context context;
    private final List<String> uidList;
    private final OnAcceptClick listener;

    // nameMap → UID → Student Name
    private final Map<String, String> nameMap = new HashMap<>();

    public TStudentRequestAdapter(Context c, List<String> list, OnAcceptClick l) {
        this.context = c;
        this.uidList = list;
        this.listener = l;
    }

    // 🔥 Call this from Activity when name loads
    public void updateStudentName(String uid, String name) {
        nameMap.put(uid, name);
        int index = uidList.indexOf(uid);
        if (index != -1) {
            notifyItemChanged(index);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tstudentreqview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        String uid = uidList.get(pos);

        // Show "Loading..." until name arrives
        String name = nameMap.get(uid);
        h.tvName.setText(name != null ? name : "Loading...");

        h.tvID.setText("UID: " + uid);

        h.btnAccept.setOnClickListener(v -> listener.onAccept(uid));
    }

    @Override
    public int getItemCount() {
        return uidList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvID;
        Button btnAccept;

        public VH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvStudentName);
            tvID = v.findViewById(R.id.tvStudentID);
            btnAccept = v.findViewById(R.id.btnAccept);
        }
    }
}
