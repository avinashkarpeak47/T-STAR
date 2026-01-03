package com.example.t_star.CHATDATA;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChatImagePreviewAdapter
        extends RecyclerView.Adapter<ChatImagePreviewAdapter.VH> {

    private final Context context;
    private final List<Uri> images;

    public ChatImagePreviewAdapter(Context c, List<Uri> imgs) {
        context = c;
        images = imgs;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        ImageView img = new ImageView(context);
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setBackgroundColor(Color.BLACK);
        return new VH(img);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Glide.with(context).load(images.get(pos)).into(h.img);
    }

    @Override
    public int getItemCount() { return images.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        VH(View v) { super(v); img = (ImageView) v; }
    }
}

