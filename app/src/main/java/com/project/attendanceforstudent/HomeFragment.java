package com.project.attendanceforstudent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.attendanceforstudent.Adapter.CourseCardDataAdapter;
import com.project.attendanceforstudent.Model.CourseCard;
import com.project.attendanceforstudent.Networking.ApiConfig;
import com.project.attendanceforstudent.Networking.AppConfig;
import com.project.attendanceforstudent.Networking.Course;
import com.project.attendanceforstudent.Networking.Courses;

import java.util.ArrayList;
import java.util.Collections;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.attendanceforstudent.Global.token;

public class HomeFragment extends Fragment {

    Courses listCourse;
    RecyclerView courseRecyclerView;

    String nameCourse[] = {"Chuyên đề J2EE - SE324.J23", "Ứng dụng di động - SE102.K12", "Toán cao cấp 1 - MA001.J21"};
    String teachers[] = {"ThS. Trương Hùng", "TS. Cao Thị Vân", "ThS. Vũ Hồng Thúy"};
    String times[] = {"Thứ 2: Tiết 123", "Thứ 3: Tiết 678", "Thứ 7: Tiết 1234"};
    String rooms[] = {"Phòng A11.09", "Phòng B05.12", "Phòng C102"};
    String statuses[] = {"Đang học", "Đang học", "Đang học"};

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        courseRecyclerView = (RecyclerView) view.findViewById(R.id.courses_recyclerView);

        callApi();
//        addCourses();

        return view;
    }

    private void callApi() {
        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);


        Call<Courses> call = getResponse.getListCourse("Token " + token, Global.studentid);
        call.enqueue(new Callback<Courses>() {
            @Override
            public void onResponse(Call<Courses> call, Response<Courses> response) {
                if (response.isSuccessful()) {

                    listCourse = (Courses) response.body();
                    addCourses();

                }
            }

            @Override
            public void onFailure(Call<Courses> call, Throwable t) {

            }
        });
    }

    private void addCourses() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        courseRecyclerView.setLayoutManager(linearLayoutManager);
        courseRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ArrayList<Course> afterSort = listCourse.getCourses();
        Collections.sort(afterSort);

        CourseCardDataAdapter adapter = new CourseCardDataAdapter(getActivity(), afterSort);
        courseRecyclerView.setAdapter(adapter);
    }
}
