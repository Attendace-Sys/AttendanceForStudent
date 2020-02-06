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
