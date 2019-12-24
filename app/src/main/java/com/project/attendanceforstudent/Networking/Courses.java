
package com.project.attendanceforstudent.Networking;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Courses {

    @SerializedName("classes")
    private List<Course> mCourses;

    public List<Course> getClasses() {
        return mCourses;
    }

    public void setClasses(List<Course> courses) {
        mCourses = courses;
    }

}
