package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Plain data model that matches the Realtime DB structure under "Users/{uid}".
 * Fields are public (simple POJO) so Firebase can map them automatically.
 */
@IgnoreExtraProperties
public class Updated_User_Model {

    public String uid;
    public String firstname;
    public String lastname;
    public String email;
    public String mobile;
    public String role;
    public String rollno;
    public String profileImageUrl;

    // Default constructor required for calls to DataSnapshot.getValue(Updated_User_Model.class)
    public Updated_User_Model() { }

    public Updated_User_Model(String uid, String firstname, String lastname,
                              String email, String mobile, String role,
                              String rollno, String profileImageUrl) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.rollno = rollno;
        this.profileImageUrl = profileImageUrl;
    }

    // Convenience: full name getter
    public String getFullName() {
        String f = firstname != null ? firstname : "";
        String l = lastname != null ? lastname : "";
        String combined = (f + " " + l).trim();
        return combined.isEmpty() ? "" : combined;
    }
}

