package com.example.t_star.STUDENTACTIVITIES.Adaptar;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Models.StudyMaterialModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StudyMaterialAdapter extends RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StudyMaterialModel> materialList;
    private ArrayList<StudyMaterialModel> fullList;

    public StudyMaterialAdapter(Context context, ArrayList<StudyMaterialModel> materialList) {
        this.context = context;
        this.materialList = materialList;
        this.fullList = new ArrayList<>(materialList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.s_item_study_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StudyMaterialModel model = materialList.get(position);

        holder.txtTitle.setText(model.getTitle());
        holder.txtDescription.setText(model.getDescription());

        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(new Date(model.getUploadedOn()));
        holder.txtMeta.setText("Uploaded on: " + date);

        setFileIcon(holder.imgFileType, model.getFileType());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(model.getFileUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    public void filter(String query) {
        materialList.clear();

        if (query.isEmpty()) {
            materialList.addAll(fullList);
        } else {
            query = query.toLowerCase();
            for (StudyMaterialModel model : fullList) {
                if (model.getTitle().toLowerCase().contains(query)) {
                    materialList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void setFileIcon(ImageView imageView, String fileType) {

        if (fileType == null) {
            imageView.setImageResource(R.drawable.ic_file_generic);
            return;
        }

        switch (fileType.toLowerCase()) {
            case "pdf":
                imageView.setImageResource(R.drawable.ic_pdf);
                break;
            case "ppt":
            case "pptx":
                imageView.setImageResource(R.drawable.ic_ppt);
                break;
            case "doc":
            case "docx":
                imageView.setImageResource(R.drawable.ic_doc);
                break;
            case "xls":
            case "xlsx":
                imageView.setImageResource(R.drawable.ic_excel);
                break;
            case "jpg":
            case "jpeg":
            case "png":
                imageView.setImageResource(R.drawable.ic_image);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_file_generic);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFileType;
        TextView txtTitle, txtDescription, txtMeta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFileType = itemView.findViewById(R.id.imgFileType);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtMeta = itemView.findViewById(R.id.txtMeta);
        }
    }
}

