package com.example.t_star.STUDENTACTIVITIES.SFragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.t_star.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class S_Profile_Fragment extends Fragment {

    private CircleImageView ivProfilePic;
    private View headerBg;
    private NestedScrollView profileScroll;

    // For animation calculations
    private float startX, startY;
    private float deltaX, deltaY;
    private boolean positionsReady = false;

    public S_Profile_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_s__profile_, container, false); // use your layout name
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        headerBg = view.findViewById(R.id.header_bg);
        profileScroll = view.findViewById(R.id.profileScroll);

        // 1) Wait until layout is done so we know start position of profile image
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        prepareAnimationPositions(view);
                    }
                }
        );

        // 2) Listen for scroll and animate according to scrollY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            profileScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY,
                                           int oldScrollX, int oldScrollY) {
                    applyAnimation(scrollY);
                }
            });
        } else {
            profileScroll.getViewTreeObserver().addOnScrollChangedListener(
                    () -> applyAnimation(profileScroll.getScrollY())
            );
        }
    }

    private void prepareAnimationPositions(View root) {
        // Starting position: where the view is initially (centered)
        startX = ivProfilePic.getX();
        startY = ivProfilePic.getY();

        // Target (end) position: top-left corner inside the header
        int margin = dpToPx(16);
        float endX = margin;
        float endY = margin;

        // We will move the image using translation, not changing layout params
        deltaX = endX - startX;
        deltaY = endY - startY;

        positionsReady = true;
    }

    private void applyAnimation(int scrollY) {
        if (!positionsReady) return;

        // How many pixels of scroll we use to finish the animation (adjust if you want)
        int maxScrollForAnim = dpToPx(140);

        float progress = scrollY / (float) maxScrollForAnim;
        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        // 1) Move profile picture from center to top-left
        ivProfilePic.setTranslationX(deltaX * progress);
        ivProfilePic.setTranslationY(deltaY * progress);

        // 2) Scale profile picture (make it smaller)
        float startScale = 1.0f;
        float endScale = 0.6f; // 60% of original size
        float currentScale = startScale + (endScale - startScale) * progress;
        ivProfilePic.setScaleX(currentScale);
        ivProfilePic.setScaleY(currentScale);

        // 3) Slightly move header up and fade it a bit (only visual, no layout changes)
        float headerTranslationY = -dpToPx(40) * progress;
        headerBg.setTranslationY(headerTranslationY);

        float startAlpha = 1f;
        float endAlpha = 0.7f;
        float currentAlpha = startAlpha + (endAlpha - startAlpha) * progress;
        headerBg.setAlpha(currentAlpha);
    }

    private int dpToPx(int dp) {
        if (getContext() == null) return dp;
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
