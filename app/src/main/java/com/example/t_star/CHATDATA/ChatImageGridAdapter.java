package com.example.t_star.CHATDATA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.t_star.R;

import java.util.List;

public class ChatImageGridAdapter
        extends RecyclerView.Adapter<ChatImageGridAdapter.VH> {

    private final Context context;
    private final List<String> images;
    private final OnImageClick listener;

    public interface OnImageClick {
        void onClick(int position);
    }

    public ChatImageGridAdapter(Context c, List<String> imgs, OnImageClick l) {
        context = c;
        images = imgs;
        listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.chat_image_item, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Glide.with(context)
                .load(images.get(pos))
                .centerCrop()
                .into(h.img);

        h.itemView.setOnClickListener(v -> listener.onClick(pos));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        VH(View v) {
            super(v);
            img = v.findViewById(R.id.img);
        }
    }
}
