package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses;


/**
 * Simple POJO representing user data saved under /Users/{uid}.
 * Note: Do NOT store raw passwords here.
 */
public class User_Authentication_Model {
    public String uid;
    public String firstname;
    public String lastname;
    public String email;
    public String mobile;
    public String role;

    // Required no-arg constructor for Firebase deserialization
    public User_Authentication_Model() {}

    public User_Authentication_Model(String uid, String firstname, String lastname,
                     String email, String mobile, String role) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
    }
}
