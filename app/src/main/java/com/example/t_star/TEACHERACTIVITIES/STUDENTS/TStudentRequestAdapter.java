package com.example.t_star.TEACHERACTIVITIES.STUDENTS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TStudentRequestAdapter
        extends RecyclerView.Adapter<TStudentRequestAdapter.VH> {

    public interface OnRequestAction {
        void onAccept(String uid);
        void onRemove(String uid);
    }

    private final Context context;
    private final List<String> uidList;
    private final OnRequestAction listener;

    private final Map<String, String> nameMap = new HashMap<>();
    private final Map<String, String> emailMap = new HashMap<>();

    public TStudentRequestAdapter(Context c,
                                  List<String> list,
                                  OnRequestAction l) {
        context = c;
        uidList = list;
        listener = l;
    }

    public void updateStudentData(String uid, String name, String email) {
        nameMap.put(uid, name);
        emailMap.put(uid, email);

        int index = uidList.indexOf(uid);
        if (index != -1) notifyItemChanged(index);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.tstudentreqview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        String uid = uidList.get(pos);

        h.tvName.setText(
                nameMap.get(uid) != null ? nameMap.get(uid) : "Loading..."
        );
        h.tvEmail.setText(
                emailMap.get(uid) != null ? "Email : " + emailMap.get(uid) : ""
        );
        h.tvID.setText("UID : " + uid);

        h.btnAccept.setOnClickListener(v ->
                listener.onAccept(uid)
        );

        h.btnRemove.setOnClickListener(v ->
                listener.onRemove(uid)
        );
    }

    @Override
    public int getItemCount() {
        return uidList.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvID;
        MaterialButton btnAccept, btnRemove;

        public VH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvStudentName);
            tvEmail = v.findViewById(R.id.tvStudentEmail);
            tvID = v.findViewById(R.id.tvStudentID);
            btnAccept = v.findViewById(R.id.btnAccept);
            btnRemove = v.findViewById(R.id.btnRemove);
        }
    }
}
