package com.project.attendanceforstudent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.attendanceforstudent.Model.Checking;
import com.project.attendanceforstudent.Model.Course;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckingCardDataAdapter extends RecyclerView.Adapter<CheckingCardDataAdapter.ViewHolder>{
    private ArrayList<Checking> checkingList;
    private Context context;

    public CheckingCardDataAdapter(Context context, ArrayList<Checking> list) {
        this.context = context;
        this.checkingList = list;
    }

    @Override
    public CheckingCardDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.checking_card, viewGroup, false);
        return new CheckingCardDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckingCardDataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.week.setText("#" + checkingList.get(i).getNumberOfWeek());
        viewHolder.date.setText(checkingList.get(i).getDate());
        if (checkingList.get(i).isIs_absent() == false)
        {
            viewHolder.is_absent.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.is_not_absent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return checkingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView week, date;
        LinearLayout is_absent, is_not_absent;
        CardClickListener cardClickListener;

        public ViewHolder(View view) {
            super(view);
            week = (TextView) view.findViewById(R.id.number_week_txt);
            date = (TextView) view.findViewById(R.id.date_txt);
            is_absent = (LinearLayout) view.findViewById(R.id.join_is_true);
            is_not_absent = (LinearLayout) view.findViewById(R.id.absent_is_true);
        }

    }
}
