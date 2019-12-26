
package com.project.attendanceforstudent.Networking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Attendances {

    @SerializedName("attendances")
    private ArrayList<Attendance> mAttendances;

    public Attendances(ArrayList<Attendance> mAttendances)
    {
        //Collections.sort(mAttendances);

        this.mAttendances = mAttendances;
    }

    public ArrayList<Attendance> getAttendances() {
        return mAttendances;
    }

    public void setAttendances(ArrayList<Attendance> attendances) {
        mAttendances = attendances;
    }

}
