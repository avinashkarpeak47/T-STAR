package com.example.t_star.STUDENTACTIVITIES.Adaptar;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Models.SStudyMaterialModel;

import java.util.ArrayList;

public class SStudyMaterialAdapter
        extends RecyclerView.Adapter<SStudyMaterialAdapter.VH> {

    private Context context;
    private ArrayList<SStudyMaterialModel> list;

    public SStudyMaterialAdapter(Context context,
                                 ArrayList<SStudyMaterialModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.s_item_study_material, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {

        SStudyMaterialModel m = list.get(position);

        h.tvTitle.setText(m.getTitle());
        h.tvDesc.setText(m.getDescription());
        h.tvDate.setText(m.getDate());

        String url = m.getFileUrl();
        if (url == null) return;

        String lower = url.toLowerCase();

        if (lower.endsWith(".pdf"))
            h.imgFileType.setImageResource(R.drawable.ic_pdf);
        else if (lower.matches(".*\\.(jpg|jpeg|png|gif|webp)"))
            h.imgFileType.setImageResource(R.drawable.ic_image);
        else
            h.imgFileType.setImageResource(R.drawable.ic_file_generic);

        h.itemView.setOnClickListener(v -> openFile(url));
        h.itemView.setOnLongClickListener(v -> {
            downloadFile(url);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgFileType;
        TextView tvTitle, tvDesc, tvDate;

        VH(@NonNull View v) {
            super(v);
            imgFileType = v.findViewById(R.id.imgFileType);
            tvTitle = v.findViewById(R.id.txtTitle);
            tvDesc = v.findViewById(R.id.txtDescription);
            tvDate = v.findViewById(R.id.txtMeta);
        }
    }

    // ---------------- OPEN ----------------
    private void openFile(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(url), "*/*");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(
                    context,
                    "No app available to open this file",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // ---------------- DOWNLOAD ----------------
    private void downloadFile(String url) {

        DownloadManager dm =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request req =
                new DownloadManager.Request(Uri.parse(url));

        req.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        req.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "study_material_" + System.currentTimeMillis()
        );

        dm.enqueue(req);

        Toast.makeText(
                context,
                "Downloading file...",
                Toast.LENGTH_SHORT
        ).show();

    }
}