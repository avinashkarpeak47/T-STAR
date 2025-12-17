package com.example.t_star.TEACHERACTIVITIES.TFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;

public class TNotificationFragment extends Fragment {

    // 🔥 ARG KEY
    private static final String ARG_CLASS_ID = "classId";

    private String classId;

    public TNotificationFragment() {
        // Required empty public constructor
    }

    // 🔥 newInstance pattern
    public static TNotificationFragment newInstance(String classId) {
        TNotificationFragment fragment = new TNotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            classId = getArguments().getString(ARG_CLASS_ID);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_t_notification, container, false);

        // Temporary UI (replace with RecyclerView notifications later)
        TextView tv = view.findViewById(R.id.tvNotificationInfo);
        tv.setText("Notifications for Class ID: " + classId);

        return view;
    }
}
