package com.example.t_star.STUDENTACTIVITIES;

/**
 * Model class for storing class details for students.
 * Matches the XML fields: className, subjectName.
 */
public class S_Fragment_Classes_RC_Model {

    private String className;
    private String subjectName;

    // Required empty constructor for Firestore
    public S_Fragment_Classes_RC_Model() {}

    public S_Fragment_Classes_RC_Model(String className, String subjectName) {
        this.className = className;
        this.subjectName = subjectName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
