package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS.TAssignStudentStatusAdapter;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS.TAssignStudentStatusModel;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class TAssignDetailActivity extends AppCompatActivity {

    TextView tvTitleDetail, tvDescDetail, tvDueDateDetail,
            tvTotalStudents, tvSubmitted, tvUnsubmitted;

    RecyclerView rvStudentStatus;
    ArrayList<TAssignStudentStatusModel> list = new ArrayList<>();
    TAssignStudentStatusAdapter adapter;

    String classId, assignmentId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tassign_detail);

        classId = getIntent().getStringExtra("classId");
        assignmentId = getIntent().getStringExtra("assignmentId");

        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDescDetail = findViewById(R.id.tvDescDetail);
        tvDueDateDetail = findViewById(R.id.tvDueDateDetail);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvSubmitted = findViewById(R.id.tvSubmitted);
        tvUnsubmitted = findViewById(R.id.tvUnsubmitted);

        tvTitleDetail.setText(getIntent().getStringExtra("title"));
        tvDescDetail.setText(getIntent().getStringExtra("description"));
        tvDueDateDetail.setText("Due: " + getIntent().getStringExtra("dueDate"));

        int total = getIntent().getIntExtra("totalStudents", 0);
        int submitted = getIntent().getIntExtra("submittedCount", 0);

        tvTotalStudents.setText("Total: " + total);
        tvSubmitted.setText("Submitted: " + submitted);
        tvUnsubmitted.setText("Pending: " + (total - submitted));

        rvStudentStatus = findViewById(R.id.rvStudentStatus);
        rvStudentStatus.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TAssignStudentStatusAdapter(list);
        rvStudentStatus.setAdapter(adapter);


        loadStatus();
    }

    private void loadStatus() {

        DatabaseReference classRef = FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("students");

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users");

        DatabaseReference subRef = FirebaseDatabase.getInstance()
                .getReference("Submissions")
                .child(classId)
                .child(assignmentId);

        // 🔹 REALTIME LISTENER
        subRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot subSnap) {

                classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot studentSnap) {

                        list.clear();

                        for (DataSnapshot s : studentSnap.getChildren()) {

                            String uid = s.getKey();

                            userRef.child(uid).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot uSnap) {

                                            String name =
                                                    uSnap.child("firstname").getValue(String.class)
                                                            + " " +
                                                            uSnap.child("lastname").getValue(String.class);

                                            boolean submitted = subSnap.hasChild(uid);

                                            String fileUrl = submitted
                                                    ? subSnap.child(uid).child("fileUrl")
                                                    .getValue(String.class)
                                                    : null;

                                            list.add(new TAssignStudentStatusModel(
                                                    uid, name, submitted, fileUrl
                                            ));

                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError e) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError e) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}