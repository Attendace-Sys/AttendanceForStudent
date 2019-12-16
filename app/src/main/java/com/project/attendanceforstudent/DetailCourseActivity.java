package com.project.attendanceforstudent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.attendanceforstudent.Model.Checking;
import com.project.attendanceforstudent.Model.Course;

import java.util.ArrayList;

public class DetailCourseActivity extends AppCompatActivity {

    ArrayList<Checking> checkingList;
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
        String mName = intent.getStringExtra("iName");
        String mTeacher = intent.getStringExtra("iTeacher");
        String mTime = intent.getStringExtra("iTime");
        String mRoom = intent.getStringExtra("iRoom");


        //actionBar.setTitle("Detail");
        mNameCourseTv.setText(mName);
        mTeacherTv.setText(mTeacher);
        mTimeTv.setText(mTime);
        mRoomTv.setText(mRoom);

        checkingList = new ArrayList<Checking>();

        addChecking();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(DetailCourseActivity.this, MainActivity.class);
                startActivity(intentBack);
            }
        });
    }

    private void addChecking() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        checkingRecyclerView.setLayoutManager(linearLayoutManager);
        checkingRecyclerView.setItemAnimator(new DefaultItemAnimator());

        for (int i = 0; i < 3; i++)
        {
            Checking checking = new Checking();
            checking.setNumberOfWeek(i + 1);
            checking.setDate(dates[i]);
            checking.setIs_absent(ischecking[i]);

            checkingList.add(checking);
        }

        CheckingCardDataAdapter adapter = new CheckingCardDataAdapter(this, checkingList);
        checkingRecyclerView.setAdapter(adapter);
    }
}
