package com.project.attendanceforstudent.Model;

public class Notification {
    String title;
    String message;
    Boolean is_absent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIs_absent() {
        return is_absent;
    }

    public void setIs_absent(Boolean is_absent) {
        this.is_absent = is_absent;
    }


}
