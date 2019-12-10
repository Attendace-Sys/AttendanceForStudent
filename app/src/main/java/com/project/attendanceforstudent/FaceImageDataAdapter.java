package com.project.attendanceforstudent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class FaceImageDataAdapter extends RecyclerView.Adapter<FaceImageDataAdapter.ViewHolder> {
    private ArrayList<Bitmap> imageBitmaps;
    private Context context;

    public FaceImageDataAdapter(Context context, ArrayList<Bitmap> imageBitmaps) {
        this.context = context;
        this.imageBitmaps = imageBitmaps;

    }

    @Override
    public FaceImageDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_grid_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.img.setImageBitmap(imageBitmaps.get(i));
        final int pos = i;
        viewHolder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(pos);
            }
        });
    }

    public void removeItem(int position) {
        this.imageBitmaps.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageButton btnClose;
        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
            btnClose = view.findViewById(R.id.closeButton);
        }
    }
}