package com.example.t_star.TEACHERACTIVITIES.STUDENTS;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    // 🔥 store user data
    private final Map<String, String> nameMap = new HashMap<>();
    private final Map<String, String> emailMap = new HashMap<>();
    private final Map<String, String> profileMap = new HashMap<>();

    public TStudentRequestAdapter(Context c,
                                  List<String> list,
                                  OnRequestAction l) {
        context = c;
        uidList = list;
        listener = l;
    }

    // ✅ UPDATE USER DATA (called from Firebase)
    public void updateStudentData(String uid,
                                  String name,
                                  String email,
                                  String profileUrl) {

        nameMap.put(uid, name);
        emailMap.put(uid, email);
        profileMap.put(uid, profileUrl);

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

        // -------- NAME --------
        h.tvName.setText(
                nameMap.get(uid) != null
                        ? nameMap.get(uid)
                        : "Loading..."
        );

        // -------- EMAIL --------
        h.tvEmail.setText(
                emailMap.get(uid) != null
                        ? "Email : " + emailMap.get(uid)
                        : ""
        );

        h.tvID.setText("UID : " + uid);

        // -------- PROFILE IMAGE --------
        String imgUrl = profileMap.get(uid);
        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.taklogo)
                    .error(R.drawable.taklogo)
                    .circleCrop()
                    .into(h.imgStudent);
        } else {
            h.imgStudent.setImageResource(R.drawable.taklogo);
        }

        // -------- ACTIONS --------
        h.btnAccept.setOnClickListener(v -> listener.onAccept(uid));
        h.btnRemove.setOnClickListener(v -> listener.onRemove(uid));
    }

    @Override
    public int getItemCount() {
        return uidList.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView imgStudent;
        TextView tvName, tvEmail, tvID;
        MaterialButton btnAccept, btnRemove;

        VH(@NonNull View v) {
            super(v);
            imgStudent = v.findViewById(R.id.imgStudent);
            tvName = v.findViewById(R.id.tvStudentName);
            tvEmail = v.findViewById(R.id.tvStudentEmail);
            tvID = v.findViewById(R.id.tvStudentID);
            btnAccept = v.findViewById(R.id.btnAccept);
            btnRemove = v.findViewById(R.id.btnRemove);
        }
    }
}
