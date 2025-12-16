package com.example.t_star.STUDENTACTIVITIES.SFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.S_Enroll_in_Class;
import com.example.t_star.STUDENTACTIVITIES.S_Fragment_Classes_RC_Adapter;
import com.example.t_star.STUDENTACTIVITIES.S_Fragment_Classes_RC_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;

public class S_Classes_Fragment extends Fragment {

    private TextView tvGreeting, tvUserName;
    private ImageView imgUserProfile;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AppCompatButton btnEnrollClass;

    private ArrayList<S_Fragment_Classes_RC_Model> classList = new ArrayList<>();
    private S_Fragment_Classes_RC_Adapter adapter;

    public S_Classes_Fragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_s__classes_, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        imgUserProfile = view.findViewById(R.id.ivUserProfile);
        recyclerView = view.findViewById(R.id.recyclerViewClasses);
        progressBar = view.findViewById(R.id.progressBarClasses);
        btnEnrollClass = view.findViewById(R.id.btnEnrollNewClass);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new S_Fragment_Classes_RC_Adapter(getContext(), classList, model -> {});
        recyclerView.setAdapter(adapter);

        setGreetingMessage();
        loadEnrolledClasses();

        btnEnrollClass.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), S_Enroll_in_Class.class))
        );

        return view;
    }

    // Greeting message
    private void setGreetingMessage() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 12)
            tvGreeting.setText("Good Morning");
        else if (hour >= 12 && hour < 16)
            tvGreeting.setText("Good Afternoon");
        else if (hour >= 16 && hour < 20)
            tvGreeting.setText("Good Evening");
        else
            tvGreeting.setText("Good Night");
    }

    // Load classes student is enrolled in
    private void loadEnrolledClasses() {

        progressBar.setVisibility(View.VISIBLE);
        classList.clear();
        adapter.notifyDataSetChanged();

        String studentUid = FirebaseAuth.getInstance().getUid();
        if (studentUid == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Classes");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot classSnap : snapshot.getChildren()) {

                    String classId = classSnap.getKey();
                    if (classId == null) continue;

                    // Check student enrolled?
                    if (classSnap.child("students").hasChild(studentUid)) {

                        String className = classSnap.child("className").getValue(String.class);
                        String subjectName = classSnap.child("subjectName").getValue(String.class);
                        String teacherId = classSnap.child("teacherId").getValue(String.class);

                        if (teacherId == null) teacherId = "";

                        // Fetch Teacher Name
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(teacherId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap) {

                                        String teacherName = "Teacher";
                                        if (snap.exists()) {
                                            String fname = snap.child("firstname").getValue(String.class);
                                            String lname = snap.child("lastname").getValue(String.class);
                                            teacherName = fname + " " + lname;
                                        }

                                        classList.add(new S_Fragment_Classes_RC_Model(
                                                classId,
                                                className,
                                                teacherName
                                        ));

                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                    }
                }

                progressBar.setVisibility(View.GONE);

                if (classList.isEmpty()) {
                    Toast.makeText(getContext(), "You are not enrolled in any class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
