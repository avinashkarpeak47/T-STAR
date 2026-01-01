package com.example.t_star.TEACHERACTIVITIES;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.t_star.CHATDATA.ImagePagerAdapter;
import com.example.t_star.R;

import java.util.ArrayList;

public class ImageViewerActivity extends AppCompatActivity {

    ViewPager2 pager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_image_viewer);

        ArrayList<String> imgs =
                getIntent().getStringArrayListExtra("images");

        if (imgs == null || imgs.isEmpty()) {
            finish();
            return;
        }

        int start = getIntent().getIntExtra("start", 0);

        pager = findViewById(R.id.pager);
        pager.setOrientation(ViewPager2.ORIENTATION_VERTICAL); // 🔥 THIS LINE
        pager.setAdapter(new ImagePagerAdapter(this, imgs));
        pager.setCurrentItem(start, false);
        pager.setOffscreenPageLimit(1);



    }
}
