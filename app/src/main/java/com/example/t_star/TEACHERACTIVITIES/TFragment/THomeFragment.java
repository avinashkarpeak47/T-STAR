package com.example.t_star.TEACHERACTIVITIES.TFragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TClassesAssignActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesAttendActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesChatStudActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesEventActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesNotiActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesResourcesActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesStudentsActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class THomeFragment extends Fragment {

    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // Cards
    CardView cardCreateAssignment, cardAttendance, cardCreateEvent, cardChat, cardStudents, cardResources, cardNotifications;

    public THomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_t_home, container, false);

        // ===================== FIND VIEWS =====================
        toolbar = view.findViewById(R.id.toolbar);

        // DrawerLayout & NavigationView present in your Activity
        drawerLayout = requireActivity().findViewById(R.id.drawerLayout);
        navigationView = requireActivity().findViewById(R.id.navigationView);

        // ===================== ENABLE TOOLBAR MENU CLICK TO OPEN DRAWER =====================
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // ===================== CARD CLICKS =====================
        cardCreateAssignment = view.findViewById(R.id.cardCreateAssignment);
        cardAttendance = view.findViewById(R.id.cardAttendance);
        cardCreateEvent = view.findViewById(R.id.cardCreateEvent);
        cardChat = view.findViewById(R.id.cardChat);
        cardStudents = view.findViewById(R.id.cardStudents);
        cardResources = view.findViewById(R.id.cardResources);
        cardNotifications = view.findViewById(R.id.cardNotifications);

        // 👉 Add your activity navigation
        cardCreateAssignment.setOnClickListener(v -> startActivity(new Intent(getActivity(), TClassesAssignActivity.class)));
        cardAttendance.setOnClickListener(v -> startActivity(new Intent(getActivity(), TClassesAttendActivity.class)));
        cardCreateEvent.setOnClickListener(v -> startActivity(new Intent(getActivity(), TClassesEventActivity.class)));
        cardChat.setOnClickListener(v -> startActivity(new Intent(getActivity(), TClassesChatStudActivity.class)));
        cardStudents.setOnClickListener(v -> startActivity(new Intent(getActivity(), TClassesStudentsActivity.class)));
        cardResources.setOnClickListener(v -> startActivity(new Intent(getActivity(), TClassesResourcesActivity.class)));
        cardNotifications.setOnClickListener(v -> startActivity(new Intent(getActivity(), TClassesNotiActivity.class)));

        return view;
    }
}
