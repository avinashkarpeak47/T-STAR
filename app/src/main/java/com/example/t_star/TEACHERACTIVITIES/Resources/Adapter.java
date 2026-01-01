package com.example.t_star.TEACHERACTIVITIES.Resources;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.VH> {

    private final Context context;
    private final String classId;

    private final ArrayList<Model> fullList = new ArrayList<>();     // master list
    private final ArrayList<Model> filteredList = new ArrayList<>(); // visible list

    public Adapter(Context context, ArrayList<Model> list, String classId) {
        this.context = context;
        this.classId = classId;

        fullList.addAll(list);
        filteredList.addAll(list);
    }

    // 👉 Firebase new data update
    public void setData(ArrayList<Model> newList) {

        fullList.clear();
        fullList.addAll(newList);

        filteredList.clear();
        filteredList.addAll(newList);

        notifyDataSetChanged();
    }

    // 👉 Search filter
    public void filter(String query) {

        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);   // reset full list
        } else {
            query = query.toLowerCase();

            for (Model m : fullList) {
                String t = m.getTitle() == null ? "" : m.getTitle().toLowerCase();
                String d = m.getDescription() == null ? "" : m.getDescription().toLowerCase();

                if (t.contains(query) || d.contains(query)) {
                    filteredList.add(m);
                }
            }
        }

        notifyDataSetChanged();
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

        Model model = filteredList.get(position);

        h.tvTitle.setText(model.getTitle());
        h.tvDesc.setText(model.getDescription());
        h.tvDate.setText(model.getDate());

        String url = model.getFileUrl();
        if (url != null) {
            String lower = url.toLowerCase();

            if (lower.endsWith(".pdf")) h.imgFileType.setImageResource(R.drawable.ic_pdf);
            else if (lower.matches(".*\\.(jpg|jpeg|png|gif|webp)"))
                h.imgFileType.setImageResource(R.drawable.ic_image);
            else h.imgFileType.setImageResource(R.drawable.ic_file_generic);
        }

        h.itemView.setOnClickListener(v -> openFile(context, model.getFileUrl()));

        h.itemView.setOnLongClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Delete material?")
                    .setMessage("This will remove the file for all students.")
                    .setPositiveButton("Delete", (d, w) -> deleteMaterial(model))
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // ================= DELETE =================
    private void deleteMaterial(Model model) {

        if (model.getMaterialId() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("StudyMaterial")
                .child(classId)
                .child("Resources")
                .child(model.getMaterialId())
                .removeValue()
                .addOnSuccessListener(a ->
                        Toast.makeText(context, "Material deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show());
    }

    // ================= VIEW HOLDER =================
    static class VH extends RecyclerView.ViewHolder {

        ImageView imgFileType;
        TextView tvTitle, tvDesc, tvDate;

        VH(@NonNull View itemView) {
            super(itemView);
            imgFileType = itemView.findViewById(R.id.imgFileType);
            tvTitle = itemView.findViewById(R.id.txtTitle);
            tvDesc  = itemView.findViewById(R.id.txtDescription);
            tvDate  = itemView.findViewById(R.id.txtMeta);
        }
    }

    // ============== DOWNLOAD + OPEN FILE ==============
    private void openFile(Context context, String url) {
        if (url == null) return;

        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(url), "*/*");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(i);

        } catch (Exception ex) {

            DownloadManager dm = (DownloadManager)
                    context.getSystemService(Context.DOWNLOAD_SERVICE);

            DownloadManager.Request req =
                    new DownloadManager.Request(Uri.parse(url));

            req.setTitle("Downloading file...");
            req.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            req.setDestinationInExternalFilesDir(
                    context,
                    Environment.DIRECTORY_DOWNLOADS,
                    "study_material"
            );

            dm.enqueue(req);

            Toast.makeText(context,
                    "Downloading... Open from notification",
                    Toast.LENGTH_LONG).show();
        }
    }
}
