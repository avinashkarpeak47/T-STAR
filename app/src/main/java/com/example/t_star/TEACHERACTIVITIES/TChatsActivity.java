package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.t_star.CHATDATA.*;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.GET_FROM_DATABASE.User_Data_Fetcher;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.Updated_User_Model;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TChatsActivity extends AppCompatActivity {

    RecyclerView rvStudents;
    SearchView searchView;
    TabLayout tabLayout;

    List<TChatUserModel> all = new ArrayList<>();
    List<TChatUserModel> filtered = new ArrayList<>();

    TChatListAdapter adapter;
    String classId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tchats);



        classId = getIntent().getStringExtra("classId");
        if (classId == null) {
            finish();
            return;
        }

        rvStudents = findViewById(R.id.rvStudents);
        searchView = findViewById(R.id.searchViewStudents);
        tabLayout = findViewById(R.id.tabLayoutStudents);

        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TChatListAdapter(this, filtered, classId);
        rvStudents.setAdapter(adapter);

        setupTabs();
        setupSearch();
        loadChats();
    }
    private void loadChats() {
        DatabaseReference chatRef =
                FirebaseDatabase.getInstance().getReference("Chats").child(classId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                all.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    String studentUid = d.getKey();
                    if (studentUid == null) continue;

                    TChatUserModel m = new TChatUserModel();
                    m.setStudentUid(studentUid);
                    m.setLastMessage(d.child("lastMessage").getValue(String.class));
                    m.setLastSenderId(d.child("lastSenderId").getValue(String.class));
                    m.setLastDelivered(Boolean.TRUE.equals(d.child("lastDelivered").getValue(Boolean.class)));
                    m.setLastSeen(Boolean.TRUE.equals(d.child("lastSeen").getValue(Boolean.class)));

                    Long ts = d.child("lastTimestamp").getValue(Long.class);
                    Integer uc = d.child("unread").child("teacher").getValue(Integer.class);

                    m.setLastTimestamp(ts != null ? ts : 0);
                    m.setUnreadCount(uc != null ? uc : 0);

                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(studentUid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot u) {

                                    String fname = u.child("firstname").getValue(String.class);
                                    String lname = u.child("lastname").getValue(String.class);

                                    m.setName(
                                            (fname != null ? fname : "") + " " +
                                                    (lname != null ? lname : "")
                                    );

                                    m.setEmail(u.child("email").getValue(String.class));

                                    // ✅ FETCH PROFILE IMAGE
                                    m.setProfileImageUrl(
                                            u.child("profileImageUrl").getValue(String.class)
                                    );

                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError e) {}
                            });


                    all.add(m);
                }

                // ✅ SORT: latest message first, empty chats last
                Collections.sort(all, (a, b) -> {
                    if (a.getLastTimestamp() == 0 && b.getLastTimestamp() == 0) return 0;
                    if (a.getLastTimestamp() == 0) return 1;
                    if (b.getLastTimestamp() == 0) return -1;
                    return Long.compare(b.getLastTimestamp(), a.getLastTimestamp());
                });

                applyFilters();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }





    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String q) {
                applyFilters();
                return true;
            }

            public boolean onQueryTextChange(String q) {
                applyFilters();
                return true;
            }
        });
    }

    private void setupTabs() {

        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Unread"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                applyFilters();
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void applyFilters() {

        filtered.clear();

        String q = searchView.getQuery().toString().toLowerCase();
        boolean unreadOnly = tabLayout.getSelectedTabPosition() == 1;

        for (TChatUserModel m : all) {

            if (!TextUtils.isEmpty(q)
                    && !m.getName().toLowerCase().contains(q)) continue;

            if (unreadOnly && m.getUnreadCount() <= 0) continue;

            filtered.add(m);
        }

        adapter.notifyDataSetChanged();
    }
}
