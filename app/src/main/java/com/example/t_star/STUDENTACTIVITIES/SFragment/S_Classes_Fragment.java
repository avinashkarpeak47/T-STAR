package com.example.t_star.STUDENTACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.Splash_Screen;

/*import com.bumptech.glide.Glide;
import com.example.t_star.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class S_Classes_Fragment extends Fragment {

    // UI Elements
    private TextView tvGreeting, tvUserName;
    private ImageView imgUserProfile;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AppCompatButton btnEnrollClass;

    // Firestore
    //private FirebaseFirestore db;
    //private FirebaseAuth auth;

    // RecyclerView
    private List<S_Fragment_Classes_RC_Model> classList;
    private S_Fragment_Classes_RC_Adapter adapter;

    public S_Classes_Fragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_s__classes_, container, false);

        // Initialize UI
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        imgUserProfile = view.findViewById(R.id.ivUserProfile);

        recyclerView = view.findViewById(R.id.recyclerViewClasses);
        progressBar = view.findViewById(R.id.progressBarClasses);
        btnEnrollClass = view.findViewById(R.id.btnEnrollNewClass);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //db = FirebaseFirestore.getInstance();
        //auth = FirebaseAuth.getInstance();

        classList = new ArrayList<>();

        adapter = new S_Fragment_Classes_RC_Adapter(
                getContext(),
                classList,
                model -> Toast.makeText(getContext(),
                        "Clicked: " + model.getClassName(), Toast.LENGTH_SHORT).show()
        );

        recyclerView.setAdapter(adapter);

        setGreetingMessage();
        //loadUserDetails();
        //loadClassesFromFirestore();

        btnEnrollClass.setOnClickListener(v -> enrollNewClass());

        return view;
    }

    // -----------------------------------------------------------------------
    // GREETINGS LOGIC
    // -----------------------------------------------------------------------
    private void setGreetingMessage() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        String greeting;

        if (hour >= 4 && hour < 12)
            greeting = "Good Morning";
        else if (hour >= 12 && hour < 16)
            greeting = "Good Afternoon";
        else if (hour >= 16 && hour < 20)
            greeting = "Good Evening";
        else
            greeting = "Good Night";

        tvGreeting.setText(greeting);
    }

    // -----------------------------------------------------------------------
    // USER DETAILS FETCHING (NAME + PROFILE IMAGE)
    // -----------------------------------------------------------------------
   /* private void loadUserDetails() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("USERS")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String profileURL = doc.getString("profilePic");

                        tvUserName.setText(name != null ? name : "Student");

                        if (profileURL != null && !profileURL.isEmpty()) {
                            Glide.with(this)
                                    .load(profileURL)
                                    .placeholder(R.drawable.s_profile)
                                    .into(imgUserProfile);
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "User load error", Toast.LENGTH_SHORT).show();
                });
    }

    // -----------------------------------------------------------------------
    // LOAD CLASSES LIST FROM FIRESTORE
    // -----------------------------------------------------------------------
    private void loadClassesFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        classList.clear();

        db.collection("STUDENT_CLASSES")
                .get()
                .addOnSuccessListener(query -> {

                    for (QueryDocumentSnapshot doc : query) {
                        S_Fragment_Classes_RC_Model model = doc.toObject(S_Fragment_Classes_RC_Model.class);
                        classList.add(model);
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }*/

    // -----------------------------------------------------------------------
    // ENROLL NEW CLASS  (DEMO ONLY)
    // -----------------------------------------------------------------------
    private void enrollNewClass() {
        Intent intent = new Intent(getActivity(), S_Enroll_in_Class.class);
        startActivity(intent);
        Toast.makeText(getContext(), "Enroll New Class clicked", Toast.LENGTH_SHORT).show();
    }
}
