package com.project.attendanceforstudent;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

import androidx.annotation.NonNull;

public class ExtractImageTask extends AsyncTask<Bitmap, Void, Bitmap> {

    ProgressDialog p;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        p = new ProgressDialog(DetectVideoActivity.this);
//        p.setMessage("Please wait...It is downloading");
//        p.setIndeterminate(false);
//        p.setCancelable(false);
//        p.show();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {
        final Bitmap[] outImage = new Bitmap[1];

        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmaps[0]);
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .build();
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        Task<List<FirebaseVisionFace>> listTask = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> faces) {
                        outImage[0] = processFaceResult(faces, image);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
        return outImage[0];
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // Download is done
    }

    private Bitmap processFaceResult(List<FirebaseVisionFace> faces, FirebaseVisionImage image) {
        Bitmap result;

//        for(FirebaseVisionFace face: faces)
//        {
        Rect bounds = faces.get(0).getBoundingBox();

        result = cropBitmap(image.getBitmap(), bounds);

        result = Bitmap.createScaledBitmap(result,160, 160, true);

//            listBitmap.add(bitmap);
//
//            temp++;
//        }
        return result;
        // Toast.makeText(this,String.format(""+temp), Toast.LENGTH_SHORT).show();
    }

    public Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        Bitmap ret = Bitmap.createBitmap(w, h, bitmap.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, -rect.left, -rect.top, null);
        return ret;
    }
}
