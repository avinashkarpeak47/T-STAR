package com.example.t_star.CHATDATA;

import android.content.Context;
import android.content.Intent;
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
import com.example.t_star.TEACHERACTIVITIES.TChatRoomActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TChatListAdapter extends RecyclerView.Adapter<TChatListAdapter.VH> {

    private final Context context;
    private final List<TChatUserModel> list;
    private final String classId;
    private final String myUid; // 🔥 IMPORTANT

    public TChatListAdapter(Context context, List<TChatUserModel> list, String classId) {
        this.context = context;
        this.list = list;
        this.classId = classId;
        this.myUid = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.tstudentchatview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {

        TChatUserModel m = list.get(pos);
        if (m == null) return;

        // ---------------- BASIC INFO ----------------
        h.tvName.setText(m.getName());
        h.tvEmail.setText(m.getEmail());

        /*// ---------------- NO LAST MESSAGE ----------------
        if (TextUtils.isEmpty(m.getLastMessage())) {
            h.tvLastMsg.setText("");
            h.tvTime.setText("");
            h.imgTick.setVisibility(View.GONE);
            h.tvMsgNo.setVisibility(View.GONE);
            return;
        }

        // ---------------- LAST MESSAGE ----------------
        h.tvLastMsg.setText(m.getLastMessage());
        h.tvTime.setText(
                m.getLastTimestamp() > 0
                        ? TimeUtil.format(m.getLastTimestamp())
                        : ""
        );

        // 🔥 CORE LOGIC: WHO SENT LAST MESSAGE
        boolean isLastFromMe = myUid.equals(m.getLastSenderId());

        if (!isLastFromMe) {
            // ================= INCOMING =================
            // ❌ No tick for incoming
            h.imgTick.setVisibility(View.GONE);

            // small left padding (WhatsApp look)
            h.tvLastMsg.setPadding(dp(10), 0, 0, 0);

        } else {
            // ================= OUTGOING =================
            h.imgTick.setVisibility(View.VISIBLE);
            h.tvLastMsg.setPadding(0, 0, 0, 0);

            if (m.isLastSeen()) {
                // ✔✔ seen (green)
                h.imgTick.setImageResource(R.drawable.tdouble_tick);
                h.imgTick.setColorFilter(context.getColor(R.color.green));

            } else if (m.isLastDelivered()) {
                // ✔✔ delivered (grey)
                h.imgTick.setImageResource(R.drawable.tdouble_tick);
                h.imgTick.setColorFilter(context.getColor(R.color.dark_greay));

            } else {
                // ✔ sent
                h.imgTick.setImageResource(R.drawable.tsingle_tick);
                h.imgTick.setColorFilter(context.getColor(R.color.dark_greay));
            }
        }
*/
        // ---------------- NO LAST MESSAGE ----------------
        if (TextUtils.isEmpty(m.getLastMessage())) {
            h.tvTime.setText("");
            h.tvLastMsg.setText("");
            h.imgTick.setVisibility(View.GONE);
            h.tvMsgNo.setVisibility(View.GONE);
        } else {
            // ---------------- LAST MESSAGE ----------------
            h.tvLastMsg.setText(m.getLastMessage());
            h.tvTime.setText(
                    m.getLastTimestamp() > 0 ? TimeUtil.format(m.getLastTimestamp()) : ""
            );

            boolean isLastFromMe = myUid.equals(m.getLastSenderId());
            if (!isLastFromMe) {
                h.imgTick.setVisibility(View.GONE);
                h.tvLastMsg.setPadding(dp(10), 0, 0, 0);
            } else {
                h.imgTick.setVisibility(View.VISIBLE);
                h.tvLastMsg.setPadding(0, 0, 0, 0);

                if (m.isLastSeen()) {
                    h.imgTick.setImageResource(R.drawable.tdouble_tick);
                    h.imgTick.setColorFilter(context.getColor(R.color.green));
                } else if (m.isLastDelivered()) {
                    h.imgTick.setImageResource(R.drawable.tdouble_tick);
                    h.imgTick.setColorFilter(context.getColor(R.color.dark_greay));
                } else {
                    h.imgTick.setImageResource(R.drawable.tsingle_tick);
                    h.imgTick.setColorFilter(context.getColor(R.color.dark_greay));
                }
            }
        }

        // ---------------- UNREAD BADGE ----------------
        if (m.getUnreadCount() > 0) {
            h.tvMsgNo.setVisibility(View.VISIBLE);
            h.tvMsgNo.setText(String.valueOf(m.getUnreadCount()));
        } else {
            h.tvMsgNo.setVisibility(View.GONE);
        }

        // ---------------- OPEN CHAT ----------------
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, TChatRoomActivity.class);
            i.putExtra("classId", classId);
            i.putExtra("studentUid", m.getStudentUid());
            context.startActivity(i);
        });

        if (!TextUtils.isEmpty(m.getProfileImageUrl())) {
            Glide.with(context)
                    .load(m.getProfileImageUrl())
                    .placeholder(R.drawable.taklogo)
                    .error(R.drawable.taklogo)
                    .circleCrop()
                    .into(h.imgStudent);
        } else {
            h.imgStudent.setImageResource(R.drawable.taklogo);
        }


    }

    private int dp(int v) {
        return (int) (v * context.getResources()
                .getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView imgStudent, imgTick;
        TextView tvName, tvEmail, tvLastMsg, tvTime, tvMsgNo;

        VH(View v) {
            super(v);
            imgStudent = v.findViewById(R.id.imgStudent);
            imgTick = v.findViewById(R.id.imgTick);
            tvName = v.findViewById(R.id.tvStudentName);
            tvEmail = v.findViewById(R.id.tvStudentEmail);
            tvLastMsg = v.findViewById(R.id.tvMsg);
            tvTime = v.findViewById(R.id.tvTime);
            tvMsgNo = v.findViewById(R.id.tvMsgNo);
        }
    }
}
