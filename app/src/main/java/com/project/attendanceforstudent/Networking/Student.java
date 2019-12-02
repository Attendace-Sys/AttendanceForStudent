package com.project.attendanceforstudent.Networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.lang.reflect.Field;

public class Student {

    @SerializedName("student_code")
//    @Expose
    private String studentId;
    @SerializedName("student_name")
//    @Expose
    private String studentName;
    @SerializedName("student_email")
//    @Expose
    private String studentEmail;
    @SerializedName("student_video_data")
//    @Expose
    private File studentVideoData;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public File getStudentVideoData() {
        return studentVideoData;
    }

    public void setStudentVideoData(File studentVideoData) {
        this.studentVideoData = studentVideoData;
    }

}
