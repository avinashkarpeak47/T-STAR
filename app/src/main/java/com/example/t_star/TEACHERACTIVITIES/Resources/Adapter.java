package com.example.t_star.TEACHERACTIVITIES.Resources;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    private Context context;
    private ArrayList<Model> list;
    private String classId;   // ✅ must

    // ✅ CORRECT CONSTRUCTOR
    public Adapter(Context context, ArrayList<Model> list, String classId) {
        this.context = context;
        this.list = list;
        this.classId = classId;
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

        Model model = list.get(position);

        h.tvTitle.setText(model.getTitle());
        h.tvDesc.setText(model.getDescription());
        h.tvDate.setText(model.getDate());

        String url = model.getFileUrl();
        if (url == null) return;

        String lower = url.toLowerCase();

        if (lower.endsWith(".pdf")) {
            h.imgFileType.setImageResource(R.drawable.ic_pdf);
        } else if (lower.matches(".*\\.(jpg|jpeg|png|gif|webp)")) {
            h.imgFileType.setImageResource(R.drawable.ic_image);
        } else {
            h.imgFileType.setImageResource(R.drawable.ic_file_generic);
        }

        // open file
        h.itemView.setOnClickListener(v -> openFile(context, url));

        // delete on long click
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

    // ✅ SAFE DELETE
    private void deleteMaterial(Model model) {

        if (model.getMaterialId() == null || classId == null) {
            Toast.makeText(context, "Invalid delete request", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("StudyMaterial")
                .child(model.getMaterialId())
                .removeValue()
                .addOnSuccessListener(a ->
                        Toast.makeText(context,
                                "Material deleted",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context,
                                "Delete failed",
                                Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= VIEW HOLDER =================
    static class VH extends RecyclerView.ViewHolder {

        ImageView imgFileType;
        TextView tvTitle, tvDesc, tvDate;

        public VH(@NonNull View itemView) {
            super(itemView);
            imgFileType = itemView.findViewById(R.id.imgFileType);
            tvTitle = itemView.findViewById(R.id.txtTitle);
            tvDesc  = itemView.findViewById(R.id.txtDescription);
            tvDate  = itemView.findViewById(R.id.txtMeta);
        }
    }

    // ================= FILE OPEN LOGIC =================
    private void openFile(Context context, String url) {

        Uri uri = Uri.parse(url);
        String mimeType = getMimeType(url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(Intent.createChooser(intent, "Open with"));
        } catch (Exception e) {
            Toast.makeText(context, "No app found", Toast.LENGTH_LONG).show();
        }
    }

    private String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            String mime = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(extension.toLowerCase());
            if (mime != null) return mime;
        }
        if (url.endsWith(".pdf")) return "application/pdf";
        return "*/*";
    }
}
