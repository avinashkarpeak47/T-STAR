package com.example.t_star.CHATDATA;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TChatRoomActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class TChatListAdapter extends RecyclerView.Adapter<TChatListAdapter.VH> {

    private final Context context;
    private final List<TChatUserModel> list;
    private final String classId;
    private final String myUid;

    public TChatListAdapter(Context context, List<TChatUserModel> list, String classId) {
        this.context = context;
        this.list = list;
        this.classId = classId;
        this.myUid = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tstudentchatview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TChatUserModel m = list.get(position);
        if (m == null) return;

        h.tvName.setText(m.getName());
        h.tvEmail.setText(m.getEmail());
        h.tvLastMsg.setText(m.getLastMessage());

        // ================= UNREAD BADGE =================
        if (m.getUnreadCount() > 0) {
            h.tvMsgNo.setVisibility(View.VISIBLE);
            h.tvMsgNo.setText(String.valueOf(m.getUnreadCount()));
        } else {
            h.tvMsgNo.setVisibility(View.GONE);
        }

        // ================= TICK LOGIC =================
        h.imgTick.setVisibility(View.GONE);

        boolean lastSentByMe = myUid.equals(m.getLastSenderId());

        if (lastSentByMe && m.getUnreadCount() == 0) {
            h.imgTick.setVisibility(View.VISIBLE);

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

        // ================= TIME =================
        h.tvTime.setText(m.getLastTimestamp() > 0
                ? TimeUtil.format(m.getLastTimestamp()) : "");

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, TChatRoomActivity.class);
            i.putExtra("classId", classId);
            i.putExtra("studentUid", m.getStudentUid());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgStudent, imgTick;
        TextView tvName, tvEmail, tvLastMsg, tvTime, tvMsgNo;

        VH(@NonNull View itemView) {
            super(itemView);
            imgStudent = itemView.findViewById(R.id.imgStudent);
            imgTick = itemView.findViewById(R.id.imgTick);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvLastMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMsgNo = itemView.findViewById(R.id.tvMsgNo);
        }
    }
}
