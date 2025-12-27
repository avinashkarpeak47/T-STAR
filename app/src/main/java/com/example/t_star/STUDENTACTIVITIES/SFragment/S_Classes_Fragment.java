package com.example.t_star.STUDENTACTIVITIES.SFragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.GET_FROM_DATABASE.User_Data_Fetcher;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.Updated_User_Model;
import com.example.t_star.STUDENTACTIVITIES.S_Class_Dashboard_Activity;
import com.example.t_star.STUDENTACTIVITIES.S_Enroll_in_Class;
import com.example.t_star.STUDENTACTIVITIES.S_Fragment_Classes_RC_Adapter;
import com.example.t_star.STUDENTACTIVITIES.S_Fragment_Classes_RC_Model;
import com.example.t_star.TSTAR_Entry_Activity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class S_Classes_Fragment extends Fragment {

    //Comment

    // UI
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView imgUserProfile, drawerImgProfile;
    TextView tvGreeting, tvUserName, drawerUserName, drawerUserEmail;

    RecyclerView recyclerView;
   // ProgressBar progressBar;
    AppCompatButton btnEnrollClass;
    TextView tvEmptyState;

    // Data
    ArrayList<S_Fragment_Classes_RC_Model> classList = new ArrayList<>();
    S_Fragment_Classes_RC_Adapter adapter;

    String uid;
    String profileUrl = "";

    public S_Classes_Fragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_s__classes_, container, false);

        uid = FirebaseAuth.getInstance().getUid();

        // Drawer
        drawerLayout = view.findViewById(R.id.s_drawerLayout);
        navigationView = view.findViewById(R.id.s_navigationView);

        View header = navigationView.getHeaderView(0);
        drawerImgProfile = header.findViewById(R.id.imgProfile);
        drawerUserName = header.findViewById(R.id.tvTeacherName);
        drawerUserEmail = header.findViewById(R.id.tvTeacherEmail);

        // Top UI
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        imgUserProfile = view.findViewById(R.id.ivUserProfile);

        recyclerView = view.findViewById(R.id.recyclerViewClasses);
        //progressBar = view.findViewById(R.id.progressBarClasses);
        btnEnrollClass = view.findViewById(R.id.btnEnrollNewClass);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new S_Fragment_Classes_RC_Adapter(
                getContext(),
                classList,
                model -> {

                    if (!"approved".equals(model.getStatus())) {
                        Toast.makeText(getContext(),"Class request not approved yet",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    markApprovedAsSeen(model);

                    Intent i = new Intent(getContext(), S_Class_Dashboard_Activity.class);
                    i.putExtra("classId", model.getClassId());
                    startActivity(i);
                }
        );

        recyclerView.setAdapter(adapter);

        setGreetingMessage();
        loadStudentProfile();   // 🔥 profile + drawer data fetch
        loadEnrolledClasses();  // 🔥 classes fetch

        btnEnrollClass.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), S_Enroll_in_Class.class))
        );

        setupDrawerMenuActions();

        return view;
    }

    // ---------- Greeting ----------
    private void setGreetingMessage() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 12)
            tvGreeting.setText("Good Morning ☀");
        else if (hour >= 12 && hour < 16)
            tvGreeting.setText("Good Afternoon \uD83C\uDF24");
        else if (hour >= 16 && hour < 20)
            tvGreeting.setText("Good Evening \uD83C\uDF06");
        else
            tvGreeting.setText("Good Night \uD83C\uDF19");
    }



    // ---------- Load Profile + Drawer Header ----------
    private void loadStudentProfile() {

        if (uid == null) return;

        User_Data_Fetcher.getUserData(uid, new User_Data_Fetcher.UserDataCallback() {
            @Override
            public void onSuccess(Updated_User_Model user) {

                tvUserName.setText(user.getFullName());
                drawerUserName.setText(user.getFullName());
                drawerUserEmail.setText(user.email);

                profileUrl = user.profileImageUrl;

                if(profileUrl != null && !profileUrl.isEmpty()) {

                    Glide.with(requireContext())
                            .load(profileUrl)
                            .circleCrop()
                            .placeholder(R.drawable.baseline_person_24)
                            .into(imgUserProfile);

                    Glide.with(requireContext())
                            .load(profileUrl)
                            .circleCrop()
                            .into(drawerImgProfile);

                } else {
                    imgUserProfile.setImageResource(R.drawable.baseline_person_24);
                    drawerImgProfile.setImageResource(R.drawable.baseline_person_24);
                }
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }

    // ---------- Load Enrolled Classes ----------
    private void loadEnrolledClasses() {

       // progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        classList.clear();
        adapter.notifyDataSetChanged();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("StudentClasses")
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    showEmpty();
                    return;
                }

                int total = (int) snapshot.getChildrenCount();
                final int[] count = {0};

                for (DataSnapshot s : snapshot.getChildren()) {

                    String classId = s.getKey();
                    String className = s.child("className").getValue(String.class);
                    String teacherId = s.child("teacherId").getValue(String.class);
                    String status = s.child("status").getValue(String.class);
                    boolean seenApproved = Boolean.TRUE.equals(
                            s.child("seenApproved").getValue(Boolean.class)
                    );

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(teacherId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snap) {

                                    String teacherName = "Teacher";
                                    if (snap.exists()) {
                                        teacherName =
                                                snap.child("firstname").getValue(String.class)
                                                        + " " +
                                                        snap.child("lastname").getValue(String.class);
                                    }

                                    classList.add(new S_Fragment_Classes_RC_Model(
                                            classId,
                                            className,
                                            teacherName,
                                            status,
                                            seenApproved
                                    ));

                                    adapter.notifyDataSetChanged();

                                    count[0]++;
                                    if(count[0] == total) finalizeUI();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    count[0]++;
                                    if(count[0] == total) finalizeUI();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmpty();
            }
        });
    }

    private void finalizeUI() {
       // progressBar.setVisibility(View.GONE);

        if(classList.isEmpty()) showEmpty();
        else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void showEmpty() {
       // progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }

    private void markApprovedAsSeen(S_Fragment_Classes_RC_Model m) {
        if (!"approved".equals(m.getStatus()) || m.isSeenApproved()) return;

        FirebaseDatabase.getInstance()
                .getReference("StudentClasses")
                .child(uid)
                .child(m.getClassId())
                .child("seenApproved")
                .setValue(true);
    }

    // ---------- Drawer Menu ----------
    private void setupDrawerMenuActions() {

        // ================= DRAWER MENU =================
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_editprofile) {

                // Tell BottomNavigation to switch to Profile tab
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                        requireActivity().findViewById(R.id.s_bottom_nav);

                bottomNav.setSelectedItemId(R.id.nav_s_profile);
            } else if (id == R.id.nav_setting) {

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                startActivity(intent);
            } else if (id == R.id.nav_contactus) {

                View dialogView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_contact_support, null);

                EditText etSubject = dialogView.findViewById(R.id.etSubject);
                EditText etMessage = dialogView.findViewById(R.id.etMessage);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Contact Support")
                        .setView(dialogView)
                        .setPositiveButton("Send", (d, w) -> {


                            String subject = etSubject.getText().toString().trim();
                            String message = etMessage.getText().toString().trim();

                            // 1️⃣ Save to Firebase
                            saveContactMessageToFirebase(subject, message);
// 2️⃣ Open Gmail with same values (fixed email)
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("message/rfc822");

                            emailIntent.putExtra(Intent.EXTRA_EMAIL,
                                    new String[]{"cpeproject2025@gmail.com"});   // Fixed To email
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // Dialog subject
                            emailIntent.putExtra(Intent.EXTRA_TEXT, message);    // Dialog message

// Force Gmail only
                            emailIntent.setPackage("com.google.android.gm");

                            try {
                                startActivity(emailIntent);
                            } catch (Exception e) {
                                // Fallback if Gmail not installed
                                startActivity(Intent.createChooser(emailIntent, "Send via Email"));
                            }


                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }


            // ---------- SHARE APP ----------
            else if (id == R.id.nav_share) {


                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hey! I'm using T-STAR – a smart app for students with AI assistant, classes, and learning tools.\n\n" +
                                "Download: https://play.google.com/store/apps/details?id="
                                + requireContext().getPackageName()
                );

                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } else if (id == R.id.nav_logout) {
                showLogoutDialog();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void showLogoutDialog() {

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {

                    FirebaseAuth.getInstance().signOut();

                    Intent i = new Intent(getActivity(), TSTAR_Entry_Activity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveContactMessageToFirebase( String subject, String message) {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ContactUs/Feedback")
                .push();

        String id = ref.getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("userId", uid);
        map.put("role", "Teacher");
        map.put("subject", subject);
        map.put("message", message);
        map.put("timestamp", System.currentTimeMillis());

        ref.setValue(map);
    }


}