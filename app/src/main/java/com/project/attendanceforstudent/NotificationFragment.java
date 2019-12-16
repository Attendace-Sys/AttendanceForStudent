package com.project.attendanceforstudent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.attendanceforstudent.Model.Course;
import com.project.attendanceforstudent.Model.Notification;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationFragment extends Fragment {

    ArrayList<Notification> notificationList;
    RecyclerView notiRecyclerView;

    String titles[] = {"Chuyên đề J2EE - SE324.J23","Ứng dụng di động - SE102.K12","Toán cao cấp 1 - MA001.J21"};
    String messgages[] = {"Thứ 2: Ngày 01/20/2109", "Vắng quá số buổi qui định", "Thứ 7: Ngày 12/12/2019"};
    Boolean absent[] = {false, true, false};
    public NotificationFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notiRecyclerView = (RecyclerView) view.findViewById(R.id.noti_recyclerView);

        notificationList =  new ArrayList<Notification>();

        addNotifications();
        // Inflate the layout for this fragment
        return view;
    }

    private void addNotifications() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notiRecyclerView.setLayoutManager(linearLayoutManager);
        notiRecyclerView.setItemAnimator(new DefaultItemAnimator());

        for (int i = 0; i < titles.length; i++)
        {
            Notification noti = new Notification();
            noti.setTitle(titles[i]);
            noti.setMessage(messgages[i]);
            noti.setIs_absent(absent[i]);
            notificationList.add(noti);
        }

        NotificationCardDataAdapter adapter = new NotificationCardDataAdapter(getActivity(), notificationList);
        notiRecyclerView.setAdapter(adapter);
    }
}
