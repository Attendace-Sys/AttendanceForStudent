package com.project.attendanceforstudent.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.attendanceforstudent.Interface.CardClickListener;
import com.project.attendanceforstudent.DetailCourseActivity;
import com.project.attendanceforstudent.Model.CourseCard;
import com.project.attendanceforstudent.Networking.Course;
import com.project.attendanceforstudent.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseCardDataAdapter extends RecyclerView.Adapter<CourseCardDataAdapter.ViewHolder>
{

    private ArrayList<Course> courses;
    private Context context;

    public CourseCardDataAdapter(Context context, ArrayList<Course> listCourse) {
        this.context = context;
        this.courses = listCourse;
    }

    @Override
    public CourseCardDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_card, viewGroup, false);
        return new CourseCardDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int timeEnd = courses.get(i).getTimeStartOfCourse() + courses.get(i).getTimeDuration();
        final String mtime = "Thứ " + courses.get(i).getDayOfWeek() + ": Tiết " + courses.get(i).getTimeStartOfCourse() + " đến tiết " + timeEnd;

        viewHolder.name.setText(courses.get(i).getCourseName());
        viewHolder.teacher.setText(courses.get(i).getTeacherFirstName());
        viewHolder.time.setText(mtime);
        viewHolder.room.setText(courses.get(i).getCourseRoom());
//        viewHolder.status.setText(courses.get(i).getStatus());

        /*Use when you want to view detail on each card click*/
        viewHolder.setCardClickListener(new CardClickListener() {
            @Override
            public void onCardClick(View v, int pos) {
                String id = courses.get(pos).getCourseCode();
                String name = courses.get(pos).getCourseName();
                String teacher = courses.get(pos).getTeacherFirstName();
                String time = mtime;
                String room = courses.get(pos).getCourseRoom();

                Intent intent = new Intent(context, DetailCourseActivity.class);
                intent.putExtra("courseId", id);
                intent.putExtra("iName", name);
                intent.putExtra("iTeacher", teacher);
                intent.putExtra("iTime", time);
                intent.putExtra("iRoom", room);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, teacher, time, room, status;
        CardClickListener cardClickListener;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name_course_txt);
            teacher = (TextView) view.findViewById(R.id.teacher_name_txt);
            time = (TextView) view.findViewById(R.id.class_time_txt);
            room = (TextView) view.findViewById(R.id.room_txt);
//            status = (TextView) view.findViewById(R.id.status_txt);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.cardClickListener.onCardClick(v, getAdapterPosition());
        }

        public void setCardClickListener(CardClickListener cardClick)
        {
            this.cardClickListener = cardClick;
        }
    }
}
