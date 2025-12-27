package com.example.t_star.TEACHERACTIVITIES.TMainFragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesAdapter;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesModel;
import com.example.t_star.TEACHERACTIVITIES.TCreateClassActivity;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.example.t_star.TSTAR_Entry_Activity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;

public class TClassesFragment extends Fragment {

    // ================= UI =================
    private RecyclerView recyclerView;
    private AppCompatButton btnCreateClass;
    private SearchView searchView;
    private TextView tvUserName, tvTimestamp;
    private MaterialToolbar toolbar;

    // Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // ================= DATA =================
    private ArrayList<TClassesModel> classList = new ArrayList<>();
    private ArrayList<TClassesModel> fullClassList = new ArrayList<>();
    private TClassesAdapter adapter;

    private String currentTeacherId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_t_classes, container, false);

        // ================= FIND VIEWS =================
        recyclerView   = view.findViewById(R.id.recyclerViewClasses);
        btnCreateClass = view.findViewById(R.id.btnEnrollNewClass);
        searchView     = view.findViewById(R.id.searchViewGrid);
        tvUserName     = view.findViewById(R.id.tvUserName);
        tvTimestamp    = view.findViewById(R.id.tvtimestamp);
        toolbar        = view.findViewById(R.id.toolbar);

        drawerLayout   = view.findViewById(R.id.drawerLayout);
        navigationView = view.findViewById(R.id.navigationView);

        // ================= TOOLBAR → OPEN DRAWER =================
        toolbar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        // ================= DRAWER MENU =================
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

           if (id == R.id.nav_editprofile) {

               // Tell BottomNavigation to switch to Profile tab
               com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                       requireActivity().findViewById(R.id.bottomNav);

               bottomNav.setSelectedItemId(R.id.nav_tprofile);
            }
            else if (id == R.id.nav_contactus) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:cpeproject2025@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "T-STAR Support");

                startActivity(Intent.createChooser(emailIntent, "Contact Support"));

            }

            // ---------- SHARE APP ----------
            else if (id == R.id.nav_share) {


               Intent shareIntent = new Intent(Intent.ACTION_SEND);
               shareIntent.setType("text/plain");
               shareIntent.putExtra(
                       Intent.EXTRA_TEXT,
                       "Try T-STAR – Smart Learning App for Students!\n\n" +
                               "Download: https://play.google.com/store/apps/details?id="
                               + requireContext().getPackageName()
               );

               startActivity(Intent.createChooser(shareIntent, "Share via"));
           }

           else if (id == R.id.nav_logout) {
               showLogoutDialog();
           }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        View headerView = navigationView.getHeaderView(0);

        TextView tvDrawerName  = headerView.findViewById(R.id.tvTeacherName);
        TextView tvDrawerEmail = headerView.findViewById(R.id.tvTeacherEmail);

        loadDrawerTeacherInfo(tvDrawerName, tvDrawerEmail);

        // ================= RECYCLER VIEW =================
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TClassesAdapter(
                getContext(),
                classList,
                (model, position) -> {
                    Intent i = new Intent(getActivity(), THomeActivity.class);
                    i.putExtra("classId", model.getClassId());
                    i.putExtra("className", model.getClassName());
                    startActivity(i);
                },
                (model, position) -> showDeleteClassDialog(model, position)
        );


        recyclerView.setAdapter(adapter);

        // ================= CREATE CLASS =================
        btnCreateClass.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TCreateClassActivity.class))
        );

        // ================= INIT =================
        setTimestamp();
        loadTeacherName();
        setupSearch();

        return view;
    }

    private void showDeleteClassDialog(TClassesModel model, int position) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Class")
                .setMessage(
                        "This class will be removed from:\n" +
                                "• Teacher panel\n" +
                                "• Student accounts\n" +
                                "• Attendance\n" +
                                "• Assignments\n\n" +
                                "Are you sure?"
                )
                .setPositiveButton("Delete", (d, w) -> {

                    Classes_helper.deleteClassEverywhere(
                            model.getClassId(),
                            new Classes_helper.DatabaseCallback() {

                                @Override
                                public void onSuccess() {

                                    classList.remove(position);
                                    fullClassList.remove(model);
                                    adapter.notifyItemRemoved(position);

                                    Toast.makeText(
                                            getContext(),
                                            "Class deleted everywhere",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(
                                            getContext(),
                                            "Delete failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void loadDrawerTeacherInfo(TextView tvName, TextView tvEmail) {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String firstName = snapshot.child("firstname").getValue(String.class);
                        String lastName  = snapshot.child("lastname").getValue(String.class);
                        String email     = snapshot.child("email").getValue(String.class);

                        String profileImageUrl =
                                snapshot.child("profileImageUrl").getValue(String.class);

                        // ---------- TOP BAR PROFILE IMAGE ----------
                        ImageView ivTopProfile =
                                getView().findViewById(R.id.ivUserProfile);

                        if (ivTopProfile != null &&
                                profileImageUrl != null &&
                                !profileImageUrl.isEmpty()) {

                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.baseline_person_24)
                                    .circleCrop()
                                    .into(ivTopProfile);
                        }

                        // ---------- DRAWER NAME ----------
                        if (firstName != null) {
                            tvName.setText(firstName + " " + (lastName != null ? lastName : ""));
                        } else {
                            tvName.setText("Teacher");
                        }

                        // ---------- DRAWER EMAIL ----------
                        if (email != null) {
                            tvEmail.setText(email);
                        } else {
                            tvEmail.setText("");
                        }

                        // ---------- DRAWER HEADER PROFILE IMAGE ----------
                        ImageView ivDrawerProfile =
                                navigationView.getHeaderView(0)
                                        .findViewById(R.id.imgProfile);

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.baseline_person_24)
                                    .error(R.drawable.baseline_person_24)
                                    .circleCrop()
                                    .into(ivDrawerProfile);
                        } else {
                            ivDrawerProfile.setImageResource(R.drawable.baseline_person_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }




    @Override
    public void onResume() {
        super.onResume();
        loadClasses();
    }

    // ================= TIMESTAMP =================
    private void setTimestamp() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour >= 4 && hour < 12)
            tvTimestamp.setText("Good Morning ☀️");
        else if (hour >= 12 && hour < 16)
            tvTimestamp.setText("Good Afternoon 🌤️");
        else if (hour >= 16 && hour < 20)
            tvTimestamp.setText("Good Evening \uD83C\uDF06");
        else
            tvTimestamp.setText("Good Night 🌙");
    }

    // ================= TEACHER NAME =================
    private void loadTeacherName() {

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String first = snapshot.child("firstname").getValue(String.class);
                        String last  = snapshot.child("lastname").getValue(String.class);

                        if (first != null) {
                            tvUserName.setText(first + " " + (last != null ? last : ""));
                        } else {
                            tvUserName.setText("Teacher");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // ================= SEARCH =================
    private void setupSearch() {

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                com.example.t_star.TEACHERACTIVITIES.TMainActivity.bottomNav
                        .setVisibility(View.GONE);
            } else {
                com.example.t_star.TEACHERACTIVITIES.TMainActivity.bottomNav
                        .setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnCloseListener(() -> {
            com.example.t_star.TEACHERACTIVITIES.TMainActivity.bottomNav
                    .setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<TClassesModel> filtered = new ArrayList<>();

                for (TClassesModel model : fullClassList) {
                    if (model.getClassName() != null &&
                            model.getClassName().toLowerCase()
                                    .contains(newText.toLowerCase())) {
                        filtered.add(model);
                    }
                }

                classList.clear();
                classList.addAll(filtered);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    // ================= LOAD CLASSES =================
    private void loadClasses() {

        currentTeacherId = FirebaseAuth.getInstance().getUid();
        if (currentTeacherId == null) return;

        Classes_helper.getAllClasses(list -> {

            classList.clear();
            fullClassList.clear();

            for (TClassesModel model : list) {
                if (currentTeacherId.equals(model.getTeacherId())) {
                    classList.add(model);
                    fullClassList.add(model);
                }
            }

            adapter.notifyDataSetChanged();
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

}
