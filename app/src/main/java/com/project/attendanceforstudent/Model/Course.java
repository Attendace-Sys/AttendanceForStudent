package com.project.attendanceforstudent.Model;

public class Course {
    String name;
    String teacher;
    String time;
    String room;
    String status;

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }


    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getRoom() {
        return room;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
