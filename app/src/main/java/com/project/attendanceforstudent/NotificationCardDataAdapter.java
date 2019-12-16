package com.project.attendanceforstudent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.attendanceforstudent.Model.Course;
import com.project.attendanceforstudent.Model.Notification;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationCardDataAdapter extends RecyclerView.Adapter<NotificationCardDataAdapter.ViewHolder> {
    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationCardDataAdapter(Context context, ArrayList<Notification> list) {
        this.context = context;
        this.notifications = list;
    }

    @Override
    public NotificationCardDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_card, viewGroup, false);
        return new NotificationCardDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationCardDataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(notifications.get(i).getTitle());
        viewHolder.message.setText(notifications.get(i).getMessage());
        if (notifications.get(i).getIs_absent() == false)
        {
            viewHolder.absent.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.notabsent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, message, absent, notabsent;

        CardClickListener cardClickListener;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.noti_title_txt);
            message = (TextView) view.findViewById(R.id.noti_message_txt);
            absent = (TextView) view.findViewById(R.id.is_absent);
            notabsent = (TextView) view.findViewById(R.id.is_not_absent);

        }

    }


}
