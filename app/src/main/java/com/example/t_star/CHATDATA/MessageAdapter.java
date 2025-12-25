package com.example.t_star.CHATDATA;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.t_star.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // -------- DATE HEADER LOGIC --------
        if (pos == 0) {
            h.tvDateHeader.setVisibility(View.VISIBLE);
            h.tvDateHeader.setText(getDateLabel(m.getTimestamp()));
        } else {
            MessageModel prev = list.get(pos - 1);
            if (!isSameDay(prev.getTimestamp(), m.getTimestamp())) {
                h.tvDateHeader.setVisibility(View.VISIBLE);
                h.tvDateHeader.setText(getDateLabel(m.getTimestamp()));
            } else {
                h.tvDateHeader.setVisibility(View.GONE);
            }
        }

        // -------- NORMAL MESSAGE LOGIC --------
        if (!m.getSenderId().equals(myUid)) {
            // received
            h.layoutReceived.setVisibility(View.VISIBLE);
            h.layoutSent.setVisibility(View.GONE);
            h.tvRecvMsg.setText(m.getText());
            h.tvRecvTime.setText(TimeUtil.format(m.getTimestamp()));
        } else {
            // sent
            h.layoutReceived.setVisibility(View.GONE);
            h.layoutSent.setVisibility(View.VISIBLE);
            h.tvSendMsg.setText(m.getText());
            h.tvSendTime.setText(TimeUtil.format(m.getTimestamp()));

            if (!m.isDelivered()) {
                h.imgTick.setImageResource(R.drawable.tsingle_tick);
                h.imgTick.setColorFilter(Color.GRAY);
            } else if (!m.isSeen()) {
                h.imgTick.setImageResource(R.drawable.tdouble_tick);
                h.imgTick.setColorFilter(Color.GRAY);
            } else {
                h.imgTick.setImageResource(R.drawable.tdouble_tick);
                h.imgTick.setColorFilter(Color.GREEN);
            }
        }
    }
    private String getDateLabel(long timestamp) {
        Calendar msgCal = Calendar.getInstance();
        msgCal.setTimeInMillis(timestamp);

        Calendar today = Calendar.getInstance();

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        if (isSameDay(msgCal.getTimeInMillis(), today.getTimeInMillis()))
            return "Today";

        if (isSameDay(msgCal.getTimeInMillis(), yesterday.getTimeInMillis()))
            return "Yesterday";

        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(new Date(timestamp));
    }

    private boolean isSameDay(long t1, long t2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(t1);
        c2.setTimeInMillis(t2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    static class VH extends RecyclerView.ViewHolder {
        LinearLayout layoutReceived, layoutSent;
        TextView tvRecvMsg, tvRecvTime, tvSendMsg, tvSendTime,tvDateHeader;
        ImageView imgTick;

        VH(View v) {
            super(v);
            tvDateHeader = v.findViewById(R.id.tvDateHeader);
            layoutReceived = v.findViewById(R.id.layoutReceived);
            layoutSent = v.findViewById(R.id.layoutSent);
            tvRecvMsg = v.findViewById(R.id.tvRecvMsg);
            tvRecvTime = v.findViewById(R.id.tvRecvTime);
            tvSendMsg = v.findViewById(R.id.tvSendMsg);
            tvSendTime = v.findViewById(R.id.tvSendTime);
            imgTick = v.findViewById(R.id.imgTick);
        }
    }

    /*@Override
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
    }*/


}
