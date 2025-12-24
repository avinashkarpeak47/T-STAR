package com.example.t_star.CHATDATA;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.t_star.R;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    private final Context context;
    private final List<MessageModel> list;
    private final String myUid;

    public MessageAdapter(Context context, List<MessageModel> list, String myUid) {
        this.context = context;
        this.list = list;
        this.myUid = myUid;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tchatmsgview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        MessageModel m = list.get(pos);
        if (m == null) return;

        // ================= RECEIVED =================
        if (!m.getSenderId().equals(myUid)) {
            h.layoutReceived.setVisibility(View.VISIBLE);
            h.layoutSent.setVisibility(View.GONE);
            h.tvRecvMsg.setText(m.getText());
            h.tvRecvTime.setText(TimeUtil.format(m.getTimestamp()));
            return;
        }

        // ================= SENT =================
        h.layoutReceived.setVisibility(View.GONE);
        h.layoutSent.setVisibility(View.VISIBLE);
        h.tvSendMsg.setText(m.getText());
        h.tvSendTime.setText(TimeUtil.format(m.getTimestamp()));

        if (!m.isDelivered()) {
            h.imgTick.setImageResource(R.drawable.tsingle_tick);
            h.imgTick.setColorFilter(context.getColor(R.color.dark_greay));
        } else if (!m.isSeen()) {
            h.imgTick.setImageResource(R.drawable.tdouble_tick);
            h.imgTick.setColorFilter(context.getColor(R.color.dark_greay));
        } else {
            h.imgTick.setImageResource(R.drawable.tdouble_tick);
            h.imgTick.setColorFilter(context.getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout layoutReceived, layoutSent;
        TextView tvRecvMsg, tvRecvTime, tvSendMsg, tvSendTime;
        ImageView imgTick;

        VH(View v) {
            super(v);
            layoutReceived = v.findViewById(R.id.layoutReceived);
            layoutSent = v.findViewById(R.id.layoutSent);
            tvRecvMsg = v.findViewById(R.id.tvRecvMsg);
            tvRecvTime = v.findViewById(R.id.tvRecvTime);
            tvSendMsg = v.findViewById(R.id.tvSendMsg);
            tvSendTime = v.findViewById(R.id.tvSendTime);
            imgTick = v.findViewById(R.id.imgTick);
        }
    }
}
