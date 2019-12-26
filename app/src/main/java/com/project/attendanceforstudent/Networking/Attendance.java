
package com.project.attendanceforstudent.Networking;

import com.google.gson.annotations.SerializedName;
import com.project.attendanceforstudent.Model.CheckingCard;

public class Attendance implements Comparable{
    @SerializedName("schedule_code")
    private String mScheduleCode;
    @SerializedName("attendance_code")
    private String mAttendanceCode;
    @SerializedName("schedule_code__schedule_number_of_day")
    private int mScheduleCodeScheduleNumberOfDay;
    @SerializedName("schedule_code__schedule_date")
    private String mScheduleCodeScheduleDate;
    @SerializedName("absent_status")
    private Boolean mAbsentStatus;

    public Attendance(String mScheduleCode, String mAttendanceCode, int mScheduleCodeScheduleNumberOfDay, String mScheduleCodeScheduleDate, Boolean mAbsentStatus) {
        this.mScheduleCode = mScheduleCode;
        this.mAttendanceCode = mAttendanceCode;
        this.mScheduleCodeScheduleNumberOfDay = mScheduleCodeScheduleNumberOfDay;
        this.mScheduleCodeScheduleDate = mScheduleCodeScheduleDate;
        this.mAbsentStatus = mAbsentStatus;
    }

    public String getmScheduleCode() {
        return mScheduleCode;
    }

    public void setmScheduleCode(String mScheduleCode) {
        this.mScheduleCode = mScheduleCode;
    }

    public String getmAttendanceCode() {
        return mAttendanceCode;
    }

    public void setmAttendanceCode(String mAttendanceCode) {
        this.mAttendanceCode = mAttendanceCode;
    }

    public int getmScheduleCodeScheduleNumberOfDay() {
        return mScheduleCodeScheduleNumberOfDay;
    }

    public void setmScheduleCodeScheduleNumberOfDay(int mScheduleCodeScheduleNumberOfDay) {
        this.mScheduleCodeScheduleNumberOfDay = mScheduleCodeScheduleNumberOfDay;
    }

    public String getmScheduleCodeScheduleDate() {
        return mScheduleCodeScheduleDate;
    }

    public void setmScheduleCodeScheduleDate(String mScheduleCodeScheduleDate) {
        this.mScheduleCodeScheduleDate = mScheduleCodeScheduleDate;
    }

    public Boolean getmAbsentStatus() {
        return mAbsentStatus;
    }

    public void setmAbsentStatus(Boolean mAbsentStatus) {
        this.mAbsentStatus = mAbsentStatus;
    }

    @Override
    public int compareTo(Object o) {
        int compareNumWeek = ((Attendance) o).getmScheduleCodeScheduleNumberOfDay();
        return this.getmScheduleCodeScheduleNumberOfDay() - compareNumWeek;
    }
}
