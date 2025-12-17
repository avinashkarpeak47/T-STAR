package com.example.t_star.TEACHERACTIVITIES.TMainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesAdapter;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesModel;
import com.example.t_star.TEACHERACTIVITIES.TCreateClassActivity;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.example.t_star.TEACHERACTIVITIES.TMainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;

public class TClassesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppCompatButton btnCreateClass;
    private SearchView searchView;
    private TextView tvUserName, tvTimestamp;
    private MaterialToolbar toolbar;

    private ArrayList<TClassesModel> classList;
    private ArrayList<TClassesModel> fullClassList;
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

        // ================= TOOLBAR MENU CLICK =================
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof TMainActivity) {
                ((TMainActivity) getActivity()).openDrawer();
            }
        });

        // ================= RECYCLER VIEW =================
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        classList = new ArrayList<>();
        fullClassList = new ArrayList<>();

        adapter = new TClassesAdapter(
                getContext(),
                classList,
                (model, position) -> {
                    Intent i = new Intent(getActivity(), THomeActivity.class);
                    i.putExtra("classId", model.getClassId());
                    i.putExtra("className", model.getClassName());
                    startActivity(i);
                }
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

    @Override
    public void onResume() {
        super.onResume();
        loadClasses();
    }

    // ================= TIMESTAMP =================
    private void setTimestamp() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour >= 4 && hour < 12)
            tvTimestamp.setText("Good Morning ,");
        else if (hour >= 12 && hour < 16)
            tvTimestamp.setText("Good Afternoon ,");
        else if (hour >= 16 && hour < 20)
            tvTimestamp.setText("Good Evening ,");
        else
            tvTimestamp.setText("Good Night ,");
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

                        if (!snapshot.exists()) {
                            tvUserName.setText("Teacher");
                            return;
                        }

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
            if (getActivity() == null) return;

            if (hasFocus) {
                TMainActivity.bottomNav.setVisibility(View.GONE);
            } else {
                TMainActivity.bottomNav.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnCloseListener(() -> {
            TMainActivity.bottomNav.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

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
}
