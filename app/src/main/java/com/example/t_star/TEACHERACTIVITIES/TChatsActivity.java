package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.CHATDATA.*;
import com.example.t_star.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;

import java.util.ArrayList;
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

                    TChatUserModel model = new TChatUserModel();
                    model.setStudentUid(studentUid);

                    model.setLastMessage(d.child("lastMessage").getValue(String.class));
                    Long ts = d.child("lastTimestamp").getValue(Long.class);
                    Integer uc = d.child("unreadCount").getValue(Integer.class);

                    if (ts != null) model.setLastTimestamp(ts);
                    if (uc != null) model.setUnreadCount(uc);

                    // 🔥 FETCH STUDENT NAME & EMAIL
                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(studentUid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot u) {
                                    model.setName(
                                            u.child("firstname").getValue(String.class) + " " +
                                                    u.child("lastname").getValue(String.class)
                                    );
                                    model.setEmail(u.child("email").getValue(String.class));
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });

                    all.add(model);
                }
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
