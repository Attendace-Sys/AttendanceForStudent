
package com.project.attendanceforstudent.Networking;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Courses {

    @SerializedName("courses")
    private ArrayList<Course> mCourses;

    public Courses(ArrayList<Course> mCourses) {
        this.mCourses = mCourses;
    }

    public ArrayList<Course> getCourses() {
        return mCourses;
    }

    public void setCourses(ArrayList<Course> courses) {
        mCourses = courses;
    }

}
