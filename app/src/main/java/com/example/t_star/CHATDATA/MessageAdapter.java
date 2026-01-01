package com.example.t_star.CHATDATA;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.ImageViewerActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        /* ---------------- RESET (🔥 VERY IMPORTANT) ---------------- */
        h.layoutReceived.setVisibility(View.GONE);
        h.layoutSent.setVisibility(View.GONE);

        h.tvRecvMsg.setVisibility(View.GONE);
        h.tvSendMsg.setVisibility(View.GONE);

        h.rvImageGridRecv.setVisibility(View.GONE);
        h.rvImageGridSend.setVisibility(View.GONE);

        h.imgReceiveImg.setVisibility(View.GONE);
        h.imgSendImg.setVisibility(View.GONE);

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
        boolean isReceived = !m.getSenderId().equals(myUid);
        boolean hasImages = m.getImageUrls() != null && !m.getImageUrls().isEmpty();

        /* ---------------- IMAGE MESSAGE ---------------- */
        if (hasImages) {

            RecyclerView grid;
            int spanCount = m.getImageUrls().size() == 1 ? 1 : 2;

            if (isReceived) {
                h.layoutReceived.setVisibility(View.VISIBLE);
                h.layoutReceived.setPadding(1, 1, 1, 1);
                grid = h.rvImageGridRecv;
                h.tvRecvTime.setText(TimeUtil.format(m.getTimestamp()));
            } else {
                h.layoutSent.setVisibility(View.VISIBLE);
                h.layoutSent.setPadding(1, 1, 1, 1);
                grid = h.rvImageGridSend;
                h.tvSendTime.setText(TimeUtil.format(m.getTimestamp()));

                // ticks
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

            grid.setVisibility(View.VISIBLE);
            grid.setLayoutManager(new GridLayoutManager(context, spanCount));

            ChatImageGridAdapter imgAdapter =
                    new ChatImageGridAdapter(context, m.getImageUrls(), clickedPos -> {
                        Intent i = new Intent(context, ImageViewerActivity.class);
                        i.putStringArrayListExtra("images",
                                new ArrayList<>(m.getImageUrls()));
                        i.putExtra("start", clickedPos);
                        context.startActivity(i);
                    });

            grid.setAdapter(imgAdapter);
            return;
        }

        /* ---------------- TEXT MESSAGE ---------------- */
        if (isReceived) {
            h.layoutReceived.setVisibility(View.VISIBLE);
            h.layoutReceived.setPadding(15, 6, 15, 6);
            h.tvRecvMsg.setVisibility(View.VISIBLE);
            h.tvRecvMsg.setText(m.getText());
            h.tvRecvTime.setText(TimeUtil.format(m.getTimestamp()));
        } else {
            h.layoutSent.setVisibility(View.VISIBLE);
            h.layoutSent.setPadding(15, 6, 15, 6);
            h.tvSendMsg.setVisibility(View.VISIBLE);
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
        LinearLayout layoutReceived, layoutSent, pdfRecv, pdfSend;
        TextView tvRecvMsg, tvRecvTime, tvSendMsg, tvSendTime,tvDateHeader;
        ImageView imgTick,imgSendImg,imgReceiveImg;
        RecyclerView rvImageGridSend, rvImageGridRecv;


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
            imgSendImg = v.findViewById(R.id.imgSendImg);
            imgReceiveImg = v.findViewById(R.id.imgReceiveImg);
            rvImageGridSend = v.findViewById(R.id.rvImageGridSent);
            rvImageGridRecv = v.findViewById(R.id.rvImageGridRecv);
            pdfRecv = v.findViewById(R.id.layoutPdfReceived);
            pdfSend = v.findViewById(R.id.layoutPdfSend);
//            tvPdfRecv = v.findViewById(R.id.tvPdfName);
//            tvPdfSend = v.findViewById(R.id.tvPdfName);

        }
    }


/*
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


        if (m.getImageUrls() != null && !m.getImageUrls().isEmpty()) {

            RecyclerView grid;

            boolean isReceived = !m.getSenderId().equals(myUid);

            if (isReceived) {
                // ---------- RECEIVED IMAGE ----------
                h.layoutReceived.setVisibility(View.VISIBLE);
                h.layoutSent.setVisibility(View.GONE);

                h.tvRecvMsg.setVisibility(View.GONE);
                h.imgReceiveImg.setVisibility(View.GONE);

                grid = h.rvImageGridRecv;
                h.tvRecvTime.setText(TimeUtil.format(m.getTimestamp()));
            } else {
                // ---------- SENT IMAGE ----------
                h.layoutReceived.setVisibility(View.GONE);
                h.layoutSent.setVisibility(View.VISIBLE);

                h.tvSendMsg.setVisibility(View.GONE);
                h.imgSendImg.setVisibility(View.GONE);

                grid = h.rvImageGridSend;
                h.tvSendTime.setText(TimeUtil.format(m.getTimestamp()));

                // ticks
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

            grid.setVisibility(View.VISIBLE);

            int imageCount = m.getImageUrls().size();

            // 🔥 IMPORTANT FIX
            if (imageCount == 1) {
                // ✅ SINGLE IMAGE → FULL WIDTH (no grid feel)
                grid.setLayoutManager(new GridLayoutManager(context, 1));
            } else {
                // ✅ MULTIPLE IMAGES → GRID
                grid.setLayoutManager(new GridLayoutManager(context, 2));
            }

            ChatImageGridAdapter imgAdapter =
                    new ChatImageGridAdapter(context, m.getImageUrls(), clickedPos -> {
                        Intent i = new Intent(context, ImageViewerActivity.class);
                        i.putStringArrayListExtra(
                                "images",
                                new ArrayList<>(m.getImageUrls())
                        );
                        i.putExtra("start", clickedPos);
                        context.startActivity(i);
                    });

            grid.setAdapter(imgAdapter);

        }else {

            // ---------- TEXT MESSAGE ----------
            h.imgSendImg.setVisibility(View.GONE);
            h.imgReceiveImg.setVisibility(View.GONE);

            if (!m.getSenderId().equals(myUid)) {
                h.layoutReceived.setVisibility(View.VISIBLE);
                h.layoutSent.setVisibility(View.GONE);
                h.tvRecvMsg.setVisibility(View.VISIBLE);
                h.tvRecvMsg.setText(m.getText());
                h.tvRecvTime.setText(TimeUtil.format(m.getTimestamp()));
            } else {
                h.layoutReceived.setVisibility(View.GONE);
                h.layoutSent.setVisibility(View.VISIBLE);
                h.tvSendMsg.setVisibility(View.VISIBLE);
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

    }


 */


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
