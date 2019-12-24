package com.project.attendanceforstudent.Model;

public class CheckingCard implements Comparable {
    String scheduldeCode;
    String attendanceId;
    int numberOfWeek;
    String date;
    boolean is_absent;

    public CheckingCard(){

    }
    public CheckingCard(String scheduldeCode, String attendanceId, int numberOfWeek, String date, boolean is_absent) {
        this.scheduldeCode = scheduldeCode;
        this.attendanceId = attendanceId;
        this.numberOfWeek = numberOfWeek;
        this.date = date;
        this.is_absent = is_absent;
    }

    public String getScheduldeCode() {
        return scheduldeCode;
    }

    public void setScheduldeCode(String scheduldeCode) {
        this.scheduldeCode = scheduldeCode;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getNumberOfWeek() {
        return numberOfWeek;
    }

    public void setNumberOfWeek(int numberOfWeek) {
        this.numberOfWeek = numberOfWeek;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isIs_absent() {
        return is_absent;
    }

    public void setIs_absent(boolean is_absent) {
        this.is_absent = is_absent;
    }

    @Override
    public int compareTo(Object o) {
        int compareNumWeek = ((CheckingCard) o).getNumberOfWeek();
        return this.numberOfWeek - compareNumWeek;
    }
}
