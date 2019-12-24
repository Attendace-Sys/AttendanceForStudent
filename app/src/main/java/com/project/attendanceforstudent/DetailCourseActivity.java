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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DetailCourseActivity extends AppCompatActivity {

    ArrayList<CheckingCard> checkingList;
    RecyclerView checkingRecyclerView;

    TextView mNameCourseTv, mTeacherTv, mTimeTv, mRoomTv;
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

        checkingList = new ArrayList<CheckingCard>();

        //callApi();

        addChecking();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(DetailCourseActivity.this, MainActivity.class);
                startActivity(intentBack);
            }
        });


    }

    private void callApi() {
        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);


//        Call<Schedules> call = getResponse.getListSchedule("Token "+ Global.token, mId);
//        call.enqueue(new Callback<Schedules>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onResponse(Call<Schedules> call, Response<Schedules> response) {
//                if(response.isSuccessful()) {
//
//                    Schedules schedules = (Schedules) response.body();
//                    checkingList = convertClassesFromCourses(schedules);
//                    addChecking();
//
//                }
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            private ArrayList<CheckingCard> convertClassesFromCourses(Schedules schedules) {
//
//                ArrayList<CheckingCard> list = new ArrayList<>();
//
//                for ( Schedule item : schedules.getSchedule()) {
//
//                    CheckingCard schedule = convertClassToCourse(item);
//                    list.add(schedule);
//                }
//
//                return list;
//            }
//
//
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            private CheckingCard convertClassToCourse(Schedule item) {
//                String scheduldeCode = item.getScheduleCode();
//                String attendanceId = item.getAttendanceCode();
//                int numberOfWeek = Math.toIntExact(item.getScheduleNumberOfDay());
//                String date = item.getScheduleDate();
//                boolean is_absent = item.getAbsentStatus();
//
//                CheckingCard checking = new CheckingCard( scheduldeCode, attendanceId, numberOfWeek, date, is_absent);
//                return checking;
//            }
//
//            @Override
//            public void onFailure(Call<Schedules> call, Throwable t) {
//
//            }
//        });

    }

    private void addChecking() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        checkingRecyclerView.setLayoutManager(linearLayoutManager);
        checkingRecyclerView.setItemAnimator(new DefaultItemAnimator());

        for (int i = 0; i < 3; i++)
        {
            CheckingCard checking = new CheckingCard();
            checking.setNumberOfWeek(2 - i);
            checking.setDate(dates[i]);
            checking.setIs_absent(ischecking[i]);

            checkingList.add(checking);
        }

        Collections.sort(checkingList);

        CheckingCardDataAdapter adapter = new CheckingCardDataAdapter(this, checkingList);
        checkingRecyclerView.setAdapter(adapter);
    }
}
