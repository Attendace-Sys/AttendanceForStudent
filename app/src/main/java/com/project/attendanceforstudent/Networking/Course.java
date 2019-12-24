
package com.project.attendanceforstudent.Networking;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Course {

    @SerializedName("course_code")
    private String mCourseCode;
    @SerializedName("course_name")
    private String mCourseName;
    @SerializedName("day_of_week")
    private String mDayOfWeek;
    @SerializedName("end_day")
    private String mEndDay;
    @SerializedName("start_day")
    private String mStartDay;
    @SerializedName("students")
    private List<Object> mStudents;
    @SerializedName("teacher")
    private String mTeacher;
    @SerializedName("time_duration")
    private Long mTimeDuration;
    @SerializedName("time_start_of_course")
    private String mTimeStartOfCourse;

    public String getCourseCode() {
        return mCourseCode;
    }

    public void setCourseCode(String courseCode) {
        mCourseCode = courseCode;
    }

    public String getCourseName() {
        return mCourseName;
    }

    public void setCourseName(String courseName) {
        mCourseName = courseName;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        mDayOfWeek = dayOfWeek;
    }

    public String getEndDay() {
        return mEndDay;
    }

    public void setEndDay(String endDay) {
        mEndDay = endDay;
    }

    public String getStartDay() {
        return mStartDay;
    }

    public void setStartDay(String startDay) {
        mStartDay = startDay;
    }

    public List<Object> getStudents() {
        return mStudents;
    }

    public void setStudents(List<Object> students) {
        mStudents = students;
    }

    public String getTeacher() {
        return mTeacher;
    }

    public void setTeacher(String teacher) {
        mTeacher = teacher;
    }

    public Long getTimeDuration() {
        return mTimeDuration;
    }

    public void setTimeDuration(Long timeDuration) {
        mTimeDuration = timeDuration;
    }

    public String getTimeStartOfCourse() {
        return mTimeStartOfCourse;
    }

    public void setTimeStartOfCourse(String timeStartOfCourse) {
        mTimeStartOfCourse = timeStartOfCourse;
    }

}
