package com.project.attendanceforstudent.Model;

public class CourseCard {
    String id;
    String name;
    String teacherId;
    String teacher;
    String time;
    String room;
    String status;

    public CourseCard(){

    }
    public CourseCard(String id, String name, String teacherId , String teacher, String time, String room, String status) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
        this.teacher = teacher;
        this.time = time;
        this.room = room;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

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
