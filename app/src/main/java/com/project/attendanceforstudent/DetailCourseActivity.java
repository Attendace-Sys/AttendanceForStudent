package com.project.attendanceforstudent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.attendanceforstudent.Adapter.CheckingCardDataAdapter;
import com.project.attendanceforstudent.Model.CheckingCard;
import com.project.attendanceforstudent.Networking.ApiConfig;
import com.project.attendanceforstudent.Networking.AppConfig;
import com.project.attendanceforstudent.Networking.Attendance;
import com.project.attendanceforstudent.Networking.Attendances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DetailCourseActivity extends AppCompatActivity {

    Attendances checkingList;
    RecyclerView checkingRecyclerView;

    TextView mNameCourseTv, mTeacherTv, mTimeTv, mRoomTv, numPresentTv, numAbsentTv;
    ImageView back;

    String dates[] = {"Thứ 2: Ngày 01/20/2109", "Thứ 3: Ngày 22/11/2019", "Thứ 7: Ngày 12/12/2019"};
    Boolean ischecking[] = {true, false, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);

        //Actionbar
        //ActionBar actionBar = getSupportActionBar();

        mNameCourseTv = (TextView) findViewById(R.id.d_name_course_txt);
        mTeacherTv = (TextView) findViewById(R.id.d_teacher_name_txt);
        mTimeTv = (TextView) findViewById(R.id.d_class_time_txt);
        mRoomTv = (TextView) findViewById(R.id.d_room_txt);
        back = (ImageView) findViewById(R.id.back);
        numPresentTv = (TextView) findViewById(R.id.num_presnt_txt);
        numAbsentTv = (TextView) findViewById(R.id.num_absent_txt);
        checkingRecyclerView = (RecyclerView) findViewById(R.id.checking_recyclerView);


        final Intent intent = getIntent();
        String mCourseId = intent.getStringExtra("courseId");
        String mName = intent.getStringExtra("iName");
        String mTeacher = intent.getStringExtra("iTeacher");
        String mTime = intent.getStringExtra("iTime");
        String mRoom = intent.getStringExtra("iRoom");


        //actionBar.setTitle("Detail");
        mNameCourseTv.setText(mName);
        mTeacherTv.setText(mTeacher);
        mTimeTv.setText(mTime);
        mRoomTv.setText(mRoom);

        callApi(mCourseId);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(DetailCourseActivity.this, MainActivity.class);
                startActivity(intentBack);
            }
        });
    }

    private void callApi(String courseId) {
        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);


        Call<Attendances> call = getResponse.getListAttendanceOfCourse("Token "+ Global.token, courseId , Global.studentid);
        call.enqueue(new Callback<Attendances>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Attendances> call, Response<Attendances> response) {
                if(response.isSuccessful()) {

                    checkingList = (Attendances) response.body();
                    addChecking();
                    setNumPresent();
                }
            }

            @Override
            public void onFailure(Call<Attendances> call, Throwable t) {

            }
        });

    }

    private void addChecking() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        checkingRecyclerView.setLayoutManager(linearLayoutManager);
        checkingRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ArrayList<Attendance> afterSort = (ArrayList<Attendance>) checkingList.getAttendances();
        Collections.sort(afterSort);
        checkingList.setAttendances(afterSort);
        CheckingCardDataAdapter adapter = new CheckingCardDataAdapter(this, afterSort);
        checkingRecyclerView.setAdapter(adapter);
    }

    private void setNumPresent() {
        int sumStudent = checkingList.getAttendances().size();
        int count = 0;
        for ( Attendance item : checkingList.getAttendances()) {
            if (item.getmAbsentStatus() == false)
                count ++;
        }

        int numPresent = sumStudent - count;

        numPresentTv.setText(String.valueOf(numPresent));
        numAbsentTv.setText(String.valueOf(count));

    }
}
