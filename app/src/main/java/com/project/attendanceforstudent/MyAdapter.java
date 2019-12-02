package com.project.attendanceforstudent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class MyAdapter extends ArrayAdapter<ArrayList<Bitmap>> {
//    String[] names;
    ArrayList<Bitmap> bitmaps;
    Context mContext;

    public MyAdapter(@NonNull Context context,     ArrayList<Bitmap> listBimap) {
        super(context, R.layout.listview_item);
        bitmaps = listBimap;
        mContext = context;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listview_item, parent, false);
            mViewHolder.mFlag = (ImageView) convertView.findViewById(R.id.imageView);
//            mViewHolder.mName = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mFlag.setImageBitmap(bitmaps.get(position));
//        mViewHolder.mName.setText(names[position]);

        return convertView;
    }

    static class ViewHolder {
        ImageView mFlag;
//        TextView mName;
    }
}
