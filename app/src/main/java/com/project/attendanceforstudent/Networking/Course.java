
package com.project.attendanceforstudent.Networking;

import com.google.gson.annotations.SerializedName;

public class Course implements Comparable{

    @SerializedName("course_code")
    private String mCourseCode;
    @SerializedName("course_name")
    private String mCourseName;
    @SerializedName("course_room")
    private String mCourseRoom;
    @SerializedName("day_of_week")
    private int mDayOfWeek;
    @SerializedName("end_day")
    private String mEndDay;
    @SerializedName("start_day")
    private String mStartDay;
    @SerializedName("teacher")
    private String mTeacher;
    @SerializedName("teacher__first_name")
    private String mTeacherFirstName;
    @SerializedName("time_duration")
    private int mTimeDuration;
    @SerializedName("time_start_of_course")
    private int mTimeStartOfCourse;

    public Course(String mCourseCode, String mCourseName, String mCourseRoom, int mDayOfWeek, String mEndDay, String mStartDay, String mTeacher, String mTeacherFirstName, int mTimeDuration, int mTimeStartOfCourse) {
        this.mCourseCode = mCourseCode;
        this.mCourseName = mCourseName;
        this.mCourseRoom = mCourseRoom;
        this.mDayOfWeek = mDayOfWeek;
        this.mEndDay = mEndDay;
        this.mStartDay = mStartDay;
        this.mTeacher = mTeacher;
        this.mTeacherFirstName = mTeacherFirstName;
        this.mTimeDuration = mTimeDuration;
        this.mTimeStartOfCourse = mTimeStartOfCourse;
    }

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

    public String getCourseRoom() {
        return mCourseRoom;
    }

    public void setCourseRoom(String courseRoom) {
        mCourseRoom = courseRoom;
    }

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
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

    public String getTeacher() {
        return mTeacher;
    }

    public void setTeacher(String teacher) {
        mTeacher = teacher;
    }

    public String getTeacherFirstName() {
        return mTeacherFirstName;
    }

    public void setTeacherFirstName(String teacherFirstName) {
        mTeacherFirstName = teacherFirstName;
    }

    public int getTimeDuration() {
        return mTimeDuration;
    }

    public void setTimeDuration(int timeDuration) {
        mTimeDuration = timeDuration;
    }

    public int getTimeStartOfCourse() {
        return mTimeStartOfCourse;
    }

    public void setTimeStartOfCourse(int timeStartOfCourse) {
        mTimeStartOfCourse = timeStartOfCourse;
    }

    @Override
    public int compareTo(Object o) {
        int compare = ((Course) o).getDayOfWeek();
        return this.getDayOfWeek() - compare;
    }
}
