package com.example.t_star.TEACHERACTIVITIES.TFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TAsssignmentActivity;
import com.example.t_star.TEACHERACTIVITIES.TAttendanceActivity;
import com.example.t_star.TEACHERACTIVITIES.TChatActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesAssignActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesAttendActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesChatStudActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesEventActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesNotiActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesResourcesActivity;
import com.example.t_star.TEACHERACTIVITIES.TClassesStudentsActivity;
import com.example.t_star.TEACHERACTIVITIES.TEventActivity;
import com.example.t_star.TEACHERACTIVITIES.TNotificationActivity;
import com.example.t_star.TEACHERACTIVITIES.TProfileActivity;
import com.example.t_star.TEACHERACTIVITIES.TResourcesActivity;
import com.example.t_star.TEACHERACTIVITIES.TStudentsActivity;

public class THomeFragment extends Fragment {

    CardView cardCreateAssignment, cardAttendance, cardCreateEvent, cardChat, cardStudents, cardResources, cardNotifications;
    ImageView imgProfile;

    public THomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_t_home, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), TProfileActivity.class))
        );

        // Cards
        cardCreateAssignment = view.findViewById(R.id.cardCreateAssignment);
        cardAttendance = view.findViewById(R.id.cardAttendance);
        cardCreateEvent = view.findViewById(R.id.cardCreateEvent);
        cardChat = view.findViewById(R.id.cardChat);
        cardStudents = view.findViewById(R.id.cardStudents);
        cardResources = view.findViewById(R.id.cardResources);
        cardNotifications = view.findViewById(R.id.cardNotifications);

        cardCreateAssignment.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TClassesAssignActivity.class)));

        cardAttendance.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TClassesAttendActivity.class)));

        cardCreateEvent.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TClassesEventActivity.class)));

        cardChat.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TClassesChatStudActivity.class)));

        cardStudents.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TClassesStudentsActivity.class)));

        cardResources.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TClassesResourcesActivity.class)));

        cardNotifications.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TClassesNotiActivity.class)));

        return view;
    }
}
