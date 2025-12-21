package com.example.t_star.TEACHERACTIVITIES.TFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.*;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class THomeFragment extends Fragment {

    private static final String ARG_CLASS_ID = "classId";

    private String classId;
    private String className;

    MaterialToolbar toolbar;
    NavigationView navigationView;
    TextView tvClassID, tvClassName, tvSubject, tvTeacher;

    SearchView searchView;

    GridLayout gridLayout;

    CardView cardCreateAssignment, cardAttendance, cardCreateEvent,
            cardChat, cardStudents, cardResources, cardNotifications;

    // 🔥 ORDER PRESERVED MAP
    private Map<CardView, String> cardMap = new LinkedHashMap<>();
    private List<CardView> originalOrder = new ArrayList<>();

    public THomeFragment() {}

    public static THomeFragment newInstance(String classId) {
        THomeFragment fragment = new THomeFragment();
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

        if (getActivity() != null) {
            className = getActivity().getIntent().getStringExtra("className");
        }

        if (className == null) className = "Class";
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_t_home, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        searchView = view.findViewById(R.id.searchViewGrid);
        gridLayout = view.findViewById(R.id.gridActions);

        cardCreateAssignment = view.findViewById(R.id.cardCreateAssignment);
        cardAttendance       = view.findViewById(R.id.cardAttendance);
        cardCreateEvent      = view.findViewById(R.id.cardCreateEvent);
        cardChat             = view.findViewById(R.id.cardChat);
        cardStudents         = view.findViewById(R.id.cardStudents);
        cardResources        = view.findViewById(R.id.cardResources);
        cardNotifications    = view.findViewById(R.id.cardNotifications);
        tvClassID   = view.findViewById(R.id.tvClassID);
        tvClassName = view.findViewById(R.id.tvClassName);
        tvSubject   = view.findViewById(R.id.tvSubject);
        tvTeacher   = view.findViewById(R.id.tvTeacher);



        // 🔥 MAP WITH KEYWORDS
        cardMap.put(cardCreateAssignment, "assignment");
        cardMap.put(cardAttendance, "attendance");
        cardMap.put(cardCreateEvent, "event");
        cardMap.put(cardChat, "chat");
        cardMap.put(cardStudents, "students");
        cardMap.put(cardResources, "resources");
        cardMap.put(cardNotifications, "notification");

        // 🔥 STORE ORIGINAL ORDER
        originalOrder.addAll(cardMap.keySet());

        setupSearch();
        fetchClassDetails();

        return view;
    }

    private void fetchClassDetails() {

        if (classId == null) return;

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("Classes")
                        .child(classId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("CLASS_DATA", snapshot.getValue().toString());


                if (!snapshot.exists()) return;

                String id      = snapshot.child("classId").getValue(String.class);
                String name    = snapshot.child("className").getValue(String.class);
                String subject = snapshot.child("subject").getValue(String.class);

                tvClassID.setText(id != null ? id : "-");
                tvClassName.setText(name != null ? name : "-");
                tvSubject.setText(subject != null ? subject : "-");
                String teacherId = snapshot.child("teacherId").getValue(String.class);

                if (teacherId != null) {
                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(teacherId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snap) {

                                    String firstName = snap.child("firstname").getValue(String.class);
                                    String lastName  = snap.child("lastname").getValue(String.class);

                                    String fullName = "";

                                    if (firstName != null) fullName += firstName;
                                    if (lastName != null) fullName += " " + lastName;

                                    tvTeacher.setText(
                                            fullName.trim().isEmpty() ? "-" : fullName
                                    );
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    tvTeacher.setText("-");
                                }
                            });
                } else {
                    tvTeacher.setText("-");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Failed to load class data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        navigationView = requireActivity().findViewById(R.id.navigationView);

        // 🔥 Add back arrow click action
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed(); // or requireActivity().finish();
        });

        cardCreateAssignment.setOnClickListener(v -> open(TAsssignmentActivity.class));
        cardAttendance.setOnClickListener(v -> open(TAttendanceActivity.class));
        cardCreateEvent.setOnClickListener(v -> open(TEventActivity.class));
        cardChat.setOnClickListener(v -> open(TChatActivity.class));
        cardStudents.setOnClickListener(v -> open(TStudentsActivity.class));
        cardResources.setOnClickListener(v -> open(TResourcesActivity.class));
        cardNotifications.setOnClickListener(v -> open(TNotificationActivity.class));


    }

    private void open(Class<?> cls) {
        Intent i = new Intent(getActivity(), cls);
        i.putExtra("classId", classId);
        i.putExtra("className", className);
        startActivity(i);
    }

    // ================= SEARCH =================
    private void setupSearch() {

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (getActivity() == null) return;
            TMainActivity.bottomNav.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
        });

        searchView.setOnCloseListener(() -> {
            restoreOriginalOrder();
            TMainActivity.bottomNav.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                reorderGrid(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                reorderGrid(newText);
                return true;
            }
        });
    }

    // 🔥 CORRECT LOGIC
    private void reorderGrid(String text) {

        if (text == null || text.trim().isEmpty()) {
            restoreOriginalOrder();
            return;
        }

        text = text.toLowerCase();

        List<CardView> matched = new ArrayList<>();
        List<CardView> others = new ArrayList<>();

        for (Map.Entry<CardView, String> entry : cardMap.entrySet()) {
            if (entry.getValue().contains(text)) {
                matched.add(entry.getKey());
            } else {
                others.add(entry.getKey());
            }
        }

        gridLayout.removeAllViews();

        // 🔥 ADD MATCHED FIRST
        for (CardView card : matched) {
            gridLayout.addView(card);
        }

        // 🔥 ADD OTHERS AFTER
        for (CardView card : others) {
            gridLayout.addView(card);
        }
    }

    private void restoreOriginalOrder() {
        gridLayout.removeAllViews();
        for (CardView card : originalOrder) {
            gridLayout.addView(card);
        }
    }
}
